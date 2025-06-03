<template>
  <div class="admin-container">
    <div class="admin-header">
      <h2>典籍问答管理</h2>
      <button @click="$router.push('/')">返回前台</button>
    </div>
    
    <div class="filter-section">
      <input 
        v-model.number="adminStore.filterUserId"
        placeholder="用户ID"
        type="number"
      />
      <input
        v-model.number="adminStore.filterClassicId"
        placeholder="典籍ID"
        type="number"
      />
    </div>

    <div class="tab-section">
      <button
        :class="{ active: activeTab === 'collected' }"
        @click="handleTabChange('collected')"
      >
        收藏记录
      </button>
      <button
        :class="{ active: activeTab === 'qa' }"
        @click="handleTabChange('qa')"
      >
        对话记录
      </button>
    </div>

        <div class="list-section">
      <div v-if="currentData.length === 0" class="empty-tip">
        暂无相关数据
      </div>

      <template v-else>
        <!-- 收藏记录条目 -->
        <template v-if="activeTab === 'collected'">
          <div 
            v-for="item in adminStore.collections"
            :key="item.id"
            class="admin-item"
          >
            <div class="item-info">
              <div class="info-row">
                <span class="label">用户：</span>
                <span class="value">{{ item.userName }} (ID: {{ item.userId }})</span>
              </div>
              <div class="info-row">
                <span class="label">典籍：</span>
                <span class="value">{{ item.classicTitle }} (ID: {{ item.classicId }})</span>
              </div>
            </div>
          </div>
        </template>

        <!-- 对话记录条目 -->
        <template v-if="activeTab === 'qa'">
          <div 
            v-for="item in adminStore.qaSessions"
            :key="item.sessionId"
            class="admin-item"
          >
            <div class="item-info">
              <div class="info-row">
                <span class="label">用户：</span>
                <span class="value">{{ item.userName }} (ID: {{ item.userId }})</span>
              </div>
              <div class="info-row">
                <span class="label">典籍：</span>
                <span class="value">{{ item.classicTitle }} (ID: {{ item.classicId }})</span>
              </div>
              <div class="info-row">
                <span class="label">对话标题：</span>
                <span class="value">{{ item.sessionTitle }}</span>
              </div>
              <div class="info-row">
                <span class="label">创建时间：</span>
                <span class="value">
                  {{ new Date(item.createdAt).toLocaleString() }}
                </span>
              </div>
              
              <div class="info-row">
                <button 
                  class="toggle-btn"
                  @click="toggleMessages(item.sessionId)"
                >
                  {{ expandedSession === item.sessionId ? '隐藏消息' : '查看消息' }}
                </button>
              </div>
            </div>

            <button 
              class="delete-btn"
              @click="deleteItem(item.sessionId)"
            >
              删除会话
            </button>

            <div 
              v-if="expandedSession === item.sessionId" 
              class="message-list"
            >
              <div 
                v-for="msg in adminStore.qaMessages" 
                :key="msg.id"
                class="message-item"
              >
                <div class="message-content">
                  <span class="role-tag">{{ msg.role }}</span>
                  {{ msg.content }}
                  <span class="feedback">反馈：{{ msg.feedback }}</span>
                </div>
                <button 
                  class="delete-message-btn"
                  @click="deleteMessage(msg.id)"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
        </template>

        <!-- 分页 -->
        <div class="pagination">
          <button @click="prevPage" :disabled="adminStore.currentPage === 0">
            上一页
          </button>
          <span>第 {{ adminStore.currentPage + 1 }} 页 / 共 {{ totalPages }} 页</span>
          <button 
            @click="nextPage" 
            :disabled="adminStore.currentPage >= totalPages - 1"
          >
            下一页
          </button>
        </div>
      </template>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, watch } from 'vue';
import { useAdminStore } from '@/stores/useAdminStore';

const adminStore = useAdminStore();
const activeTab = ref('qa');

// 计算属性
const currentData = computed(() => {
  return activeTab.value === 'collected' 
    ? adminStore.collections 
    : adminStore.qaSessions;
});

const totalPages = computed(() => {
  return activeTab.value === 'collected'
    ? Math.ceil(adminStore.totalCollections / adminStore.pageSize)
    : Math.ceil(adminStore.totalQaSessions / adminStore.pageSize);
});

// 操作处理
const handleTabChange = (tab) => {
  activeTab.value = tab;
  adminStore.currentPage = 0;
  loadData();
};

const loadData = () => {
  if (activeTab.value === 'collected') {
    adminStore.loadCollections();
  } else {
    adminStore.loadQaSessions();
  }
};

const deleteItem = (id) => {
  if (activeTab.value === 'collected') {
    adminStore.removeCollection(id);
  } else {
    adminStore.removeQaSession(id);
  }
};
// 新增状态
const expandedSession = ref(null);

// 展开/收起消息
const toggleMessages = async (sessionId) => {
  if (expandedSession.value === sessionId) {
    expandedSession.value = null;
    adminStore.qaMessages = [];
  } else {
    expandedSession.value = sessionId;
    await adminStore.loadQaMessages(sessionId);
  }
};

// 删除消息
const deleteMessage = async (messageId) => {
  try {
    await adminStore.removeQaMessage(messageId);
    // 显示操作反馈
    alert('消息删除成功');
  } catch (error) {
    console.error('删除操作失败:', error);
    alert('删除失败，请检查控制台日志');
  }
};
// 分页控制
const prevPage = () => {
  if (adminStore.currentPage > 0) {
    adminStore.currentPage--;
    loadData();
  }
};

const nextPage = () => {
  if (adminStore.currentPage < totalPages.value - 1) {
    adminStore.currentPage++;
    loadData();
  }
};

// 初始化加载
loadData();

// 监听筛选参数变化
watch(
  () => [adminStore.filterUserId, adminStore.filterClassicId],
  () => loadData()
);
</script>


<style scoped>
/* 保持之前提供的样式不变 */
.admin-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.admin-container::before {
  content: "";
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("https://c.animaapp.com/m9pqi0c3GNaMeT/img/back.png");
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.3; /* 调整透明度 */
  z-index: -1;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.tab-section button {
  padding: 8px 16px;
  margin-right: 10px;
  background: #f0f0f0;
  border: none;
  cursor: pointer;
}

.tab-section button.active {
  background: #007bff;
  color: white;
}

.admin-list {
  margin-top: 20px;
}

.admin-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.9);
  margin-bottom: 12px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.delete-btn {
  background: #ff4444;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
  border: none;
  cursor: pointer;
}

.delete-btn:hover {
  background: #cc0000;
}

.pagination {
  margin-top: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.empty-tip {
  text-align: center;
  color: #666;
  padding: 40px 0;
}
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}
.message-list {
  margin-top: 10px;
  width: 100%;
}

.message-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  margin: 4px 0;
  background: #f8f9fa;
  border-radius: 4px;
}

.message-content {
  flex-grow: 1;
  margin-right: 10px;
}

.feedback {
  color: #666;
  font-size: 0.9em;
  margin-left: 10px;
}

.delete-message-btn {
  background: #ff4444;
  color: white;
  padding: 4px 8px;
  border-radius: 3px;
  border: none;
  cursor: pointer;
}
</style>