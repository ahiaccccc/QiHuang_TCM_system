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
