<template>
  <div class="selection-popup" :style="style">
    <div class="popup-header">
      <h3>针对选中文本提问</h3>
      <button @click="$emit('cancel')" class="close-btn">
        <i class="fas fa-times"></i>
      </button>
    </div>
    
    <div class="selected-text-preview">
      "{{ selectedText }}"
    </div>
    
    <div class="input-group">
      <textarea
        v-model="question"
        placeholder="输入你的问题..."
        @keyup.enter="submit"
      ></textarea>
    </div>
    
    <div class="popup-actions">
      <button class="btn-cancel" @click="$emit('cancel')">
        取消
      </button>
      <button class="btn-ask" @click="submit">
        提问
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  style: Object,
  selectedText: String
})

const emit = defineEmits(['ask', 'cancel'])
const question = ref('')

const submit = () => {
  const fullQuestion = props.selectedText 
    ? `关于选中的内容："${props.selectedText}"，${question.value || '我的问题是：'}`
    : question.value || '请解释这段话'
  
  emit('ask', fullQuestion)
}
</script>

<style scoped>
.selection-popup {
  position: fixed;
  width: 320px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.2);
  z-index: 1000;
  padding: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.popup-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 16px;
  padding: 4px;
}

.close-btn:hover {
  color: #666;
}

.selected-text-preview {
  background-color: #f8f9fa;
  border-left: 3px solid #6b5deb;
  padding: 10px 12px;
  margin-bottom: 15px;
  border-radius: 0 4px 4px 0;
  font-size: 14px;
  color: #555;
  line-height: 1.5;
  max-height: 100px;
  overflow-y: auto;
}

.input-group textarea {
  width: 92%;
  height: 80px;
  padding: 4%;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  margin-bottom: 15px;
}

.input-group textarea:focus {
  outline: none;
  border-color: #6b5deb;
  box-shadow: 0 0 0 2px rgba(107, 93, 235, 0.2);
}

.popup-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.popup-actions button {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f5f5f7;
  color: #666;
  border: 1px solid #e0e0e0;
}

.btn-cancel:hover {
  background: #ebebed;
}

.btn-ask {
  background: #6b5deb;
  color: white;
  border: none;
}

.btn-ask:hover {
  background: #5a4bd8;
}
</style>