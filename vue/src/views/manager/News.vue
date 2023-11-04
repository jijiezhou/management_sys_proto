<template>
  <div>
    <div>
      <el-input style="width: 200px" placeholder="query title" v-model="title"></el-input>
      <el-button type="primary" style="margin-left: 10px" @click="load(1)">Query</el-button>
      <el-button type="info" @click="reset">Reset</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" plain @click="handleAdd">Insert</el-button>
      <el-button type="danger" plain @click="delBatch">DeleteBatch</el-button>
    </div>

    <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="id" label="id" width="70" align="center"></el-table-column>
      <el-table-column prop="title" label="title"></el-table-column>
      <el-table-column prop="description" label="description"></el-table-column>
      <el-table-column prop="content" label="content">
        <template v-slot="scope">
          <el-button @click="showContent(scope.row.content)" size="mini">Show Content</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="detail">
        <template v-slot="scope">
          <el-button @click="$router.push('/newsDetail?id=' + scope.row.id)" size="mini">NewsDetail</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="author" label="author"></el-table-column>
      <el-table-column prop="time" label="time"></el-table-column>
      <el-table-column label="operation" align="center" width="180">
        <template v-slot="scope">
          <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">Edit</el-button>
          <el-button size="mini" type="danger" plain @click="del(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 10px 0">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="News Information" :visible.sync="formVisible" width="60%" @close="closeDialog" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="title" prop="title">
          <el-input v-model="form.title" placeholder="title"></el-input>
        </el-form-item>
        <el-form-item label="description" prop="description">
          <el-input v-model="form.description" placeholder="description"></el-input>
        </el-form-item>
        <el-form-item label="content" prop="content">
          <div id="editor"></div>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Content" :visible.sync="formVisible1" width="60%">
      <el-card class="w-e-text">
        <div v-html="content"></div>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="formVisible1 = false">Confirm</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>

import E from "wangeditor"
import hljs from 'highlight.js'

export default {
  name: "News",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 5,  // 每页显示的个数
      username: '',
      title: '',
      total: 0,
      formVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
        ]
      },
      ids: [],
      editor: null,
      content: '',
      formVisible1: false,
    }
  },
  created() {
    this.load()
  },
  methods: {
    showContent(content) {
      this.content = content
      this.formVisible1 = true
    },
    closeDialog() {
      // 销毁编辑器
      this.editor.destroy()
      this.editor = null
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('Please select data')
        return
      }
      this.$confirm('您确认批量删除这些数据吗？Are you sure delete data by batch?', 'Confirm', {type: "warning"}).then(response => {
        this.$request.delete('/news/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {   // operation success
            this.$message.success('delete success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // log error info
          }
        })
      }).catch(() => {})
    },
    //All row data currently selected, and generate corresponding array of ids
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },
    del(id) {
      this.$confirm('Are you sure to delete?', 'Confirm', {type: "warning"}).then(response => {
        this.$request.delete('/news/delete/' + id).then(res => {
          if (res.code === '200') {   // operation success
            this.$message.success('delete success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // log error msg
          }
        })
      }).catch(() => {})
    },
    setRichText() {
      this.$nextTick(() => {
        this.editor = new E(`#editor`)
        this.editor.i18next = window.i18next
        this.editor.config.lang = 'en'
        this.editor.highlight = hljs
        this.editor.config.uploadImgServer = this.$baseUrl + '/file/editor/upload'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadImgParams = {
          type: 'img',
        }
        this.editor.config.uploadVideoServer = this.$baseUrl + '/file/editor/upload'
        this.editor.config.uploadVideoName = 'file'
        this.editor.config.uploadVideoHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadVideoParams = {
          type: 'video',
        }
        this.editor.create()  // create editor
      })
    },
    //Edit data
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // deep copy, set initial data into form
      this.formVisible = true   // open dialog
      this.setRichText()
      setTimeout(() => {
        this.editor.txt.html(row.content)  // need to use setTimeout here
      }, 0)
    },
    //Add data
    handleAdd() {
      this.form = {}  // make sure the initial data is blank
      this.formVisible = true   // open dialog

      this.setRichText()
    },
    //The logic triggered by the save button will trigger new or updated
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          //Get the contents of the edit box
          let content = this.editor.txt.html()
          this.form.content = content
          this.$request({
            url: this.form.id ? '/news/update': '/news/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // save success
              this.$message.success('save success')
              this.load(1)
              this.formVisible = false
            } else {
              this.$message.error(res.msg)  // log error info
            }
          })
        }
      })
    },
    reset() {
      this.title = ''
      this.load()
    },
    load(pageNum) {  // pagination
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/news/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
