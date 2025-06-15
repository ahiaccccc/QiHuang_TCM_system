import os
import shutil
import mysql.connector
from mysql.connector import Error


def get_book_names_from_db():
    """从数据库获取所有书名"""
    names = set()
    try:
        conn = mysql.connector.connect(
            host="222.206.4.166",
            port=9006,
            user="qihuang",
            password="qihuang",
            database="qihuangdb"
        )
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM book")
        for (name,) in cursor:
            # 清洗书名：去除空格和特殊符号
            clean_name = name.strip().replace(' ', '').replace('　', '')
            names.add(clean_name)
        return names
    except Error as e:
        print(f"数据库错误: {e}")
        return None
    finally:
        if 'conn' in locals() and conn.is_connected():
            cursor.close()
            conn.close()


def copy_matching_files(source_dir, target_dir, names):
    """复制匹配文件并保持文件名结构"""
    Path(target_dir).mkdir(parents=True, exist_ok=True)

    total = 0
    for filename in Path(source_dir).rglob('*.txt'):
        # 预处理文件名：去除编号和特殊字符
        clean_filename = filename.stem.split('-', 1)[-1]  # 去掉开头的编号
        clean_filename = clean_filename.replace(' ', '')

        # 检查是否包含任意书名
        for name in names:
            if name in clean_filename:
                target_path = Path(target_dir) / filename.name
                shutil.copy(filename, target_path)
                total += 1
                break
    return total


def main():
    # 配置路径
    source_folder = "books"
    target_folder = "books_clean"

    # 获取书名列表
    book_names = get_book_names_from_db()
    if not book_names:
        print("无法获取书名列表")
        return

    # 执行复制
    copied_count = copy_matching_files(source_folder, target_folder, book_names)
    print(f"操作完成！成功复制 {copied_count} 个文件")

    # 生成未匹配名单
    check_unmatched_files(source_folder, book_names)


def check_unmatched_files(source_dir, names):
    """生成未匹配文件报告"""
    unmatched = []
    for filename in Path(source_dir).rglob('*.txt'):
        clean_name = filename.stem.split('-', 1)[-1].replace(' ', '')
        if not any(name in clean_name for name in names):
            unmatched.append(filename.name)

    if unmatched:
        with open("unmatched_files.log", 'w', encoding='utf-8') as f:
            f.write("以下文件未能匹配到书名:\n")
            f.write('\n'.join(unmatched))
        print(f"发现 {len(unmatched)} 个未匹配文件，详见 unmatched_files.log")


if __name__ == "__main__":
    from pathlib import Path

    main()