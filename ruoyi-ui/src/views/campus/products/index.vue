<template>
  <div class="app-container">
    <!-- 搜索和筛选栏 -->
    <div class="filter-container">
      <el-row :gutter="10" class="mb8">
        <el-col :span="6">
          <el-input
            v-model="queryParams.title"
            placeholder="商品名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="queryParams.categoryId"
            placeholder="商品分类"
            clearable
            @change="handleQuery"
          >
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="queryParams.conditions"
            placeholder="新旧程度"
            clearable
            @change="handleQuery"
          >
            <el-option label="全新" value="1" />
            <el-option label="9成新" value="2" />
            <el-option label="8成新" value="3" />
            <el-option label="7成新" value="4" />
            <el-option label="6成新及以下" value="5" />
          </el-select>
        </el-col>
        <div><br><br></div>
        <el-col :span="20">
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="handleAdd" v-hasPermi="['campus:products:add']">发布商品</el-button>
          <el-button 
            type="danger" 
            icon="Delete" 
            @click="handleDeleteSelected"
            v-hasPermi="['campus:products:remove']"
            :disabled="multiple"
          >
            删除商品
          </el-button>
        </el-col>
      </el-row>
      
      <el-row :gutter="10" class="mb8">
        <el-col :span="24">
          <div class="sort-options">
            <span>排序方式:</span>
            <el-button 
              :type="sortField === 'price' && sortOrder === 'asc' ? 'primary' : 'default'"
              @click="sortBy('price', 'asc')"
            >
              价格从低到高
            </el-button>
            <el-button 
              :type="sortField === 'price' && sortOrder === 'desc' ? 'primary' : 'default'"
              @click="sortBy('price', 'desc')"
            >
              价格从高到低
            </el-button>
            <el-button 
              :type="sortField === 'createdAt' && sortOrder === 'desc' ? 'primary' : 'default'"
              @click="sortBy('createdAt', 'desc')"
            >
              最新发布
            </el-button>
            <el-button 
              :type="sortField === 'viewCount' && sortOrder === 'desc' ? 'primary' : 'default'"
              @click="sortBy('viewCount', 'desc')"
            >
              热门
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 商品网格展示 -->
    <div class="products-grid-container">
      <div class="select-all-header">
        <el-checkbox 
          v-model="allChecked" 
          :indeterminate="isIndeterminate"
          @change="handleCheckAllChange"
        >
          全选
        </el-checkbox>
      </div>
      <div class="products-grid">
        <div 
          v-for="product in productsList" 
          :key="product.id" 
          class="product-card"
        >
          <div class="product-checkbox">
            <el-checkbox 
              v-model="product.checked" 
              @change="(val) => handleItemChange(val, product)"
            ></el-checkbox>
          </div>
          <div class="product-image" @click="viewProduct(product)">
            <img :src="getImageUrls(product.imageUrls)[0]" :alt="product.title" @error="onImageError" />
            <div class="product-badge" v-if="product.status === 0">已售罄</div>
          </div>
          <div class="product-info">
            <h3 class="product-title" @click="viewProduct(product)">{{ product.title }}</h3>
            <p class="product-description">{{ product.description?.substring(0, 50) }}...</p>
            <div class="product-meta">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-condition">成新度: {{ getConditionText(product.conditions) }}</span>
            </div>
            <div class="product-seller">
              <span>卖家: {{ getUserName(product.userId) }}</span>
              <span>浏览: {{ product.viewCount }}</span>
            </div>
          </div>
          <div class="product-actions">
            <el-button 
              type="danger" 
              size="small" 
              icon="ShoppingBag"
              @click="buyProductFromGrid(product)"
              :disabled="product.status === 0"
            >
              立即购买
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              icon="ShoppingCart"
              @click="addToCart(product)"
              :disabled="product.status === 0"
            >
              加入购物车
            </el-button>
            <el-button 
              :type="product.isFavorite ? 'warning' : 'default'" 
              size="small" 
              icon="Star"
              @click="toggleFavorite(product)"
            >
              {{ product.isFavorite ? '已收藏' : '收藏' }}
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              icon="ChatLineRound"
              @click="complainProduct(product)"
            >
              投诉
            </el-button>
            <el-button
              type="primary"
              size="small"
              icon="Edit"
              @click="handleUpdate(product)"
              v-hasPermi="['campus:products:edit']"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              icon="Delete"
              @click="handleDeleteOne(product)"
              v-hasPermi="['campus:products:remove']"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 发布商品对话框 -->
    <el-dialog :title="productDialogTitle" v-model="productDialogVisible" width="600px" append-to-body>
      <el-form ref="productFormRef" :model="productForm" :rules="productRules" label-width="100px">
        <el-form-item label="商品标题" prop="title">
          <el-input id="product-title" v-model="productForm.title" placeholder="请输入商品标题" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select id="product-category" v-model="productForm.categoryId" placeholder="请选择商品分类" style="width: 100%;">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number id="product-price" v-model="productForm.price" :min="0" :step="0.01" placeholder="请输入价格" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="新旧程度" prop="conditions">
          <el-select id="product-conditions" v-model="productForm.conditions" placeholder="请选择新旧程度" style="width: 100%;">
            <el-option label="全新" value="1" />
            <el-option label="9成新" value="2" />
            <el-option label="8成新" value="3" />
            <el-option label="7成新" value="4" />
            <el-option label="6成新及以下" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片" prop="imageUrls">
          <image-upload v-model="productForm.imageUrls" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input 
            v-model="productForm.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入商品详细描述"
          />
        </el-form-item>
        <el-form-item label="商品状态" prop="status">
          <el-radio-group v-model="productForm.status">
            <el-radio :label="1">在售</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="productDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitProductForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 商品详情对话框 -->
    <el-dialog title="商品详情" v-model="productDetailVisible" width="60%" append-to-body>
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="image-container">
              <img 
                :src="getImageUrls(selectedProduct.imageUrls)[0]" 
                :alt="selectedProduct.title" 
                class="detail-image" 
                @error="onImageError" 
                @click="openImageViewer(0)"
                style="cursor: pointer; width: 100%; height: 300px; object-fit: cover; border-radius: 8px;"
              />
              <div v-if="getImageUrls(selectedProduct.imageUrls).length > 1" class="image-count">
                {{ currentImageUrlIndex + 1 }} / {{ getImageUrls(selectedProduct.imageUrls).length }}
              </div>
              <!-- 图片缩略图预览 -->
              <div v-if="getImageUrls(selectedProduct.imageUrls).length > 1" class="thumbnail-preview">
                <div 
                  v-for="(img, index) in getImageUrls(selectedProduct.imageUrls)" 
                  :key="index"
                  class="thumbnail-item"
                  :class="{ active: index === currentImageUrlIndex }"
                  @click="currentImageUrlIndex = index"
                >
                  <img :src="img" :alt="`图片${index+1}`" />
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <h2>{{ selectedProduct.title }}</h2>
            <div class="detail-price">¥{{ selectedProduct.price }}</div>
            <div class="detail-meta">
              <p>成新度: {{ getConditionText(selectedProduct.conditions) }}</p>
              <p>卖家: {{ getUserName(selectedProduct.userId) }}</p>
              <p>浏览次数: {{ selectedProduct.viewCount }}</p>
              <p>发布时间: {{ parseTime(selectedProduct.createdAt, '{y}-{m}-{d}') }}</p>
            </div>
            <div class="detail-actions">
              <el-button 
                type="danger" 
                size="large" 
                icon="ShoppingBag"
                @click="buyProductFromDetail(selectedProduct)"
                :disabled="selectedProduct.status === 0"
              >
                立即购买
              </el-button>
              <el-button 
                type="success" 
                size="large" 
                icon="ShoppingCart"
                @click="addToCartFromDetail(selectedProduct)"
                :disabled="selectedProduct.status === 0"
              >
                加入购物车
              </el-button>
              <el-button 
                :type="selectedProduct.isFavorite ? 'warning' : 'default'" 
                size="large" 
                icon="Star"
                @click="toggleFavorite(selectedProduct)"
              >
                {{ selectedProduct.isFavorite ? '已收藏' : '收藏' }}
              </el-button>
              <el-button 
                type="primary" 
                size="large" 
                icon="Edit"
                @click="handleUpdate(selectedProduct)"
                v-if="selectedProduct.userId === getCurrentUserId() || auth.hasPermi('campus:products:edit')"
              >
                编辑商品
              </el-button>
              <el-button
                type="danger"
                size="large"
                icon="Delete"
                @click="handleDeleteOne(selectedProduct)"
                v-if="auth.hasPermi('campus:products:remove')"
              >
                删除商品
              </el-button>
            </div>
          </el-col>
        </el-row>
        <div class="detail-description">
          <h4>商品描述</h4>
          <p>{{ selectedProduct.description }}</p>
        </div>
        
        <!-- 商品评论区域 -->
        <div class="product-reviews">
          <h4>商品评价</h4>
          <div class="review-form">
            <el-input 
              v-model="newReviewContent" 
              type="textarea" 
              :rows="3" 
              placeholder="请输入您的评价..." 
            />
            <div class="review-rating">
              <span>评分：</span>
              <el-rate v-model="newReviewRating" :max="5" />
            </div>
            <el-button 
              type="primary" 
              @click="submitReview" 
              :disabled="!newReviewContent || !newReviewRating"
              style="margin-top: 10px;"
            >
              发表评价
            </el-button>
          </div>
          
          <div class="reviews-list">
            <div 
              v-for="review in productReviews" 
              :key="review.id" 
              class="review-item"
            >
              <div class="review-header">
                <span class="reviewer">用户{{ review.reviewerId }}</span>
                <el-rate 
                  v-model="review.rating" 
                  :max="5" 
                  disabled 
                  show-score 
                  text-color="#ff9900" 
                  score-template="{value}" 
                />
              </div>
              <div class="review-content">{{ review.comment }}</div>
              <div class="review-time">{{ parseTime(review.createdAt, '{y}-{m}-{d}') }}</div>
            </div>
            
            <div v-if="productReviews.length === 0" class="no-reviews">
              暂无评价
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 购买商品对话框 -->
    <el-dialog title="确认购买" v-model="buyDialogVisible" width="400px" append-to-body>
      <div v-if="selectedProduct">
        <div class="buy-product-info">
          <img :src="getImageUrls(selectedProduct.imageUrls)[0]" :alt="selectedProduct.title" class="buy-product-image" @error="onImageError" />
          <div class="buy-product-details">
            <h3>{{ selectedProduct.title }}</h3>
            <p class="buy-product-price">¥{{ selectedProduct.price }}</p>
            <p>卖家: {{ getUserName(selectedProduct.userId) }}</p>
          </div>
        </div>
        <div class="buy-options">
          <p class="total-price">总计: ¥{{ selectedProduct.price.toFixed(2) }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="buyDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="showPaymentDialog">去支付</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 支付确认对话框 -->
    <el-dialog title="扫码支付" v-model="showPaymentConfirmation" width="400px" append-to-body>
      <div class="payment-content">
        <p class="payment-amount">支付金额: <strong>¥{{ selectedProduct?.price.toFixed(2) }}</strong></p>
        <div class="qr-code-container">
          <div class="qr-placeholder">
            <div class="qr-simulator">
              <img :src="paymentQrCode" alt="二维码">
            </div>
            <p>请使用支付宝或微信扫描支付</p>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showPaymentConfirmation = false">取消支付</el-button>
          <el-button type="primary" @click="confirmPayment">确认已支付</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 投诉商品对话框 -->
    <el-dialog title="投诉商品" v-model="complaintDialogVisible" width="500px" append-to-body>
      <el-form :model="complaintForm" :rules="complaintRules" ref="complaintRef" label-width="80px">
        <el-form-item label="投诉商品">
          <div class="complaint-product-info">
            <img :src="getImageUrls(selectedProduct?.imageUrls)[0]" :alt="selectedProduct?.title" class="complaint-product-image" @error="onImageError" />
            <span>{{ selectedProduct?.title }}</span>
          </div>
        </el-form-item>
        <el-form-item label="投诉类型" prop="title">
          <el-select v-model="complaintForm.title" id="complaint-type" placeholder="请选择投诉类型" style="width: 100%;">
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="虚假描述" value="虚假描述" />
            <el-option label="卖家服务态度差" value="卖家服务态度差" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉内容" prop="content">
          <el-input 
            v-model="complaintForm.content" 
            id="complaint-content"
            type="textarea" 
            placeholder="请输入投诉内容" 
            :rows="4" 
          />
        </el-form-item>
        <el-form-item label="涉及订单">
          <el-input id="complaint-order-id" v-model="complaintForm.orderId" placeholder="如涉及订单，请填写订单ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="complaintDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitComplaint">提 交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Products">
import { listProducts, getProducts, delProducts, batchDelProducts, addProducts, updateProducts } from "@/api/campus/products"
import { listCategories } from "@/api/campus/categories"
import { addCarts } from "@/api/campus/carts"
import { addFavorites, listFavorites, delFavorites } from "@/api/campus/favorites"
import { listOrders, addOrders } from "@/api/campus/orders"
import { addComplaints } from "@/api/campus/complaints"
import { listReviews, addReviews } from "@/api/campus/reviews"
import { getUser } from "@/api/system/user"
import useUserStore from '@/store/modules/user'
import auth from '@/plugins/auth'
import payImage from "@/assets/images/pay.jpg"

const { proxy } = getCurrentInstance()

const productsList = ref([])
const categoryList = ref([])
const loading = ref(true)
const total = ref(0)
const sortField = ref('')
const sortOrder = ref('')
const productDetailVisible = ref(false)
const buyDialogVisible = ref(false)
const showPaymentConfirmation = ref(false) // 新增：支付确认对话框
const complaintDialogVisible = ref(false)
const paymentQrCode = ref(payImage)   // 支付二维码图片路径
const selectedProduct = ref(null)
const selectedTotalPrice = ref(0)  // 添加缺失的响应式变量
const buyForm = reactive({
  quantity: 1
})
// 用于存储映射关系的数据
const userMap = ref({})
// 评论相关数据
const productReviews = ref([])
const newReviewContent = ref('')
const newReviewRating = ref(0)
// 图片相关状态
const imageViewerVisible = ref(false)
const currentImageUrlIndex = ref(0)
// 删除相关状态
const multiple = ref(true)
const ids = ref([])
// 选择相关状态
const allChecked = ref(false)
const isIndeterminate = ref(false)
const complaintForm = reactive({
  title: '',
  content: '',
  orderId: null,
  productId: null,
  complainantId: null,
  accusedId: null
})
const complaintRules = {
  title: [
    { required: true, message: "请选择投诉类型", trigger: "change" }
  ],
  content: [
    { required: true, message: "投诉内容不能为空", trigger: "blur" }
  ]
}

// 发布商品相关
const productDialogVisible = ref(false)
const productDialogTitle = ref('')
const productFormRef = ref(null)
const productForm = reactive({
  id: null,
  title: null,
  categoryId: null,
  price: null,
  conditions: null,
  imageUrls: null,
  description: null,
  status: 1,
  userId: null
})
const productRules = {
  title: [
    { required: true, message: "商品标题不能为空", trigger: "blur" }
  ],
  categoryId: [
    { required: true, message: "请选择商品分类", trigger: "change" }
  ],
  price: [
    { required: true, message: "价格不能为空", trigger: "blur" },
    { type: 'number', min: 0, message: "价格不能小于0", trigger: "blur" }
  ],
  conditions: [
    { required: true, message: "请选择新旧程度", trigger: "change" }
  ],
  description: [
    { required: true, message: "商品描述不能为空", trigger: "blur" }
  ]
}

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 12, // 每页显示12个商品，适合网格布局
    userId: null,
    categoryId: null,
    title: null,
    conditions: null,
    status: null,
  }
})

