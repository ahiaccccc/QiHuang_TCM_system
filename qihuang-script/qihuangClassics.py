import pymysql
import os
import re
from pathlib import Path

# 数据库配置
db_config = {
    'host': '222.206.4.166',
    'port': 9006,
    'user': 'qihuang',
    'password': 'qihuang',
    'database': 'qihuangdb',
    'charset': 'utf8mb4'
}


def extract_book_name(filename):
    """处理带编号的文件名，返回标准化书名"""
    # 使用正则去除数字前缀和短横线，例如："656-类证普济本事方续集.txt" → "类证普济本事方续集"
    match = re.match(r'^\d+-(?P<name>.+?)\.txt$', filename)
    if match:
        return match.group('name').strip()
    return filename.replace('.txt', '').strip()


def process_files(folder_path):
    # 创建数据库连接
    conn = pymysql.connect(**db_config)

    try:
        with conn.cursor() as cursor:
            # 遍历所有txt文件
            txt_files = Path(folder_path).glob('*.txt')
            for file_path in txt_files:
                # 提取标准书名
                book_name = extract_book_name(file_path.name)

                # 查询book_id
                cursor.execute("SELECT book_id FROM book WHERE name = %s", (book_name,))
                result = cursor.fetchone()
                if not result:
                    print(f"未找到匹配书籍：{book_name} ({file_path.name})")
                    continue
                book_id = result[0]

                # 读取并解析文件内容
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 使用正则分割章节
                chapters = re.split(r'<目录>\s*', content)
                insert_data = []

                for chapter in chapters[1:]:  # 跳过第一个空元素
                    # 提取篇名和内容
                    title_match = re.search(r'<篇名>(.*?)\s*\n', chapter)
                    if not title_match:
                        continue

                    title = title_match.group(1).strip()
                    content_text = re.sub(r'^内容：', '',
                                          chapter.split('</篇名>')[-1],
                                          flags=re.MULTILINE
                                          ).split('<目录>')[0].strip()

                    insert_data.append((book_id, title, content_text))

                # 批量插入
                if insert_data:
                    cursor.executemany(
                        "INSERT INTO classics (book_id, title, original_text) VALUES (%s, %s, %s)",
                        insert_data
                    )
                    print(f"已插入 {len(insert_data)} 条记录：{book_name}")
                    conn.commit()

    except Exception as e:
        print(f"处理异常：{str(e)}")
        conn.rollback()
    finally:
        conn.close()


if __name__ == "__main__":
    # 使用示例
    txt_folder = "books_clean"  # 替换为实际路径
    process_files(txt_folder)
    print("处理完成！请检查数据库数据")