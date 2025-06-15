import os
import mysql.connector
from mysql.connector import Error
import re
from typing import Dict


def parse_by_fields(file_path: str) -> Dict[str, str]:
    """基于字段标识的智能解析方法"""
    field_pattern = re.compile(
        r"^(?P<field>书名|作者|朝代|年份)[\s:：]*"  # 匹配字段名和分隔符
        r"(?P<value>.+?)\s*$"  # 捕获值部分
    )

    result = {
        "name": None,
        "author": None,
        "dynasty": None,
        "year": None
    }

    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as f:
            # 最多扫描前50行
            for _ in range(50):
                line = f.readline().strip()
                if not line:
                    continue

                # 字段匹配
                match = field_pattern.match(line)
                if match:
                    field_type = match.group("field")
                    value = match.group("value")

                    # 根据字段类型存储
                    if field_type == "书名":
                        result["name"] = value
                    elif field_type == "作者":
                        result["author"] = value.strip("　")  # 去除全角空格
                    elif field_type == "朝代":
                        result["dynasty"] = value
                    elif field_type == "年份":
                        result["year"] = value

                # 提前退出条件
                if all(result.values()):
                    break

    except Exception as e:
        print(f"文件解析失败: {file_path} - {str(e)}")
        return None

    # 验证必填字段
    required_fields = ["name", "author", "dynasty", "year"]
    if any(result[field] is None for field in required_fields):
        missing = [f for f in required_fields if result[f] is None]
        print(f"字段缺失 {file_path}: {missing}")
        return None

    return result


def db_connection():
    """创建数据库连接"""
    try:
        conn = mysql.connector.connect(
            host="222.206.4.166",
            port=9006,
            user="qihuang",
            password="qihuang",
            database="qihuangdb",
            charset='utf8mb4'
        )
        return conn
    except Error as e:
        print(f"数据库连接失败: {e}")
        return None


def create_table(conn):
    """创建数据表（如果不存在）"""
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS book (
        book_id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        author VARCHAR(255),
        dynasty VARCHAR(50),
        year VARCHAR(50),
        INDEX idx_name (name),
        INDEX idx_dynasty (dynasty)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
    """
    try:
        cursor = conn.cursor()
        cursor.execute(create_table_sql)
        conn.commit()
    except Error as e:
        print(f"创建表失败: {e}")
    finally:
        if cursor: cursor.close()


def save_to_db(conn, data):
    """将数据存入数据库"""
    if not data: return

    insert_sql = """
    INSERT INTO book (name, author, dynasty, year)
    VALUES (%s, %s, %s, %s)
    """
    try:
        cursor = conn.cursor()
        cursor.execute(insert_sql, (
            data['name'],
            data['author'],
            data['dynasty'],
            data['year']
        ))
        conn.commit()
    except Error as e:
        print(f"插入数据失败: {e}")
        conn.rollback()
    finally:
        if cursor: cursor.close()


def main(folder_path):
    # 初始化数据库连接
    conn = db_connection()
    if not conn: return

    create_table(conn)

    # 遍历文件夹
    processed = 0
    for filename in os.listdir(folder_path):
        if filename.endswith('.txt'):
            file_path = os.path.join(folder_path, filename)
            data = parse_by_fields(file_path)
            if data:
                save_to_db(conn, data)
                processed += 1

    print(f"处理完成！成功处理 {processed} 个文件")
    conn.close()


if __name__ == "__main__":
    # 使用示例
    folder_path = "books"

    # 检查路径有效性
    if os.path.isdir(folder_path):
        main(folder_path)
    else:
        print("错误：输入的路径不是有效的文件夹")