<template>
  <div>
    <div style="box-shadow: 0 0 10px rgba(0,0,0,.1); padding: 10px 20px; border-radius: 5px; margin-bottom: 10px">
      Hi，{{ user.name }}，fighting！
    </div>

    <div style="display: flex">
      <el-card style="width: 100%;">
        <div slot="header" class="clearfix">
          <span>Management System</span>
        </div>
        <div>
          Management System Prototype
          <div style="margin-top: 20px">
            <div style="margin: 10px 0"><strong>Theme color</strong></div>
            <el-button type="primary">primary</el-button>
            <el-button type="success">success</el-button>
            <el-button type="warning">warning</el-button>
            <el-button type="danger">danger</el-button>
            <el-button type="info">info</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div style="display: flex; margin: 15px 0">
      <el-card style="width: 50%; margin-right: 10px">
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold">System Notice</div>
        <el-timeline style="padding: 0">
          <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time" placement="top">
            <el-card>
              <h4>{{ item.title }}</h4>
              <div v-html="item.content"></div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card style="width: 50%">
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold">System Notice</div>
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item v-for="(item, index) in notices" :key="item.id" :name="index + ''">
            <template slot="title">
              <div style="display: flex; align-items: center; width: 100%">
                <h4 style="flex: 1">{{ item.title }}</h4>
                <div style="width: 150px; color: #888">{{ item.time }}</div>
              </div>
            </template>
            <div v-html="item.content"></div>
          </el-collapse-item>
        </el-collapse>
      </el-card>
    </div>

  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      notices: [],
      activeName: '0',
    }
  },
  created() {
    this.loadNotice()
  },
  methods: {
    loadNotice() {
      this.$request.get('/notice/selectUserData').then(res => {
        this.notices = res.data
      })
    }
  }
}
</script>

<style scoped>

</style>
