<template>
  <div>
    <div>
      <el-input style="width: 200px" placeholder="query username" v-model="username"></el-input>
      <el-input style="width: 200px; margin: 0 5px" placeholder="query name" v-model="name"></el-input>
      <el-button type="primary" @click="load(1)">Query</el-button>
      <el-button type="info" @click="reset">Reset</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" plain @click="handleAdd">Add</el-button>
      <el-button type="danger" plain @click="delBatch">DelBatch</el-button>
      <el-button type="info" plain @click="exportData">ExportData</el-button>
      <el-upload :action="$baseUrl + '/user/import'" :headers="{token: user.token}" :on-success="handleImport" style="display: inline-block; margin-left: 10px" :show-file-list="false">
        <el-button type="primary" plain>ImportData</el-button>
      </el-upload>
    </div>

    <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="id" label="id" width="70" align="center"></el-table-column>
      <el-table-column prop="username" label="password"></el-table-column>
      <el-table-column prop="name" label="name"></el-table-column>
      <el-table-column prop="phone" label="phone"></el-table-column>
      <el-table-column prop="email" label="email"></el-table-column>
      <el-table-column prop="address" label="address"></el-table-column>
      <el-table-column label="avatar">
        <template v-slot="scope">
          <div style="display: flex; align-items: center">
            <el-image style="width: 50px; height: 50px; border-radius: 50%" v-if="scope.row.avatar"
                      :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"></el-image>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="role" label="role"></el-table-column>
      <el-table-column label="operate" align="center" width="180">
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

    <el-dialog title="User Information" :visible.sync="formVisible" width="30%">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="Username" prop="username">
          <el-input v-model="form.username" placeholder="username"></el-input>
        </el-form-item>
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" placeholder="name"></el-input>
        </el-form-item>
        <el-form-item label="Phone" prop="phone">
          <el-input v-model="form.phone" placeholder="phone"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="email"></el-input>
        </el-form-item>
        <el-form-item label="Address" prop="address">
          <el-input type="textarea" v-model="form.address" placeholder="address"></el-input>
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="admin"></el-radio>
            <el-radio label="user"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Avatar">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/file/upload'"
              :headers="{ token: user.token }"
              :file-list="form.avatar ? [form.avatar] : []"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">Upload avatar</el-button>
          </el-upload>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
export default {
  name: "User",
  data() {
    return {
      tableData: [],  // all data
      pageNum: 1,   // current page number
      pageSize: 5,  // number of items in one page
      username: '',
      name: '',
      total: 0,
      formVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        username: [
          {required: true, message: 'please enter username', trigger: 'blur'},
        ]
      },
      ids: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    handleImport(res, file, fileList) {
      if (res.code === '200') {
        this.$message.success("import success")
        this.load(1)
      } else {
        this.$message.error(res.msg)
      }
    },
    exportData() {   // export data in batch
      //When no rows are selected, export all or export based on my search conditions.
      if (!this.ids.length) {
        window.open(this.$baseUrl + '/user/export?token=' + this.user.token + "&username="
            + this.username + "&name=" + this.name)
      } else {      // [1,2,3] => '1,2,3'
        //Convert array to String
        let idStr = this.ids.join(',')
        window.open(this.$baseUrl + '/user/export?token=' + this.user.token + '&ids=' + idStr)
      }
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('Please select data')
        return
      }
      this.$confirm('Are you sure delete those data by batch？', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/user/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // delete success
            this.$message.success('deleteBatch sucess')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // pop error info
          }
        })
      }).catch(() => {
      })
    },
    //All row data currently selected
    handleSelectionChange(rows) {
      //each rows data using map
      this.ids = rows.map(v => v.id)
    },
    //Delete User
    del(id) {
      this.$confirm('Are you sure to delete', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/user/delete/' + id).then(res => {
          if (res.code === '200') {   // delete success
            this.$message.success('delete success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // pop error message
          }
        })
      }).catch(() => {
      })
    },
    // Edit data
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // form have some initial data -> deep copuy
      this.formVisible = true   // open dialog
    },
    // add data
    handleAdd() {
      this.form = {role: 'user'}  // Clear data when adding new data
      this.formVisible = true   // open dialog
    },
    //save button will trigger new or updated
    save() {
      this.$refs.formRef.validate((valid) => {
        //need to validate form on front end
        if (valid) {
          this.$request({
            //if have id -> update; otherwise -> add
            url: this.form.id ? '/user/update' : '/user/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // save success
              this.$message.success('save success')
              this.load(1)
              this.formVisible = false
            } else {
              this.$message.error(res.msg)  // error message
            }
          })
        }
      })
    },
    reset() {
      this.name = ''
      this.username = ''
      this.load()
    },
    load(pageNum) {  // pagination
      //created() -> load()
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/user/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
          name: this.name
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      //Replace the user's avatar attribute with the link to the uploaded image
      this.form.avatar = response.data
    },
  }
}
</script>

<style scoped>
</style>
