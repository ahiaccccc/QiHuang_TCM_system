import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ClassicsView from '@/views/Classic/ClassicsView.vue'
import ClassicDetail from '@/views/Classic/ClassicDetail.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/admin-classics',
      name: 'admin-classics',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Classic/AdminClassic.vue'),
    },
      {
    path: '/books',
    name: 'Books',
    component: () => import('@/views/Classic/BooksView.vue'),
    props: route => ({
      bookId: route.query.bookId // 将书籍ID作为prop传递
    })
  },
  {
    path: '/books/:bookId/classics',
    name: 'Classics',
    component: () => import('@/views/Classic/ClassicsView.vue'),
    props: route => ({
      bookId: route.params.bookId // 将书籍ID作为prop传递
    })
  },
  {
    path: '/books/:bookId/classics/:classicId',
    name: 'ClassicDetail',
    component: () => import('@/views/Classic/ClassicDetail.vue'),
          props: route => ({
        bookId: route.params.bookId, // 添加bookId参数
        classicId: route.params.classicId // 明确命名
      })
  },
    {
      path: '/admin-QA',
      name: 'admin-QA',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Classic/AdminQA.vue'),
    },
  ],
  
})

export default router
