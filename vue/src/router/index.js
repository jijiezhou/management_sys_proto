import Vue from 'vue'
import VueRouter from 'vue-router'

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
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
      { path: '403', name: 'Auth', meta: { name: '无权限' }, component: () => import('../views/manager/Auth') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'person', name: 'Person', meta: { name: '个人信息' }, component: () => import('../views/manager/Person') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'news', name: 'News', meta: { name: '新闻信息' }, component: () => import('../views/manager/News') },
      { path: 'newsDetail', name: 'NewsDetail', meta: { name: '新闻详情' }, component: () => import('../views/manager/NewsDetail') },
      { path: 'notice', name: 'Notice', meta: { name: '系统公告' }, component: () => import('../views/manager/Notice') },
      { path: 'logs', name: 'Logs', meta: { name: '系统日志' }, component: () => import('../views/manager/Logs') },
      { path: 'charts', name: 'Charts', meta: { name: '数据统计' }, component: () => import('../views/manager/Charts') },
      { path: 'orders', name: 'Orders', meta: { name: '订单管理' }, component: () => import('../views/manager/Orders') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: '404', meta: { name: '无法访问' }, component: () => import('../views/404.vue') }, //other situations we not consider all 404
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
  if (user.role !== '管理员' && adminPaths.includes(to.path)) {
    //if current user not admin and want to visit the pages only open to admin, then route guide to page 403
    next('/403')
  } else {
    next()
  }
})

export default router
