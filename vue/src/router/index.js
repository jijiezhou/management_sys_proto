import Vue from 'vue'
import VueRouter from 'vue-router'

//Solve the problem of vue-router in the navigation bar or bottom navigation tabBar reporting errors when frequently clicking the menu in version 3.0 or above.
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/home',  // redirect to home page
    children: [
      { path: '403', name: 'Auth', meta: { name: 'Unauth' }, component: () => import('../views/manager/Auth') },
      { path: 'home', name: 'Home', meta: { name: 'Home Page' }, component: () => import('../views/manager/Home') },
      { path: 'user', name: 'User', meta: { name: 'User Info' }, component: () => import('../views/manager/User') },
      { path: 'person', name: 'Person', meta: { name: 'Personal Info' }, component: () => import('../views/manager/Person') },
      { path: 'password', name: 'Password', meta: { name: 'Change Password' }, component: () => import('../views/manager/Password') },
      { path: 'news', name: 'News', meta: { name: 'News Info' }, component: () => import('../views/manager/News') },
      { path: 'newsDetail', name: 'NewsDetail', meta: { name: 'News Detail' }, component: () => import('../views/manager/NewsDetail') },
      { path: 'notice', name: 'Notice', meta: { name: 'System Notice' }, component: () => import('../views/manager/Notice') },
      { path: 'logs', name: 'Logs', meta: { name: 'System Logs' }, component: () => import('../views/manager/Logs') },
      { path: 'charts', name: 'Charts', meta: { name: 'Charts' }, component: () => import('../views/manager/Charts') },
      { path: 'orders', name: 'Orders', meta: { name: 'Orders' }, component: () => import('../views/manager/Orders') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: 'Login' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: 'Register' }, component: () => import('../views/Register.vue') },
  { path: '*', name: '404', meta: { name: '404 Not Found' }, component: () => import('../views/404.vue') }, //other situations we not consider all 404
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  // to -> route info to
  // from -> route info from
  // next -> router helper function
  //admin path
  let adminPaths = ['/user']
  let user = JSON.parse(localStorage.getItem('honey-user') || '{}')

  //If the currently logged-in user is not an administrator, and the current arrival path is a path that only administrators
  // have permission to access, then at this time I will let the user go to a page without permission and prevent him from accessing the actual page.
  if (user.role !== 'admin' && adminPaths.includes(to.path)) {
    //if current user not admin and want to visit the pages only open to admin, then route guide to page 403
    next('/403')
  } else {
    next()
  }
})

export default router
