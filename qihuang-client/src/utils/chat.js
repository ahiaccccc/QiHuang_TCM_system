export const scrollToBottom = (element) => {
  if (element) {
    element.scrollTop = element.scrollHeight;
  }
};

export const handleCopy = (content) => {
  return navigator.clipboard.writeText(content)
    .then(() => ({ success: true }))
    .catch(() => ({ success: false }));
};

export const validateTitle = (title) => {
  if (!title || !title.trim()) {
    return { valid: false, message: "标题不能为空！" };
  }
  if (title.length > 50) {
    return { valid: false, message: "标题长度不能超过50个字符！" };
  }
  return { valid: true };
};
