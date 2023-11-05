<template>
  <div>
    <div>
      <el-input style="width: 200px" placeholder="name" v-model="name"></el-input>
      <el-button type="primary" style="margin-left: 10px" @click="load(1)">Query</el-button>
      <el-button type="info" @click="reset">Reset</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" plain @click="handleAdd">Add</el-button>
      <el-button type="danger" plain @click="delBatch">DeleteBatch</el-button>
    </div>

    <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column prop="id" label="id" width="70" align="center"></el-table-column>
      <el-table-column prop="no" label="number"></el-table-column>
      <el-table-column prop="name" label="name"></el-table-column>
      <el-table-column prop="money" label="money" show-overflow-tooltip></el-table-column>
      <el-table-column prop="user" label="user"></el-table-column>
      <el-table-column prop="date" label="date"></el-table-column>
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

    <el-dialog title="Order Information" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="name" prop="name">
          <el-input v-model="form.name" placeholder="name"></el-input>
        </el-form-item>
        <el-form-item label="money" prop="money">
          <el-input v-model="form.money" placeholder="money"></el-input>
        </el-form-item>
        <el-form-item label="category" prop="category">
          <el-select style="width: 100%" v-model="form.category">
            <el-option v-for="item in ['Fruits', 'Veggies', 'Snacks', 'Drinks', 'Milk', 'Cakes']" :key="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

export default {
  name: "Orders",
  data() {
    return {
      tableData: [],  // all data in table
      pageNum: 1,   // current page number
      pageSize: 5,  // items per page
      username: '',
      name: '',
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        name: [
          { required: true, message: 'please enter name', trigger: 'blur' },
        ],
        money: [
          { required: true, message: 'please enter money', trigger: 'blur' },
        ],
        category: [
          { required: true, message: 'please enter category', trigger: 'blur' },
        ]
      },
      ids: [],
      content: '',
    }
  },
  created() {
    this.load()
  },
  methods: {
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('Please select data')
        return
      }
      this.$confirm('Are you sure to delete orders by batch', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {   // deleteBatch success
            this.$message.success('deleteBatch success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // log error message
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {   // selected rows data
      this.ids = rows.map(v => v.id)
    },
    del(id) {
      this.$confirm('Are you sure to delete?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/' + id).then(res => {
          if (res.code === '200') {   // delete success
            this.$message.success('delete success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // log erro info
          }
        })
      }).catch(() => {})
    },
    //Edit Order
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))  // deep copy
      this.fromVisible = true   // open dialog
    },
    //Add Order
    handleAdd() {
      this.form = {}  // clear data in the form
      this.fromVisible = true   // open dialog
    },
    //The logic triggered by the save button will trigger new or updated
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.sendSaveRequest()
        }
      })
    },
    sendSaveRequest() {
      this.$request({
        url: this.form.id ? '/orders/update': '/orders/add',
        method: this.form.id ? 'PUT' : 'POST',
        data: this.form
      }).then(res => {
        if (res.code === '200') {  // save success
          this.$message.success('save success')
          this.load(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // load error msg
        }
      })
    },
    reset() {
      this.name = ''
      this.load()
    },
    load(pageNum) {  // pagination
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/orders/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
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
  }
}
</script>

<style scoped>

</style>