const { queryParams } = toRefs(data)

// 获取分类列表
function getCategoryList() {
  listCategories({}).then(response => {
    categoryList.value = response.rows
  })
}

// 获取商品列表
function getList() {
  loading.value = true
  // 添加排序参数
  const params = { ...queryParams.value }
  if (sortField.value) {
    params.orderByColumn = sortField.value
    params.isAsc = sortOrder.value
  }
  
  listProducts(params).then(response => {
    productsList.value = response.rows.map(item => ({
      ...item,
      checked: false
    }))
    total.value = response.total
    loading.value = false
    
    // 获取当前用户收藏的商品列表
    checkFavorites()
    
    // 获取所有相关数据的映射
    loadRelatedData(response.rows)
  })
}

// 检查收藏状态
function checkFavorites() {
  // 这里需要获取当前用户收藏的商品ID列表
  // 为了演示，我们假设用户已登录并获取其收藏列表
  const favoriteParams = {
    pageNum: 1,
    pageSize: 9999, // 获取所有收藏
    userId: getCurrentUserId() // 获取当前用户ID
  }
  
  listFavorites(favoriteParams).then(response => {
    const favoriteIds = response.rows.map(item => item.productId)
    productsList.value.forEach(product => {
      product.isFavorite = favoriteIds.includes(product.id)
    })
  })
}

