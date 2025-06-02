<template>
  <div class="selection-icon" :style="style">
    <i class="fas fa-question-circle">
      <img src="../../assets/images/logo.png" alt="..." style="width: 20px; height: 20px; position:absolute; top: 2px; left: 2px;">
    </i>
  </div>
</template>

<script setup>
defineProps({
  style: Object
})
</script>

<style scoped>
.selection-icon {
  position: fixed;
  width: 24px;
  height: 24px;
  background: white;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 100;
  color: #6b5deb;
  font-size: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.selection-icon:hover {
  transform: scale(1.1);
  box-shadow: 0 3px 10px rgba(0,0,0,0.3);
  color: #4a4ae6;
}
</style>