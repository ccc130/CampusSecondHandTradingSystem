<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="买家ID" prop="buyerId">
        <el-input
          v-model="queryParams.buyerId"
          placeholder="请输入买家ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卖家ID" prop="sellerId">
        <el-input
          v-model="queryParams.sellerId"
          placeholder="请输入卖家ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品ID" prop="productId">
        <el-input
          v-model="queryParams.productId"
          placeholder="请输入商品ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in campus_order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['campus:orders:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['campus:orders:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['campus:orders:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ordersList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="id" />
      <el-table-column label="买家" align="center" prop="buyerId">
        <template #default="scope">
          {{ getUserName(scope.row.buyerId) }}
        </template>
      </el-table-column>
      <el-table-column label="卖家" align="center" prop="sellerId">
        <template #default="scope">
          {{ getUserName(scope.row.sellerId) }}
        </template>
      </el-table-column>
      <el-table-column label="商品" align="center" prop="productId">
        <template #default="scope">
          {{ getProductName(scope.row.productId) }}
        </template>
      </el-table-column>

      <el-table-column label="总价" align="center" prop="totalPrice" />
      <el-table-column label="订单状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="campus_order_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易完成时间" align="center" prop="completedAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.completedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['campus:orders:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['campus:orders:remove']">删除</el-button>
          <el-button link type="success" icon="Star" @click="handleReview(scope.row)" v-if="scope.row.status == 2 && scope.row.buyerId == getCurrentUserId()">评价</el-button>
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

    <!-- 添加或修改我的订单对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="ordersRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="买家ID" prop="buyerId">
          <el-input v-model="form.buyerId" placeholder="请输入买家ID" readonly />
        </el-form-item>
        <el-form-item label="卖家ID" prop="sellerId">
          <el-input v-model="form.sellerId" placeholder="请输入卖家ID" readonly />
        </el-form-item>
        <el-form-item label="商品ID" prop="productId">
          <el-input v-model="form.productId" placeholder="请输入商品ID" readonly />
        </el-form-item>

        <el-form-item label="总价" prop="totalPrice">
          <el-input v-model="form.totalPrice" placeholder="请输入总价" />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择订单状态">
            <el-option
              v-for="dict in campus_order_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间" prop="createdAt">
          <el-date-picker clearable
            v-model="form.createdAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择下单时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="交易完成时间" prop="completedAt">
          <el-date-picker clearable
            v-model="form.completedAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择交易完成时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价商品对话框 -->
    <el-dialog :title="reviewTitle" v-model="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewRef" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="商品名称" prop="productId">
          <el-input v-model="reviewProductName" placeholder="商品名称" readonly />
        </el-form-item>
        <el-form-item label="评价等级" prop="rating">
          <el-rate v-model="reviewForm.rating" :max="5" show-text allow-half />
        </el-form-item>
        <el-form-item label="评价内容" prop="comment">
          <el-input v-model="reviewForm.comment" type="textarea" :rows="4" placeholder="请输入评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelReview">取 消</el-button>
          <el-button type="primary" @click="submitReview">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Orders">
import { listOrders, getOrders, delOrders, addOrders, updateOrders } from "@/api/campus/orders"
import { getUser } from "@/api/system/user"
import { getProducts } from "@/api/campus/products"
import { addReviews } from "@/api/campus/reviews"
import useUserStore from '@/store/modules/user'
import { checkPermi } from '@/utils/permission'

const { proxy } = getCurrentInstance()
const { campus_order_status } = proxy.useDict('campus_order_status')

const ordersList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
// 评价相关
const reviewOpen = ref(false)
const reviewTitle = ref("")
// 用于存储映射关系的数据
const userMap = ref({})
const productMap = ref({})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    buyerId: null,
    sellerId: null,
    productId: null,
    status: null,
  },
  reviewForm: {
    orderId: null,
    reviewerId: null,
    reviewedUserId: null,
    rating: 5,
    comment: null,
    createdAt: null
  },
  rules: {
  }
})

const { queryParams, form, reviewForm } = toRefs(data)