// 获取当前用户ID
function getCurrentUserId() {
  // 从store中获取当前用户ID
  const userStore = useUserStore();
  return userStore.id;
}

// 获取商品评论
function getProductReviews(productId) {
  // 获取所有评论，然后筛选与当前商品相关的评论
  const orderParams = {
    pageNum: 1,
    pageSize: 9999, // 获取所有相关订单
    productId: productId
  }
  
  // 先获取相关订单
  listOrders(orderParams).then(orderResponse => {
    const relatedOrderIds = orderResponse.rows.map(order => order.id)
    
    // 然后获取对这些订单的评论
    if (relatedOrderIds.length > 0) {
      // 如果有相关订单，查找对这些订单的评论
      const reviewParams = {
        pageNum: 1,
        pageSize: 100,
      }
      
      listReviews(reviewParams).then(reviewResponse => {
        // 过滤出与相关订单相关的评论
        productReviews.value = reviewResponse.rows.filter(review => 
          relatedOrderIds.includes(review.orderId)
        )
      }).catch(error => {
        console.error("获取商品评论失败", error)
        productReviews.value = []
      })
    } else {
      // 如果没有相关订单，就没有评论
      productReviews.value = []
    }
  }).catch(error => {
    console.error("获取相关订单失败", error)
    // 如果无法获取订单，尝试直接获取评论（可能不准确）
    const reviewParams = {
      pageNum: 1,
      pageSize: 100,
    }
    
    listReviews(reviewParams).then(reviewResponse => {
      // 这种情况下无法准确筛选，暂时显示所有评论
      productReviews.value = reviewResponse.rows
    }).catch(reviewError => {
      console.error("获取商品评论失败", reviewError)
      productReviews.value = []
    })
  })
}

