import os
import shutil  # 替代Path.copy
import chardet
from pathlib import Path


def detect_encoding(file_path):
    """改进的编码检测函数"""
    with open(file_path, 'rb') as f:
        raw_data = f.read(100000)  # 读取前100KB提高准确率
        return chardet.detect(raw_data)['encoding']


def safe_convert(content):
    """处理代理字符问题"""
    return content.encode('utf-8', 'surrogateescape').decode('utf-8', 'replace')


def convert_to_utf8(src_folder, dest_folder, log_file='conversion.log'):
    """增强版转换函数"""
    dest_path = Path(dest_folder)
    dest_path.mkdir(exist_ok=True)

    with open(log_file, 'w', encoding='utf-8') as log:
        for file_path in Path(src_folder).rglob('*.txt'):
            try:
                # 跳过已转换的错误文件
                if file_path.name.startswith('ERROR_'):
                    continue

                # 检测编码
                encoding = detect_encoding(file_path)
                if not encoding:
                    raise ValueError("无法检测编码")

                # 读取内容
                with open(file_path, 'r', encoding=encoding, errors='surrogateescape') as f:
                    content = f.read()

                # 处理特殊字符
                cleaned_content = safe_convert(content)

                # 构建输出路径（保持目录结构）
                relative_path = file_path.relative_to(src_folder)
                output_path = dest_path / relative_path
                output_path.parent.mkdir(parents=True, exist_ok=True)

                # 写入UTF-8
                with open(output_path, 'w', encoding='utf-8', errors='strict') as f_out:
                    f_out.write(cleaned_content)

                log.write(f"成功转换: {relative_path} ({encoding} → UTF-8)\n")

            except Exception as e:
                # 错误处理
                error_filename = f"ERROR_{file_path.name}"
                shutil.copy(file_path, dest_path / error_filename)  # 使用shutil复制
                log.write(f"转换失败: {relative_path} | 错误类型: {type(e).__name__} | 详情: {str(e)}\n")


if __name__ == "__main__":
    src = input("请输入源文件夹路径：").strip()
    dest = input("请输入目标文件夹路径：").strip()

    try:
        convert_to_utf8(src, dest)
        print(f"转换完成！请查看 {dest} 文件夹中的结果")
    except Exception as e:
        print(f"严重错误: {str(e)}")