<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="投诉人" prop="complainantId">
            <el-input
              v-model="queryParams.complainantId"
              placeholder="请输入投诉人ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="被投诉人" prop="accusedId">
            <el-input
              v-model="queryParams.accusedId"
              placeholder="请输入被投诉人ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="涉及商品" prop="productId">
            <el-input
              v-model="queryParams.productId"
              placeholder="请输入涉及商品ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="涉及订单" prop="orderId">
            <el-input
              v-model="queryParams.orderId"
              placeholder="请输入涉及订单ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="投诉标题" prop="title">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入投诉标题"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="处理状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
              <el-option
                v-for="dict in campus_complaints_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="处理人" prop="handlerId">
            <el-input
              v-model="queryParams.handlerId"
              placeholder="请输入处理人"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="提交时间" prop="createdAt">
            <el-date-picker 
              v-model="queryParams.createdAt"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择提交时间">
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <div class="my-10px">
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </div>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['campus:complaints:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['campus:complaints:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['campus:complaints:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="complaintsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="投诉ID" align="center" prop="id" width="80" />
      <el-table-column label="投诉人" align="center" prop="complainantId" width="100">
        <template #default="scope">
          {{ getUserName(scope.row.complainantId) }}
        </template>
      </el-table-column>
      <el-table-column label="被投诉人" align="center" prop="accusedId" width="100">
        <template #default="scope">
          {{ getUserName(scope.row.accusedId) }}
        </template>
      </el-table-column>
      <el-table-column label="涉及商品" align="center" prop="productId" width="100">
        <template #default="scope">
          {{ getProductName(scope.row.productId) }}
        </template>
      </el-table-column>
      <el-table-column label="涉及订单" align="center" prop="orderId" width="100">
        <template #default="scope">
          {{ getOrderName(scope.row.orderId) }}
        </template>
      </el-table-column>
      <el-table-column label="投诉标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="处理状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="campus_complaints_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="handlerId" width="100" />
      <el-table-column label="处理时间" align="center" prop="handledAt" width="120">
        <template #default="scope">
          <span>{{ parseTime(scope.row.handledAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createdAt" width="120">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['campus:complaints:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['campus:complaints:remove']">删除</el-button>
          <el-button link type="success" icon="Check" @click="handleApprove(scope.row)" v-hasPermi="['campus:complaints:handle']">审核</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改投诉商品对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="complaintsRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="投诉人" prop="complainantId">
              <el-input v-model="form.complainantId" placeholder="请输入投诉人ID" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="被投诉人" prop="accusedId">
              <el-input v-model="form.accusedId" placeholder="请输入被投诉人ID" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="涉及商品" prop="productId">
              <el-input v-model="form.productId" placeholder="请输入商品ID" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="涉及订单" prop="orderId">
              <el-input v-model="form.orderId" placeholder="请输入订单ID" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入投诉标题" />
        </el-form-item>
        <el-form-item label="详细描述" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="4"
            placeholder="请输入详细描述" 
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="处理状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择处理状态" style="width: 100%;">
                <el-option
                  v-for="dict in campus_complaints_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理人" prop="handlerId">
              <el-input v-model="form.handlerId" placeholder="请输入处理人ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="提交时间" prop="createdAt">
              <el-date-picker 
                v-model="form.createdAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择提交时间"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核投诉商品对话框 -->
    <el-dialog :title="titleApprove" v-model="openApproveDialog" width="600px" append-to-body>
      <el-form ref="complaintsApproveRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="投诉人" prop="complainantId">
              <el-input v-model="form.complainantId" placeholder="请输入投诉人ID" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="被投诉人" prop="accusedId">
              <el-input v-model="form.accusedId" placeholder="请输入被投诉人ID" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="涉及商品" prop="productId">
              <el-input v-model="currentProductName" placeholder="请输入商品ID" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="涉及订单" prop="orderId">
              <el-input v-model="currentOrderName" placeholder="请输入订单ID" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入投诉标题" readonly />
        </el-form-item>
        <el-form-item label="详细描述" prop="content">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            :rows="4"
            placeholder="投诉详细描述" 
            readonly
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="处理状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择处理状态" style="width: 100%;">
                <el-option
                  v-for="dict in campus_complaints_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="parseInt(dict.value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理人" prop="handlerId">
              <el-input v-model="form.handlerId" placeholder="请输入处理人ID" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="审核备注">
          <el-input 
            v-model="form.reviewComment" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入审核意见或处理说明" 
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="处理时间" prop="handledAt">
              <el-date-picker 
                v-model="form.handledAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择处理时间"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提交时间" prop="createdAt">
              <el-date-picker 
                v-model="form.createdAt"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择提交时间"
                style="width: 100%;" readonly>
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitApprove">确 定</el-button>
          <el-button @click="cancelApprove">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Complaints">
import { listComplaints, getComplaints, delComplaints, addComplaints, updateComplaints } from "@/api/campus/complaints"
import { getUser } from "@/api/system/user"
import { getProducts } from "@/api/campus/products"
import { getOrders } from "@/api/campus/orders"
import useUserStore from '@/store/modules/user'
import { checkPermi } from '@/utils/permission'

const { proxy } = getCurrentInstance()
const { campus_complaints_status } = proxy.useDict('campus_complaints_status')

const complaintsList = ref([])
const open = ref(false)
const openApproveDialog = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const titleApprove = ref("")
// 用于存储映射关系的数据
const userMap = ref({})
const productMap = ref({})
const orderMap = ref({})

// 当前订单名称
const currentOrderName = ref("")
const currentProductName = ref("")

// 审核对话框表单引用
const complaintsRef = ref(null)
const complaintsApproveRef = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    complainantId: null,
    accusedId: null,
    productId: null,
    orderId: null,
    title: null,
    status: null,
    handlerId: null,
    handledAt: null,
    createdAt: null
  },
  rules: {
    complainantId: [
      { required: true, message: "投诉人不能为空", trigger: "blur" }
    ],
    accusedId: [
      { required: true, message: "被投诉人不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询投诉商品列表 */
function getList() {
  loading.value = true
  listComplaints(queryParams.value).then(response => {
    // 检查用户是否拥有编辑权限
    const hasEditPerm = hasEditPermission()
    
    if (!hasEditPerm) {
      const currentUserId = getCurrentUserId()
      if (currentUserId) {
        // 如果没有编辑权限，只显示当前用户相关的投诉数据（作为投诉人或被投诉人）
        complaintsList.value = response.rows.filter(item => 
          item.complainantId == currentUserId || item.accusedId == currentUserId
        )
        // 更新总数为过滤后的数量
        total.value = complaintsList.value.length
      } else {
        complaintsList.value = []
        total.value = 0
      }
    } else {
      // 有编辑权限，显示所有数据
      complaintsList.value = response.rows
      total.value = response.total
    }
    
    loading.value = false
    
    // 获取所有相关数据的映射
    loadRelatedData(complaintsList.value)
  })
}

// 加载相关数据的映射关系
function loadRelatedData(complaints) {
  // 获取所有唯一的用户ID
  const userIds = [...new Set(complaints.flatMap(item => [item.complainantId, item.accusedId, item.handlerId]).filter(id => id))]
  
  // 获取所有唯一的商品ID
  const productIds = [...new Set(complaints.map(item => item.productId).filter(id => id))]
  
  // 获取所有唯一的订单ID
  const orderIds = [...new Set(complaints.map(item => item.orderId).filter(id => id))]
  
  // 获取用户信息
  userIds.forEach(userId => {
    if (!userMap.value[userId]) {
      getUser(userId).then(response => {
        userMap.value[userId] = response.data.nickName || response.data.userName || `用户${userId}`
      }).catch(() => {
        userMap.value[userId] = `用户${userId}`
      })
    }
  })
  
  // 获取商品信息
  productIds.forEach(productId => {
    if (!productMap.value[productId]) {
      getProducts(productId).then(response => {
        productMap.value[productId] = response.data.title || `商品${productId}`
      }).catch(() => {
        productMap.value[productId] = `商品${productId}`
      })
    }
  })
  
  // 获取订单信息
  orderIds.forEach(orderId => {
    if (!orderMap.value[orderId]) {
      getOrders(orderId).then(response => {
        orderMap.value[orderId] = `订单${orderId}` // 订单可能需要显示更多信息，这里简化处理
      }).catch(() => {
        orderMap.value[orderId] = `订单${orderId}`
      })
    }
  })
}

// 根据ID获取用户名称
function getUserName(userId) {
  return userMap.value[userId] || `用户${userId}`
}

// 根据ID获取商品名称
function getProductName(productId) {
  return productMap.value[productId] || `商品${productId}`
}

// 根据ID获取订单名称
function getOrderName(orderId) {
  return orderMap.value[orderId] || `订单${orderId}`
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    complainantId: null,
    accusedId: null,
    productId: null,
    orderId: null,
    title: null,
    content: null,
    status: null,
    handlerId: null,
    handledAt: null,
    createdAt: null
  }
  proxy.resetForm("complaintsRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 获取当前用户ID
function getCurrentUserId() {
  return useUserStore().id
}

// 检查用户是否拥有编辑权限
function hasEditPermission() {
  return checkPermi(['campus:complaints:edit'])
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  // 自动填写提交时间
  form.value.createdAt = new Date().toISOString().split('T')[0]
  open.value = true
  title.value = "添加投诉商品"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getComplaints(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改投诉商品"
  })
}

/** 审核按钮操作 */
function handleApprove(row) {
  reset()
  const _id = row.id
  getComplaints(_id).then(response => {
    form.value = response.data
    // 设置默认处理人为当前用户
    form.value.handlerId = getCurrentUserId()
    // 设置默认处理时间为当前时间
    form.value.handledAt = new Date().toISOString().split('T')[0]
    // 设置当前商品名称和订单名称
    currentProductName.value = getProductName(response.data.productId)
    currentOrderName.value = getOrderName(response.data.orderId)
    // 打开审核对话框
    openApproveDialog.value = true
    titleApprove.value = "审核投诉"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["complaintsRef"].validate(valid => {
    if (valid) {
      // 如果是新增投诉，自动填写提交时间
      if (!form.value.id) {
        form.value.createdAt = new Date().toISOString().split('T')[0]
      }
      // 如果是审核操作，自动填写处理时间和处理人
      if (title.value === "审核投诉") {
        form.value.handlerId = getCurrentUserId()
        form.value.handledAt = new Date().toISOString().split('T')[0]
      }
      
      if (form.value.id != null) {
        updateComplaints(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addComplaints(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 提交审核 */
function submitApprove() {
  proxy.$refs["complaintsApproveRef"].validate(valid => {
    if (valid) {
      // 自动填写处理人和处理时间
      form.value.handlerId = getCurrentUserId()
      form.value.handledAt = new Date().toISOString().split('T')[0]
      
      updateComplaints(form.value).then(response => {
        proxy.$modal.msgSuccess("审核成功")
        openApproveDialog.value = false
        getList()
      }).catch(error => {
        proxy.$modal.msgError("审核失败：" + error.message)
      })
    }
  })
}

// 取消审核
function cancelApprove() {
  openApproveDialog.value = false
  reset()
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除投诉商品编号为"' + _ids + '"的数据项？').then(function() {
    return delComplaints(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('campus/complaints/export', {
    ...queryParams.value
  }, `complaints_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style lang="scss">
.my-10px {
  margin: 10px 0;
}

.app-container {
  padding: 20px;
}

:deep(.el-table) {
  .cell {
    padding-left: 10px;
    padding-right: 10px;
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
  
  .el-button {
    margin-left: 10px;
  }
}

// 表格操作列按钮样式
.el-table .small-padding .cell {
  padding: 5px 0;
}

.el-table .fixed-width .el-button {
  padding: 6px 12px;
  margin-left: 6px;
  margin-right: 6px;
}

// 表单布局优化
:deep(.el-form) {
  .el-form-item {
    margin-bottom: 18px;
  }
  
  .el-row {
    margin-bottom: 0;
  }
}
</style>
