<template>
  <div>
    <div style="box-shadow: 0 0 10px rgba(0,0,0,.1); padding: 10px 20px; border-radius: 5px; margin-bottom: 10px">
      早安，{{ user.name }}，祝你开心每一天！
    </div>

    <div style="display: flex">
      <el-card style="width: 100%;">
        <div slot="header" class="clearfix">
          <span>青哥哥带你做毕设2024</span>
        </div>
        <div>
          2024毕设正式开始了！青哥哥带你手把手敲出来！
          <div style="margin-top: 20px">
            <div style="margin: 10px 0"><strong>主题色</strong></div>
            <el-button type="primary">按钮</el-button>
            <el-button type="success">按钮</el-button>
            <el-button type="warning">按钮</el-button>
            <el-button type="danger">按钮</el-button>
            <el-button type="info">按钮</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div style="display: flex; margin: 15px 0">
      <el-card style="width: 50%; margin-right: 10px">
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold">系统公告</div>
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
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold">系统公告</div>
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

    <div>
      <el-table-column>
        <template v-slot="scope">
          <el-upload
              action="https://localhost:9090/file/upload"
              :headers="{token: user.token}"
              :show-file-list="false"
              :on-success="(row, res, file, fileList) => handleTableFileUpload(scope.row, res, file, fileList)">
            <el-button size="mini" type="primary">Upload</el-button>
          </el-upload>
        </template>
      </el-table-column>
      <el-table-column label="file_upload">
        <template v-slot="scope">
          <el-image v-if="scope.row.avatar":src="scope.row.avatar" style="width: 50px; height: 50px"></el-image>
        </template>
      </el-table-column>
    </div>

    <div style="display: flex; margin: 10px 0">
      <el-card style="width: 50%; margin-right: 10px">
        <div slot="header" class="clearfix">
          File upload/download
        </div>
        <div>
          <el-upload
              accept="png"
              action="https://localhost:9090/file/upload"
              :file-list="fileList"
              :headers="{token: user.token }"
              list-type="picture"
              :on-success="handleFileUpload">
            <el-button size="small" type="primary">Upload</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
          </el-upload>
        </div>
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
      fileList: [],
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      url: ''
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
    },
    handleFileUpload(response, file, fileList) {
      this.fileList = fileList
    },
    handleTableFileUpload(row, res, file, fileList) {
      row.avatar = file.response.data
      //reset
      request.put('/user/update', row).then(res => {
        if (res.code == '200'){
          this.$message.success('upload success')
        }else{
          this.$message.err(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
