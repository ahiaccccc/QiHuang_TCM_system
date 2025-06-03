export function getSelectedText() {
  const selection = window.getSelection()
  return selection.toString().trim()
}

export function getSelectionPosition() {
  const selection = window.getSelection()
  if (!selection.rangeCount) return null
  
  const range = selection.getRangeAt(0)
  const rect = range.getBoundingClientRect()
  
  // 获取选中文本的结束位置
  const endNode = range.endContainer
  const endOffset = range.endOffset
  
  // 创建临时范围定位到结束位置
  const tempRange = document.createRange()
  tempRange.setStart(endNode, endOffset)
  tempRange.setEnd(endNode, endOffset)
  
  const endRect = tempRange.getBoundingClientRect()
  
  return {
    x: endRect.right,
    y: endRect.top
  }
}