<template>
  <div>
    <el-container>
      <!--    Aside  -->
      <el-aside :width="asideWidth" style="min-height: 100vh; background-color: #001529">
        <div style="height: 60px; color: white; display: flex; align-items: center; justify-content: center">
          <img src="@/assets/logo1.png" alt="" style="width: 40px; height: 40px">
          <span class="logo-title" v-show="!isCollapse">management sys</span>
        </div>

        <el-menu :default-openeds="['info']" :collapse="isCollapse" :collapse-transition="false" router background-color="#001529" text-color="rgba(255, 255, 255, 0.65)"
                 active-text-color="#fff" style="border: none" :default-active="$route.path">
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">System Main Page</span>
          </el-menu-item>
          <el-submenu index="info" >
            <template slot="title">
              <i class="el-icon-menu"></i>
              <span>Info Management</span>
            </template>
            <el-menu-item index="/user" v-if="user.role === 'admin'">User Info</el-menu-item>
            <el-menu-item index="/news">News Info</el-menu-item>
            <el-menu-item index="/notice" v-if="user.role === 'admin'">System Notice</el-menu-item>
            <el-menu-item index="/logs" v-if="user.role === 'admin'">System Logs</el-menu-item>
            <el-menu-item index="/charts" v-if="user.role === 'admin'">Charts</el-menu-item>
            <el-menu-item index="/orders">Orders</el-menu-item>
          </el-submenu>
        </el-menu>

      </el-aside>

      <el-container>
        <!-- Header -->
        <el-header>

          <i :class="collapseIcon" style="font-size: 26px" @click="handleCollapse"></i>
          <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-left: 20px">
            <el-breadcrumb-item :to="{ path: '/' }">Main Page</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
          </el-breadcrumb>

          <div style="flex: 1; width: 0; display: flex; align-items: center; justify-content: flex-end">
            <i class="el-icon-quanping" style="font-size: 26px" @click="handleFull"></i>
            <el-dropdown placement="bottom">
              <div style="display: flex; align-items: center; cursor: default">
                <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="" style="width: 40px; height: 40px; border-radius: 50%; margin: 0 5px">
                <span>{{ user.name }}</span>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="$router.push('/person')">Personal Info</el-dropdown-item>
                <el-dropdown-item @click.native="$router.push('/password')">Change Password</el-dropdown-item>
                <el-dropdown-item @click.native="logout">Log Out</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

        </el-header>

        <!-- Main -->
        <el-main>
          <router-view @update:user="updateUser" />
        </el-main>

      </el-container>


    </el-container>
  </div>
</template>

<script>

import user from "@/views/manager/User";

export default {
  name: 'HomeView',
  data() {
    return {
      isCollapse: false,  // not collapse
      asideWidth: '200px',
      collapseIcon: 'el-icon-s-fold',
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
    }
  },
  //trigger after page loaded
  mounted() {
    if (!this.user.id) {   // if current browser don't have user info
      this.$router.push('/login')
    }
  },
  methods: {
    //parent Get the data passed by the child and update the data of the current page
    updateUser(user) {
      //Let the parent object have no relationship with the child object
      this.user = JSON.parse(JSON.stringify(user))
    },
    logout() {
      localStorage.removeItem('honey-user')  //Clear current token and user data
      this.$router.push('/login')
    },
    handleFull() {
      document.documentElement.requestFullscreen()
    },
    handleCollapse() {
      this.isCollapse = !this.isCollapse
      this.asideWidth = this.isCollapse ? '64px' : '200px'
      this.collapseIcon = this.isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'
    }
  }
}
</script>

<style>
.el-menu--inline {
  background-color: #000c17 !important;
}
.el-menu--inline .el-menu-item {
  background-color: #000c17 !important;
  padding-left: 49px !important;
}
.el-menu-item:hover, .el-submenu__title:hover {
  color: #fff !important;
}
.el-submenu__title:hover i {
  color: #fff !important;
}
.el-menu-item:hover i {
  color: #fff !important;
}
.el-menu-item.is-active {
  background-color: #1890ff !important;
  border-radius: 5px !important;
  width: calc(100% - 8px);
  margin-left: 4px;
}
.el-menu-item.is-active i, .el-menu-item.is-active .el-tooltip{
  margin-left: -4px;
}
.el-menu-item {
  height: 40px !important;
  line-height: 40px !important;
}
.el-submenu__title {
  height: 40px !important;
  line-height: 40px !important;
}
.el-submenu .el-menu-item {
  min-width: 0 !important;
}
.el-menu--inline .el-menu-item.is-active {
  padding-left: 45px !important;
}
/*.el-submenu__icon-arrow {*/
/*  margin-top: -5px;*/
/*}*/

.el-aside {
  transition: width .3s;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
}
.logo-title {
  margin-left: 5px;
  font-size: 20px;
  transition: all .3s;   /* 0.3s */
}
.el-header {
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  display: flex;
  align-items: center;
}
</style>