// 提交评论
function submitReview() {
  if (!newReviewContent.value || !newReviewRating.value) {
    proxy.$modal.msgError("请填写评论内容和评分")
    return
  }

  // 需要先获取购买该商品的订单
  const orderParams = {
    pageNum: 1,
    pageSize: 10,
    productId: selectedProduct.value.id,
    buyerId: getCurrentUserId()
  }
  
  listOrders(orderParams).then(orderResponse => {
    if (orderResponse.rows && orderResponse.rows.length > 0) {
      // 找到相关订单，使用第一个订单ID
      const orderId = orderResponse.rows[0].id
      
      const reviewData = {
        orderId: orderId, // 关联订单ID
        reviewerId: getCurrentUserId(),
        reviewedUserId: selectedProduct.value.userId, // 商品卖家ID
        rating: newReviewRating.value,
        comment: newReviewContent.value,
        createdAt: new Date().toISOString().split('T')[0]
      }
      
      addReviews(reviewData).then(response => {
        proxy.$modal.msgSuccess("评价成功")
        // 清空输入框
        newReviewContent.value = ''
        newReviewRating.value = 0
        // 重新获取评论列表
        getProductReviews(selectedProduct.value.id)
      }).catch(error => {
        proxy.$modal.msgError("评价失败：" + error.message)
      })
    } else {
      proxy.$modal.msgError("您还没有购买此商品，无法进行评价")
    }
  }).catch(error => {
    proxy.$modal.msgError("获取订单信息失败：" + error.message)
  })
}

