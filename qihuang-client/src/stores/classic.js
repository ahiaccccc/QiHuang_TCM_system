import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchClassics,saveClassic,deleteClassic as apiDeleteClassic } from '@/apis/classic-apis'
import { getUserCollected,getCollectedStatus, toggleCollected } from '@/apis/collected' 

export const useClassicStore = defineStore('classic', () => {
  const classics = ref([])
  const totalPages = ref(1)
  const collectedBooks = ref([])

  const loadClassics = async (bookId, params) => {
    const data = await fetchClassics(bookId, params)
    classics.value = data.content
    totalPages.value = data.totalPages
  }
  const loadCollectedBooks = async (userId) => {
    const data = await getUserCollected(userId)
    collectedBooks.value = data.data
  }
   // 新增创建典籍方法
  const createClassic = async (bookId, classic) => {
   await saveClassic(bookId, classic)
  
  }
  
  // 新增更新典籍方法
  const updateClassic = async (bookId, classicId, classic) => {
    await saveClassic(bookId, { ...classic, id: classicId })
  }
  
  // 新增删除典籍方法（更完整实现）
  const deleteClassic = async (bookId, classicId) => {
    await apiDeleteClassic(bookId, classicId);
  }

  return { classics, totalPages, loadClassics, loadCollectedBooks, createClassic, updateClassic, deleteClassic }
})

export const useCollectedStore = defineStore('collected', {
  state: () => ({
    isCollected: false,
  }),
  actions: {
    async fetchCollectedStatus(userId, classicId) {
      const res = await getCollectedStatus(userId, classicId)
      this.isCollected = res
    },
    async toggle(userId, classicId, title) {
      await toggleCollected(userId, classicId, title)
      this.isCollected = !this.isCollected
    }
  }
})