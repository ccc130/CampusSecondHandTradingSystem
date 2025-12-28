<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品" prop="productId">
        <el-select
          v-model="queryParams.productId"
          filterable
          remote
          reserve-keyword
          placeholder="请输入商品ID或名称"
          :remote-method="remoteMethod"
          :loading="searchLoading"
          clearable
          @change="handleQuery"
        >
          <el-option
            v-for="item in searchOptions"
            :key="item.id"
            :label="item.title"
            :value="item.id"
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
          type="primary"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleBatchDelete"
          v-hasPermi="['campus:favorites:remove']"
        >批量删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 收藏商品网格展示 -->
    <div class="favorites-grid" v-loading="loading">
      <div 
        v-for="favorite in favoriteProducts" 
        :key="favorite.id" 
        class="favorite-item"
      >
        <div class="favorite-checkbox">
          <el-checkbox 
            v-model="favorite.checked" 
            @change="handleSelectionChange([favorite])"
          />
        </div>
        
        <div class="favorite-item-image">
          <img :src="favorite.productImageUrls" :alt="favorite.productTitle" @error="onImageError" />
        </div>
        
        <div class="favorite-item-info">
          <h3 class="favorite-item-title">{{ favorite.productTitle }}</h3>
          <p class="favorite-item-description">{{ favorite.productDescription?.substring(0, 60) }}...</p>
          <div class="favorite-item-meta">
            <span class="favorite-item-price">¥{{ favorite.productPrice }}</span>
            <span class="favorite-item-condition">成新度: {{ getConditionText(favorite.productCondition) }}</span>
          </div>
          <div class="favorite-item-seller">
            <span>卖家: {{ favorite.productUserId }}</span>
            <span>收藏: {{ parseTime(favorite.createdAt, '{y}-{m}-{d}') }}</span>
          </div>
        </div>
        
        <div class="favorite-item-actions">
          <el-button 
            type="danger" 
            size="small" 
            icon="ShoppingBag"
            @click="buyNow(favorite)"
            :disabled="favorite.productStatus === 0"
          >
            立即购买
          </el-button>
          <el-button 
            type="success" 
            size="small" 
            icon="ShoppingCart"
            @click="addToCart(favorite)"
            :disabled="favorite.productStatus === 0"
          >
            加入购物车
          </el-button>
          <el-button 
            type="warning" 
            size="small" 
            icon="Star"
            @click="removeFromFavorites(favorite)"
          >
            取消收藏
          </el-button>
          <el-button 
            type="info" 
            size="small" 
            icon="View"
            @click="viewProduct(favorite)"
          >
            查看详情
          </el-button>
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

    <!-- 空收藏提示 -->
    <div v-if="favoriteProducts.length === 0 && !loading" class="empty-favorites">
      <el-empty description="暂无收藏商品" :image-size="200" />
    </div>
    
    <!-- 底部操作栏 -->
    <div class="favorites-footer" v-if="favoriteProducts.length > 0">
      <div class="favorites-footer-left">
        <el-checkbox 
          v-model="allChecked" 
          @change="handleSelectAll"
        >
          全选
        </el-checkbox>
        <el-button link @click="handleBatchDelete" :disabled="!hasSelected">删除选中</el-button>
      </div>
      <div class="favorites-footer-right">
        <div class="favorites-total-info">
          <span>已选 {{ selectedCount }} 件商品</span>
        </div>
      </div>
    </div>
    
    <!-- 商品详情对话框 -->
    <el-dialog title="商品详情" v-model="productDetailVisible" width="60%" append-to-body>
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="image-container">
              <img 
                :src="getSafeImageUrl(selectedProduct.imageUrls)" 
                :alt="selectedProduct.title" 
                class="detail-image" 
                @error="onImageError" 
                style="cursor: pointer; width: 100%; height: 300px; object-fit: cover; border-radius: 8px;"
              />
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
          </el-col>
        </el-row>
        <div class="detail-description">
          <h4>商品描述</h4>
          <p>{{ selectedProduct.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="Favorites">
import { listFavorites, getFavorites, delFavorites, addFavorites, updateFavorites } from "@/api/campus/favorites"
import { listProducts, getProducts, updateProducts } from "@/api/campus/products"
import { addCarts } from "@/api/campus/carts"
import { addOrders } from "@/api/campus/orders"
import { addReviews } from "@/api/campus/reviews"
import { getUser } from "@/api/system/user"
import useUserStore from '@/store/modules/user'
import { checkPermi } from '@/utils/permission'

const { proxy } = getCurrentInstance()

const favoritesList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const daterangeCreatedAt = ref([])
// 收藏商品列表（包含商品详细信息）
const favoriteProducts = ref([])
// 选中状态
const selectedIds = ref([])
const allChecked = ref(false)
const hasSelected = ref(false)
const selectedCount = ref(0)
// 商品详情相关
const productDetailVisible = ref(false)
const selectedProduct = ref(null)
const productReviews = ref([])
const newReviewContent = ref('')
const newReviewRating = ref(0)
// 图片相关
const currentImageUrlIndex = ref(0)
// 用于存储用户映射关系
const userMap = ref({})
// 搜索相关
const searchLoading = ref(false)
const searchOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: null,
    productId: null,
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询我的收藏列表 */
function getList() {
  loading.value = true
  queryParams.value.params = {}
  if (null != daterangeCreatedAt && '' != daterangeCreatedAt) {
    queryParams.value.params["beginCreatedAt"] = daterangeCreatedAt.value[0]
    queryParams.value.params["endCreatedAt"] = daterangeCreatedAt.value[1]
  }
  
  // 始终只获取当前用户的收藏
  const currentUserId = getCurrentUserId()
  const queryData = { ...queryParams.value }
  if (currentUserId) {
    queryData.userId = currentUserId
  }
  
  listFavorites(queryData).then(response => {
    const favoriteItems = response.rows
    total.value = response.total
    
    // 获取商品详细信息并合并到收藏数据中
    const favoritePromises = favoriteItems.map(favorite => {
      return new Promise((resolve) => {
        // 获取商品详细信息
        listProducts({ id: favorite.productId }).then(productResponse => {
          if (productResponse.rows && productResponse.rows.length > 0) {
            const product = productResponse.rows[0]
            // 将商品信息合并到收藏项目中
            Object.assign(favorite, {
              productTitle: product.title,
              productDescription: product.description,
              productPrice: product.price,
              productImageUrls: product.imageUrls,
              productCondition: product.conditions,
              productUserId: product.userId,
              productStatus: product.status
            })
          }
          resolve(favorite)
        }).catch(() => {
          // 如果获取商品信息失败，仍保留原有数据
          Object.assign(favorite, {
            productTitle: '未知商品',
            productDescription: '商品信息获取失败',
            productPrice: 0,
            productImageUrls: '',
            productCondition: '',
            productUserId: null,
            productStatus: null
          })
          resolve(favorite)
        })
      })
    })
    
    Promise.all(favoritePromises).then(results => {
      favoriteProducts.value = results
      loading.value = false
    })
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  // 更新选中状态
  favoriteProducts.value.forEach(favorite => {
    const isSelected = selection.some(item => item.id === favorite.id)
    favorite.checked = isSelected
  })
  
  updateSelectionStatus()
}

// 全选/取消全选
function handleSelectAll(checked) {
  favoriteProducts.value.forEach(favorite => {
    favorite.checked = checked
  })
  updateSelectionStatus()
}

// 更新选中状态
function updateSelectionStatus() {
  selectedIds.value = favoriteProducts.value.filter(fav => fav.checked).map(fav => fav.id)
  multiple.value = selectedIds.value.length === 0
  hasSelected.value = selectedIds.value.length > 0
  selectedCount.value = selectedIds.value.length
  allChecked.value = favoriteProducts.value.length > 0 && favoriteProducts.value.every(fav => fav.checked)
}

// 批量删除
function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    proxy.$modal.msgError("请至少选择一件商品")
    return
  }
  
  proxy.$modal.confirm(`确定要删除选中的 ${selectedIds.value.length} 件收藏商品吗？`)
    .then(() => {
      const deletePromises = selectedIds.value.map(id => delFavorites(id))
      return Promise.all(deletePromises)
    })
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

// 取消收藏
function removeFromFavorites(favorite) {
  proxy.$modal.confirm(`确定要取消收藏 "${favorite.productTitle}" 吗？`)
    .then(() => {
      return delFavorites(favorite.id)
    })
    .then(() => {
      proxy.$modal.msgSuccess("已取消收藏")
      getList()
    })
    .catch(() => {})
}

// 加入购物车
function addToCart(favorite) {
  const cartData = {
    userId: getCurrentUserId(),
    productId: favorite.productId,
    quantity: 1, // 数量固定为1
    addedAt: new Date().toISOString().split('T')[0]
  }
  
  addCarts(cartData).then(response => {
    proxy.$modal.msgSuccess("已成功加入购物车")
  }).catch(error => {
    proxy.$modal.msgError("加入购物车失败：" + error.message)
  })
}

// 立即购买
function buyNow(favorite) {
  // 这里应该跳转到订单确认页面或直接购买
  proxy.$modal.confirm(`确定要立即购买 ${favorite.productTitle} 吗？`)
    .then(() => {
      // 创建订单
      const orderData = {
        buyerId: getCurrentUserId(),
        sellerId: favorite.productUserId,
        productId: favorite.productId,
        quantity: 1,
        totalPrice: favorite.productPrice,
        status: 2, // 已付款，待发货
        createdAt: new Date().toISOString().split('T')[0],
        completedAt: null
      }
      
      addOrders(orderData).then(response => {
        proxy.$modal.msgSuccess("购买成功")
        // 更新商品状态为已售罄
        updateProductStatus(favorite.productId, 0)
      }).catch(error => {
        proxy.$modal.msgError("购买失败：" + error.message)
      })
    })
}

// 查看商品详情
function viewProduct(favorite) {
  // 获取商品详细信息并显示详情弹窗
  getProducts(favorite.productId).then(response => {
    const product = response.data
    selectedProduct.value = product
    productDetailVisible.value = true
    
    // 加载商品评论
    getProductReviews(product.id)
    
    // 更新查看次数
    updateViewCount(product.id)
  }).catch(error => {
    proxy.$modal.msgError("获取商品详情失败：" + error.message)
  })
}

// 从商品详情对话框加入购物车
function addToCartFromDetail(product) {
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
  // 这里可以跳转到购物车或直接购买流程
  proxy.$modal.msgInfo("跳转到购买流程")
}

// 图片加载错误处理
function onImageError(event) {
  if (event && event.target) {
    event.target.src = 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
    event.target.onerror = null; // 防止无限循环
  }
}

// 更新商品查看次数
function updateViewCount(productId) {
  getProducts(productId).then(response => {
    const product = response.data
    product.viewCount = (product.viewCount || 0) + 1
    updateProducts(product).then(updateResponse => {
      console.log("商品查看次数已更新", updateResponse)
    }).catch(error => {
      console.error("更新商品查看次数失败", error)
    })
  }).catch(error => {
    console.error("获取商品信息失败", error)
  })
}

// 获取商品评论
function getProductReviews(productId) {
  // 这里可以实现获取商品评论的逻辑
  // 为了简化，暂时设置为空数组
  productReviews.value = []
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

// 获取用户名称
function getUserName(userId) {
  if (userMap.value[userId]) {
    return userMap.value[userId]
  }
  // 获取用户信息
  getUser(userId).then(response => {
    userMap.value[userId] = response.data.nickName || response.data.userName || `用户${userId}`
  }).catch(() => {
    userMap.value[userId] = `用户${userId}`
  })
  return userMap.value[userId] || `用户${userId}`
}

// 获取当前用户ID
function getCurrentUserId() {
  return useUserStore().id
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

// 远程搜索方法
function remoteMethod(query) {
  if (query !== '') {
    searchLoading.value = true
    
    // 根据输入的查询词进行搜索，可以是ID或标题
    const params = {
      pageNum: 1,
      pageSize: 100, // 限制返回结果数量
    }
    
    // 如果输入的是数字，可能是ID或标题
    if (/^\d+$/.test(query)) {
      // 同时按ID和标题搜索
      params.id = parseInt(query)
      params.title = query
    } else {
      // 按标题搜索
      params.title = query
    }
    
    listProducts(params).then(response => {
      searchOptions.value = response.rows
      searchLoading.value = false
    }).catch(() => {
      searchOptions.value = []
      searchLoading.value = false
    })
  } else {
    searchOptions.value = []
  }
}

// 搜索
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

// 重置
function resetQuery() {
  daterangeCreatedAt.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    userId: null,
    productId: null,
    createdAt: null
  }
  proxy.resetForm("favoritesRef")
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加我的收藏"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getFavorites(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改我的收藏"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["favoritesRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateFavorites(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addFavorites(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除我的收藏编号为"' + _ids + '"的数据项？').then(function() {
    return delFavorites(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('campus/favorites/export', {
    ...queryParams.value
  }, `favorites_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style lang="scss" scoped>
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin: 20px 0;
}

.favorite-item {
  display: flex;
  flex-direction: column;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fff;
  transition: all 0.3s;
  min-height: 300px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .favorite-checkbox {
    align-self: flex-start;
    margin-bottom: 10px;
  }

  .favorite-item-image {
    width: 100%;
    height: 150px;
    margin-bottom: 15px;
    border-radius: 4px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;

      &:hover {
        transform: scale(1.05);
      }
    }
  }

  .favorite-item-info {
    flex: 1;
    margin-bottom: 15px;

    .favorite-item-title {
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

    .favorite-item-description {
      font-size: 13px;
      color: #909399;
      margin: 0 0 10px 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .favorite-item-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .favorite-item-price {
        font-size: 18px;
        font-weight: bold;
        color: #f56c6c;
      }

      .favorite-item-condition {
        font-size: 12px;
        color: #909399;
        background-color: #f4f4f5;
        padding: 2px 6px;
        border-radius: 4px;
      }
    }

    .favorite-item-seller {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: #909399;
    }
  }

  .favorite-item-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    .el-button {
      flex: 1;
      min-width: 70px;
      font-size: 12px;
    }
  }
}

.favorites-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-top: 20px;

  .favorites-footer-left {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .favorites-footer-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .favorites-total-info {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
    }
  }
}

.empty-favorites {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  text-align: center;
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
}

// 响应式设计
@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }

  .favorites-footer {
    flex-direction: column;
    gap: 15px;

    .favorites-footer-right {
      flex-direction: column;
      align-items: center;
    }
  }
}
</style>