// 更新商品状态
function updateProductStatus(productId, status) {
  // 获取当前商品信息
  getProducts(productId).then(response => {
    const product = response.data
    product.status = status
    // 更新商品信息
    updateProducts(product).then(updateResponse => {
      console.log("商品状态已更新", updateResponse)
      // 刷新商品列表
      getList()
    }).catch(error => {
      console.error("更新商品状态失败", error)
    })
  }).catch(error => {
    console.error("获取商品信息失败", error)
  })
}

// 更新商品查看次数
function updateViewCount(productId) {
  // 获取当前商品信息
  getProducts(productId).then(response => {
    const product = response.data
    product.viewCount = (product.viewCount || 0) + 1
    updateProducts(product).then(updateResponse => {
      console.log("商品查看次数已更新", updateResponse)
      selectedProduct.value.viewCount = product.viewCount
      // 也更新商品列表中的对应商品查看次数
      const productInList = productsList.value.find(p => p.id === productId)
      if (productInList) {
        productInList.viewCount = product.viewCount
      }
    }).catch(error => {
      console.error("更新商品查看次数失败", error)
    })
  }).catch(error => {
    console.error("获取商品信息失败", error)
  })
}

// 搜索
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置
function resetQuery() {
  // 重置查询参数
  queryParams.value = {
    pageNum: 1,
    pageSize: 12,
    userId: null,
    categoryId: null,
    title: null,
    conditions: null,
    status: null,
  }
  sortField.value = ''
  sortOrder.value = ''
  getList()
}

// 排序
function sortBy(field, order) {
  sortField.value = field
  sortOrder.value = order
  getList()
}

// 查看商品详情
function viewProduct(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  selectedProduct.value = { ...product };
  productDetailVisible.value = true
  // 加载商品评论
  getProductReviews(product.id)
  // 更新查看次数
  updateViewCount(product.id)
}

// 加入购物车
function addToCart(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  const productCopy = { ...product };
  const cartData = {
    userId: getCurrentUserId(),
    productId: productCopy.id,
    quantity: 1,
    addedAt: new Date().toISOString().split('T')[0]
  }
  
  addCarts(cartData).then(response => {
    proxy.$modal.msgSuccess(`已成功将商品《${productCopy.title}》加入购物车`)
  }).catch(error => {
    proxy.$modal.msgError("加入购物车失败：" + error.message)
  })
}

// 购买商品
function buyProduct(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  selectedProduct.value = { ...product };
  buyDialogVisible.value = true
}

// 从商品网格立即购买
function buyProductFromGrid(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  selectedProduct.value = { ...product };
  buyDialogVisible.value = true
}

// 显示支付确认对话框
function showPaymentDialog() {
  // 确保支付金额使用当前selectedProduct的价格
  if (selectedProduct.value) {
    selectedTotalPrice.value = selectedProduct.value.price
  }
  buyDialogVisible.value = false
  showPaymentConfirmation.value = true
}

// 加载相关数据的映射关系
function loadRelatedData(products) {
  // 获取所有唯一的用户ID
  const userIds = [...new Set(products.map(item => item.userId).filter(id => id))]
  
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
}

// 根据ID获取用户名称
function getUserName(userId) {
  return userMap.value[userId] || `用户${userId}`
}

// 确认支付
function confirmPayment() {
  showPaymentConfirmation.value = false
  proxy.$modal.msgSuccess("支付成功！")
  
  // 创建订单（数量固定为1）
  // 使用selectedProduct的当前值的副本，避免后续状态变化影响
  const currentProduct = {
    id: selectedProduct.value.id,
    userId: selectedProduct.value.userId,
    price: selectedProduct.value.price
  };
  
  const orderData = {
    buyerId: getCurrentUserId(),
    sellerId: currentProduct.userId,
    productId: currentProduct.id,
    quantity: 1, // 固定为1
    totalPrice: currentProduct.price, // 价格即为总价
    status: 2, // 已付款，待发货
    createdAt: new Date().toISOString().split('T')[0],
    completedAt: null
  }
  
  addOrders(orderData).then(response => {
    proxy.$modal.msgSuccess("订单创建成功")
    // 更新商品状态为已售罄
    updateProductStatus(currentProduct.id, 0)
  }).catch(error => {
    proxy.$modal.msgError("订单创建失败：" + error.message)
  })
}

