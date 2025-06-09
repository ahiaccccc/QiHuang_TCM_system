// src/store/admin.js
import { defineStore } from 'pinia';
import { 
  getAdminCollections, 
  deleteCollection,
  getAdminQaSessions,
  deleteQaSession ,
  getAdminQaMessages,
  deleteQaMessage
} from '@/apis/admin-apis';

export const useAdminStore = defineStore('admin', {
  state: () => ({
    // 收藏记录相关
    collections: [],
    totalCollections: 0,
    
    // 对话记录相关
    qaSessions: [],
    totalQaSessions: 0,
    
    // 分页参数
    currentPage: 0,
    pageSize: 10,

    qaMessages: [],
    
    // 筛选参数
    filterUserId: null,
    filterClassicId: null
  }),

  actions: {
    // 加载收藏记录
    async loadCollections() {
      const params = {
        userId: this.filterUserId,
        classicId: this.filterClassicId,
        page: this.currentPage,
        size: this.pageSize
      };
      
      const res = await getAdminCollections(params);
      this.collections = res.content;
      this.totalCollections = res.totalElements;
    },

    // 加载对话记录
    async loadQaSessions() {
      const params = {
        userId: this.filterUserId,
        classicId: this.filterClassicId,
        page: this.currentPage,
        size: this.pageSize
      };
      
      const res = await getAdminQaSessions(params);
      this.qaSessions = res.content;
      this.totalQaSessions = res.totalElements;
    },


    // 删除对话记录
    async removeQaSession(id) {
      await deleteQaSession(id);
      await this.loadQaSessions();
    },
    // 加载消息
        async loadQaMessages(sessionId) {
      try {
        const res = await getAdminQaMessages(sessionId);
        console.log('加载对话消息:', res);
        // 确保返回数据结构正确
        this.qaMessages = res.map(msg => ({
          id: msg.id,  // 确保使用正确的ID字段
          session: { id: msg.session?.id },  // 处理嵌套session对象
          role: msg.role,
          content: msg.content,
          feedback: msg.feedback
        }));
      } catch (error) {
        console.error('加载消息失败:', error);
        this.qaMessages = [];
      }
    },

    // 新增方法：删除消息
    async removeQaMessage(messageId) {
      try {
        await deleteQaMessage(messageId);
        // 安全过滤方法
        this.qaMessages = this.qaMessages.filter(msg => {
          // 添加空值检查
          if (!msg) return false;
          return msg.id !== messageId;
        });
      } catch (error) {
        console.error('删除消息失败:', error);
        throw error;  // 抛出错误供组件处理
      }
    },

    // 分页控制
    setPage(page) {
      this.currentPage = page;
    },
    
    // 筛选参数更新
    updateFilters({ userId, classicId }) {
      this.filterUserId = userId;
      this.filterClassicId = classicId;
      this.currentPage = 0; // 重置到第一页
    }
  }
});