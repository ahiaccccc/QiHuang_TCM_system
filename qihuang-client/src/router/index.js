import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

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
      component: () => import('../views/User/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/User/RegisterView.vue'),
    },
    {
      path: '/forgetPassword',
      name: 'ForgetPassword',
      component: () => import('../views/User/ForgetPassword.vue'),
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('../views/User/ProfileView.vue'),
    },


    {
      path: '/test',
      name: 'testConnection',
      component: () => import('../views/testConnection.vue'),
    },
    {
      path: '/classics',
      name: 'classics',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Classic/ClassicsView.vue'),
    },
    {
      path: '/classic-detail',
      name: 'classic-detail',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Classic/ClassicDetail.vue'),
    },
    {
      path: '/ChatTest',
      name: 'ChatTest',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Chat/ChatTest.vue'),
    },
    {
      path: '/ChatView',
      name: 'ChatView',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Chat/ChatView.vue'),
    },
    {
      path: '/ConversationDetail',
      name: 'ConversationDetail',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Chat/ConversationDetail.vue'),
    },

  ],
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    next('/login');
  } else {
    next();
  }
});



export default router