// 从商品详情对话框加入购物车
function addToCartFromDetail(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  const productCopy = { ...product };
  const cartData = {
    userId: getCurrentUserId(),
    productId: productCopy.id,
    quantity: 1,
    addedAt: new Date().toISOString().split('T')[0]
  }
  
  addCarts(cartData).then(response => {
    proxy.$modal.msgSuccess(`已成功将商品《${productCopy.title}》加入购物车`)
  }).catch(error => {
    proxy.$modal.msgError("加入购物车失败：" + error.message)
  })
}

// 从商品详情对话框立即购买
function buyProductFromDetail(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  selectedProduct.value = { ...product };
  buyDialogVisible.value = true
}

// 切换收藏状态
function toggleFavorite(product) {
  // 使用传入的product参数的副本，避免状态共享问题
  const productCopy = { ...product };
  if (productCopy.isFavorite) {
    // 取消收藏
    const favoriteParams = {
      pageNum: 1,
      pageSize: 1,
      userId: getCurrentUserId(),
      productId: productCopy.id
    }
    
    listFavorites(favoriteParams).then(response => {
      if (response.rows && response.rows.length > 0) {
        const favoriteId = response.rows[0].id
        delFavorites(favoriteId).then(() => {
          product.isFavorite = false  // 注意：这里仍然更新原对象的属性，以保持UI响应性
          proxy.$modal.msgSuccess("已取消收藏")
        })
      }
    })
  } else {
    // 添加收藏
    const favoriteData = {
      userId: getCurrentUserId(),
      productId: productCopy.id,
      createdAt: new Date().toISOString().split('T')[0]
    }
    
    addFavorites(favoriteData).then(response => {
      product.isFavorite = true  // 注意：这里仍然更新原对象的属性，以保持UI响应性
      proxy.$modal.msgSuccess("收藏成功")
    }).catch(error => {
      proxy.$modal.msgError("收藏失败：" + error.message)
    })
  }
}

// 投诉商品
function complainProduct(product) {
  // 使用传入的product参数的深拷贝，完全隔离状态
  selectedProduct.value = { ...product };
  complaintForm.productId = product.id
  complaintForm.complainantId = getCurrentUserId()
  complaintForm.accusedId = product.userId // 设置被投诉人为商品卖家
  complaintForm.createdAt = new Date().toISOString().split('T')[0] // 自动填写提交时间
  complaintDialogVisible.value = true
}

// 提交投诉
function submitComplaint() {
  proxy.$refs["complaintRef"].validate(valid => {
    if (valid) {
      addComplaints(complaintForm).then(response => {
        proxy.$modal.msgSuccess("投诉提交成功")
        complaintDialogVisible.value = false
        // 重置投诉表单
        complaintForm.title = ''
        complaintForm.content = ''
        complaintForm.orderId = null
        complaintForm.productId = null
        complaintForm.complainantId = null
        complaintForm.accusedId = null
        complaintForm.createdAt = null
      }).catch(error => {
        proxy.$modal.msgError("投诉提交失败：" + error.message)
      })
    }
  })
}

// 获取新旧程度文本
function getConditionText(condition) {
  const conditions = {
    '1': '全新',
    '2': '9成新',
    '3': '8成新',
    '4': '7成新',
    '5': '6成新及以下'
  }
  return conditions[condition] || '未知'
}

// 获取图片URL数组
function getImageUrls(imageUrl) {
  if (!imageUrl) {
    return [];
  }
  
  if (Array.isArray(imageUrl)) {
    return imageUrl.map(url => getSafeImageUrl(url));
  }
  
  if (typeof imageUrl === 'string') {
    // 去除首尾空格
    imageUrl = imageUrl.trim();
    if (imageUrl.includes(',')) {
      // 分割并返回所有非空URL
      const urls = imageUrl.split(',').map(url => url.trim()).filter(url => url);
      return urls.map(url => getSafeImageUrl(url));
    }
    return [getSafeImageUrl(imageUrl)];
  }
  
  return ['https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg'];
}

