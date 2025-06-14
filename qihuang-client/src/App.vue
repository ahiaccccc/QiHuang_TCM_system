<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <n-loading-bar-provider>
    <n-dialog-provider>
      <n-message-provider>
        <router-view />
      </n-message-provider>
    </n-dialog-provider>
  </n-loading-bar-provider>
</template>

<style>
body,
html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  /* overflow-x: hidden; */
}
</style>