const reviewRules = {
  rating: [
    { required: true, message: '请选择评价等级', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, max: 200, message: '评价内容长度应在5到200个字符之间', trigger: 'blur' }
  ]
}

// 评价商品名称
const reviewProductName = ref('')

// 获取当前用户ID
function getCurrentUserId() {
  return useUserStore().id
}

// 检查用户是否拥有编辑权限
function hasEditPermission() {
  return checkPermi(['campus:orders:edit'])
}

/** 查询我的订单列表 */
function getList() {
  loading.value = true
  listOrders(queryParams.value).then(response => {
    // 检查用户是否拥有编辑权限
    const hasEditPerm = hasEditPermission()
    
    if (!hasEditPerm) {
      const currentUserId = getCurrentUserId()
      if (currentUserId) {
        // 如果没有编辑权限，只显示当前用户相关的订单数据（作为买家或卖家）
        ordersList.value = response.rows.filter(item => 
          item.buyerId == currentUserId || item.sellerId == currentUserId
        )
        // 更新总数为过滤后的数量
        total.value = ordersList.value.length
      } else {
        ordersList.value = []
        total.value = 0
      }
    } else {
      // 有编辑权限，显示所有数据
      ordersList.value = response.rows
      total.value = response.total
    }
    
    loading.value = false
    
    // 获取所有相关数据的映射
    loadRelatedData(ordersList.value)
  })
}

// 加载相关数据的映射关系
function loadRelatedData(orders) {
  // 获取所有唯一的用户ID
  const userIds = [...new Set(orders.flatMap(item => [item.buyerId, item.sellerId]).filter(id => id))]
  
  // 获取所有唯一的商品ID
  const productIds = [...new Set(orders.map(item => item.productId).filter(id => id))]
  
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
}

// 根据ID获取用户名称
function getUserName(userId) {
  return userMap.value[userId] || `用户${userId}`
}

// 根据ID获取商品名称
function getProductName(productId) {
  return productMap.value[productId] || `商品${productId}`
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
    buyerId: null,
    sellerId: null,
    productId: null,

    totalPrice: null,
    status: null,
    createdAt: null,
    completedAt: null
  }
  proxy.resetForm("ordersRef")
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

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加我的订单"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getOrders(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改我的订单"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ordersRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateOrders(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addOrders(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除我的订单编号为"' + _ids + '"的数据项？').then(function() {
    return delOrders(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('campus/orders/export', {
    ...queryParams.value
  }, `orders_${new Date().getTime()}.xlsx`)
}

// 取消评价
function cancelReview() {
  reviewOpen.value = false
  resetReview()
}

// 评价表单重置
function resetReview() {
  reviewForm.value = {
    orderId: null,
    reviewerId: null,
    reviewedUserId: null,
    rating: 5,
    comment: null,
    createdAt: null
  }
  reviewProductName.value = ''
  proxy.resetForm("reviewRef")
}

/** 评价按钮操作 */
function handleReview(row) {
  const currentUserId = getCurrentUserId()
  
  // 确保只有订单的买家才能评价
  if (row.buyerId != currentUserId) {
    proxy.$modal.msgError("只有订单买家才能进行评价")
    return
  }
  
  resetReview()
  // 获取当前用户ID作为评价者ID
  reviewForm.value.reviewerId = currentUserId
  // 设置订单ID
  reviewForm.value.orderId = row.id
  // 设置被评价用户ID（商品卖家）
  reviewForm.value.reviewedUserId = row.sellerId
  // 获取商品名称
  reviewProductName.value = getProductName(row.productId)
  
  reviewOpen.value = true
  reviewTitle.value = "评价商品"
}

/** 提交评价 */
function submitReview() {
  proxy.$refs["reviewRef"].validate(valid => {
    if (valid) {
      // 设置创建时间
      reviewForm.value.createdAt = new Date().toISOString().split('T')[0]
      
      addReviews(reviewForm.value).then(response => {
        proxy.$modal.msgSuccess("评价成功")
        reviewOpen.value = false
        resetReview()
      }).catch(error => {
        proxy.$modal.msgError("评价失败：" + error.message)
      })
    }
  })
}

getList()
</script>