// 获取安全的图片URL
function getSafeImageUrl(imageUrl) {
  if (!imageUrl) {
    return 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg'; // 默认图片
  }
  
  // 如果是数组，取第一张图片
  if (Array.isArray(imageUrl)) {
    return imageUrl.length > 0 ? imageUrl[0] : 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
  }
  
  // 如果是字符串但包含多个URL（可能以逗号或其他分隔符分隔），取第一个
  if (typeof imageUrl === 'string') {
    // 去除首尾空格
    imageUrl = imageUrl.trim();
    if (imageUrl.includes(',')) {
      // 分割并取第一个非空URL
      const urls = imageUrl.split(',').map(url => url.trim()).filter(url => url);
      if (urls.length > 0) {
        imageUrl = urls[0];
      } else {
        return 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
      }
    }
    
    // 如果是相对路径（以/开头但不以http/https开头），添加基础URL
    if (imageUrl.startsWith('/') && !imageUrl.startsWith('http')) {
      // 检查是否已在代理路径中，ruoyi项目通常图片路径以/profile/开头
      if (imageUrl.startsWith('/profile/')) {
        // 检查是否已经在/dev-api或/prod-api下，避免重复添加
        if (!imageUrl.startsWith('/dev-api') && !imageUrl.startsWith('/prod-api')) {
          // 根据环境变量确定基础API路径
          const basePath = import.meta.env.VITE_APP_BASE_API || '/dev-api';
          return `${basePath}${imageUrl}`;
        }
      }
      return imageUrl; // 其他相对路径直接返回
    }
    
    return imageUrl;
  }
  
  return 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
}

// 打开图片查看器
function openImageViewer(index = 0) {
  currentImageUrlIndex.value = index;
  imageViewerVisible.value = true;
}

// 图片加载错误处理
function onImageError(event) {
  if (event && event.target) {
    event.target.src = 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
    event.target.onerror = null; // 防止无限循环
  }
}

// 新增：发布商品
function handleAdd() {
  resetProductForm()
  productDialogTitle.value = "发布商品"
  productDialogVisible.value = true
}

// 新增：编辑商品
function handleUpdate(product) {
  // 检查当前用户是否为商品发布者或拥有编辑权限
  const currentUserId = getCurrentUserId()
  const hasEditPermission = auth.hasPermi('campus:products:edit')
  
  if (product.userId !== currentUserId && !hasEditPermission) {
    proxy.$modal.msgError("您没有权限编辑此商品")
    return
  }
  
  resetProductForm()
  const productId = product.id
  getProducts(productId).then(response => {
    Object.assign(productForm, response.data)
    productDialogTitle.value = "编辑商品"
    productDialogVisible.value = true
  })
}

// 新增：重置商品表单
function resetProductForm() {
  Object.assign(productForm, {
    id: null,
    title: null,
    categoryId: null,
    price: null,
    conditions: null,
    imageUrls: null,
    description: null,
    status: 1,
    userId: getCurrentUserId() // 设置当前用户为商品发布者
  })
  if (productFormRef.value) {
    productFormRef.value.resetFields()
  }
}

// 新增：提交商品表单
function submitProductForm() {
  proxy.$refs["productFormRef"].validate(valid => {
    if (valid) {
      const formData = { ...productForm }
      if (formData.id != null) {
        // 更新商品
        updateProducts(formData).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          productDialogVisible.value = false
          getList()
        })
      } else {
        // 发布新商品
        addProducts(formData).then(response => {
          proxy.$modal.msgSuccess("发布成功")
          productDialogVisible.value = false
          getList()
        })
      }
    }
  })
}

// 处理全选
function handleCheckAllChange(val) {
  productsList.value.forEach(product => {
    product.checked = val;
  });
  updateCheckedStatus();
}

// 处理单个选择变化
function handleItemChange(val, product) {
  product.checked = val;
  updateCheckedStatus();
}

// 更新选中状态
function updateCheckedStatus() {
  const checkedCount = productsList.value.filter(p => p.checked).length;
  const totalCount = productsList.value.length;
  
  allChecked.value = checkedCount === totalCount && totalCount > 0;
  isIndeterminate.value = checkedCount > 0 && checkedCount < totalCount;
  
  // 更新ids数组
  ids.value = productsList.value.filter(p => p.checked).map(p => p.id);
  multiple.value = ids.value.length === 0;
}

// 单个删除
function handleDeleteOne(product) {
  const productIds = [product.id];
  deleteProducts(productIds);
}

// 批量删除
function handleDeleteSelected() {
  if (!ids.value.length) {
    proxy.$modal.msgError("请选择要删除的商品");
    return;
  }
  deleteProducts(ids.value);
}

// 删除商品的通用方法
function deleteProducts(productIds) {
  proxy.$modal.confirm(`是否确认删除编号为 "${productIds.join(", ")}" 的商品？`).then(() => {
    return batchDelProducts(productIds.join(","));
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

// 初始化
onMounted(() => {
  getCategoryList()
  getList()
})
</script>

<style lang="scss" scoped>
.filter-container {
  background: #fff;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,.1);
  
  .sort-options {
    display: flex;
    align-items: center;
    gap: 10px;
    
    span {
      margin-right: 10px;
      font-weight: bold;
    }
  }
}

.products-grid-container {
  margin-top: 15px;
}

.select-all-header {
  margin-bottom: 15px;
  padding: 0 15px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 两列布局 */
  gap: 25px;
  margin-bottom: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  height: 100%;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  }
  
  .product-checkbox {
    padding: 10px 10px 0 10px;
    display: flex;
    justify-content: flex-end;
  }
  
  .product-image {
    position: relative;
    height: 200px;
    overflow: hidden;
    cursor: pointer;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.05);
      }
    }
    
    .product-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background: #ff4949;
      color: #fff;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      z-index: 2;
    }
  }
  
  .product-info {
    padding: 15px;
    flex: 1;
    
    .product-title {
      font-size: 16px;
      font-weight: bold;
      margin: 0 0 8px 0;
      color: #303133;
      cursor: pointer;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
      
      &:hover {
        color: #409EFF;
      }
    }
    
    .product-description {
      font-size: 13px;
      color: #909399;
      margin: 0 0 10px 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .product-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      
      .product-price {
        font-size: 18px;
        font-weight: bold;
        color: #f56c6c;
      }
      
      .product-condition {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .product-seller {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #909399;
    }
  }
  
  .product-actions {
    padding: 0 15px 15px 15px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .el-button {
      flex: calc(50% - 4px);
      font-size: 12px;
      padding: 5px 8px;
      
      &:not(:nth-child(2n)) {
        margin-right: 4px;
      }
      
      &:nth-child(n+5) {
        margin-top: 5px;
      }
    }
  }
}

.product-detail {
  .detail-image {
    width: 100%;
    height: 300px;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .detail-price {
    font-size: 24px;
    color: #f56c6c;
    font-weight: bold;
    margin: 10px 0;
  }
  
  .detail-meta {
    margin: 15px 0;
    
    p {
      margin: 5px 0;
      color: #606266;
    }
  }
  
  .detail-actions {
    margin-top: 20px;
    
    .el-button {
      margin-right: 10px;
      margin-bottom: 10px;
    }
  }
  
  .detail-description {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #dcdfe6;
    
    h4 {
      margin-bottom: 10px;
    }
    
    p {
      line-height: 1.6;
      color: #606266;
    }
  }
  
  .product-reviews {
    margin-top: 30px;
    
    h4 {
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      border-bottom: 1px solid #dcdfe6;
      padding-bottom: 10px;
    }
    
    .review-form {
      margin-bottom: 25px;
      padding: 15px;
      background-color: #f5f7fa;
      border-radius: 4px;
      
      .review-rating {
        margin: 10px 0;
        display: flex;
        align-items: center;
        
        span {
          margin-right: 10px;
        }
      }
    }
    
    .reviews-list {
      .review-item {
        padding: 15px;
        border: 1px solid #ebeef5;
        border-radius: 4px;
        margin-bottom: 10px;
        background-color: #fafafa;
        
        .review-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .reviewer {
            font-weight: bold;
            color: #303133;
          }
        }
        
        .review-content {
          margin-bottom: 5px;
          color: #606266;
          line-height: 1.5;
        }
        
        .review-time {
          font-size: 12px;
          color: #909399;
        }
      }
      
      .no-reviews {
        text-align: center;
        color: #909399;
        padding: 20px 0;
      }
    }
  }
}

.buy-product-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  
  .buy-product-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 15px;
  }
  
  .buy-product-details {
    flex: 1;
    
    h3 {
      margin: 0 0 5px 0;
      font-size: 16px;
    }
    
    .buy-product-price {
      font-size: 18px;
      color: #f56c6c;
      font-weight: bold;
      margin: 5px 0;
    }
  }
}

.buy-options {
  .total-price {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
    margin-top: 15px;
    text-align: center;
  }
}

.complaint-product-info {
  display: flex;
  align-items: center;
  
  .complaint-product-image {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 10px;
  }
}

// 图片容器样式
.image-container {
  position: relative;
  
  .image-count {
    position: absolute;
    top: 10px;
    right: 10px;
    background: rgba(0, 0, 0, 0.5);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    z-index: 10;
  }
  
  .thumbnail-preview {
    display: flex;
    gap: 10px;
    margin-top: 10px;
    flex-wrap: wrap;
    
    .thumbnail-item {
      width: 60px;
      height: 60px;
      border: 2px solid transparent;
      border-radius: 4px;
      overflow: hidden;
      cursor: pointer;
      
      &.active {
        border-color: #409EFF;
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

// 支付确认对话框样式
.payment-content {
  text-align: center;
  
  .payment-amount {
    font-size: 18px;
    margin-bottom: 20px;
    
    strong {
      color: #f56c6c;
    }
  }
  
  .qr-code-container {
    .qr-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .qr-simulator {
        width: 200px;
        height: 200px;
        border: 1px solid #dcdfe6;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 15px;
        background-color: #f8f9fa;
        font-size: 14px;
        color: #909399;
      }
    }
  }
}
</style>

<!-- 图片查看器 -->
<el-image-viewer 
  v-if="imageViewerVisible" 
  :url-list="getImageUrls(selectedProduct?.imageUrls)" 
  :initial-index="currentImageUrlIndex"
  @close="imageViewerVisible = false"
  @switch="(index) => currentImageUrlIndex = index"
/>