<template>
  <div class="app-container">
    <!-- 购物车头部信息 -->
    <div class="cart-header">
      <h2>我的购物车</h2>
      <div class="cart-summary">
        <span>共 {{ total }} 件商品</span>
        <el-button type="danger" icon="Delete" @click="handleBatchDelete" :disabled="multiple">批量删除</el-button>
      </div>
    </div>

    <!-- 购物车商品列表 -->
    <div class="cart-items">
      <div 
        v-for="cart in cartsList" 
        :key="cart.id" 
        class="cart-item"
        :class="{ 'selected': selectedIds.includes(cart.id) }"
      >
        <el-checkbox 
          v-model="cart.checked" 
          @change="handleSelectionChange"
          class="cart-checkbox"
        />
        
        <div class="cart-item-image">
          <img :src="cart.productImageUrls" :alt="cart.productTitle" @error="onImageError" />
        </div>
        
        <div class="cart-item-info">
          <h3 class="cart-item-title">{{ cart.productTitle }}</h3>
          <p class="cart-item-description">{{ cart.productDescription?.substring(0, 60) }}...</p>
          <div class="cart-item-meta">
            <span class="cart-item-price">¥{{ cart.productPrice }}</span>
            <span class="cart-item-condition">成新度: {{ getConditionText(cart.productCondition) }}</span>
          </div>
        </div>
        
        <div class="cart-item-total">
          <span class="item-total-price">¥{{ cart.productPrice.toFixed(2) }}</span>
        </div>
        
        <div class="cart-item-actions">
          <el-button type="primary" size="small" icon="ShoppingBag" @click="buyNow(cart)">立即购买</el-button>
          <el-button type="danger" size="small" icon="Delete" @click="handleDelete(cart)">删除</el-button>
        </div>
      </div>
    </div>

    <!-- 购物车底部操作栏 -->
    <div class="cart-footer" v-if="cartsList.length > 0">
      <div class="cart-footer-left">
        <el-checkbox 
          v-model="allChecked" 
          @change="handleSelectAll"
        >
          全选
        </el-checkbox>
        <el-button link @click="handleBatchDelete" :disabled="!hasSelected">删除选中</el-button>
      </div>
      <div class="cart-footer-right">
        <div class="cart-total-info">
          <span>已选 {{ selectedCount }} 件商品，</span>
          <span>总计: <strong class="total-price">¥{{ selectedTotalPrice.toFixed(2) }}</strong></span>
        </div>
        <el-button 
          type="danger" 
          size="large" 
          icon="ShoppingBag"
          :disabled="!hasSelected"
          @click="checkoutSelected"
        >
          去结算
        </el-button>
      </div>
    </div>

    <!-- 空购物车提示 -->
    <div v-if="cartsList.length === 0" class="empty-cart">
      <el-empty description="购物车空空如也" :image-size="200" />
      <el-button type="primary" size="large" @click="goToProducts">去逛逛</el-button>
    </div>
    
    <!-- 购买商品对话框 -->
    <el-dialog title="确认购买" v-model="buyDialogVisible" width="400px" append-to-body>
      <div v-if="selectedProduct">
        <div class="buy-product-info">
          <img :src="selectedProduct.imageUrls" :alt="selectedProduct.title" class="buy-product-image" @error="onImageError" />
          <div class="buy-product-details">
            <h3>{{ selectedProduct.title }}</h3>
            <p class="buy-product-price">¥{{ selectedProduct.price }}</p>
            <p>卖家: {{ selectedProduct.userId }}</p>
          </div>
        </div>
        <div class="buy-options">
          <p class="total-price">总计: ¥{{ selectedProduct.price.toFixed(2) }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="buyDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="showBuyPaymentDialog">去支付</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 结算弹窗 - 现在用于购物车结算和立即购买 -->
    <el-dialog title="订单结算" v-model="showCheckoutDialog" width="600px" append-to-body>
      <div class="checkout-content">
        <h3>确认订单信息</h3>
        <!-- 立即购买的订单信息 -->
        <div v-if="selectedProduct && !cartsList.some(c => c.checked)" class="selected-item">
          <div class="item-info">
            <p class="item-title">{{ selectedProduct.title }}</p>
            <p class="item-price">¥{{ selectedProduct.price }} × 1</p>
          </div>
          <div class="item-total">¥{{ selectedProduct.price.toFixed(2) }}</div>
        </div>
        <!-- 购物车选中商品的订单信息 -->
        <div v-else class="selected-items">
          <div 
            v-for="cart in cartsList.filter(c => c.checked)" 
            :key="cart.id" 
            class="selected-item"
          >
            <div class="item-info">
              <p class="item-title">{{ cart.productTitle }}</p>
              <p class="item-price">¥{{ cart.productPrice }} × 1</p>
            </div>
            <div class="item-total">¥{{ cart.productPrice.toFixed(2) }}</div>
          </div>
        </div>
        <div class="checkout-summary">
          <div class="summary-row">
            <span>商品总计</span>
            <span>¥{{ selectedTotalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费</span>
            <span>¥0.00</span>
          </div>
          <div class="summary-row total-row">
            <span>合计</span>
            <span class="total-amount">¥{{ selectedTotalPrice.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCheckoutDialog = false">取 消</el-button>
          <el-button type="primary" @click="completeCheckout">确 认 支 付</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 支付二维码弹窗 -->
    <el-dialog title="扫码支付" v-model="showPaymentDialog" width="400px" append-to-body>
      <div class="payment-content">
        <p class="payment-amount">支付金额: <strong>¥{{ selectedTotalPrice.toFixed(2) }}</strong></p>
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
          <el-button @click="showPaymentDialog = false">取消支付</el-button>
          <el-button type="primary" @click="confirmPayment">确认已支付</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Carts">
import { listCarts, getCarts, delCarts, addCarts, updateCarts } from "@/api/campus/carts"
import { listProducts, getProducts, updateProducts } from "@/api/campus/products"
import { addOrders } from "@/api/campus/orders"
import useUserStore from '@/store/modules/user'
import payImage from "@/assets/images/pay.jpg"

const { proxy } = getCurrentInstance()

const cartsList = ref([])
const loading = ref(true)
const total = ref(0)
const ids = ref([])
const selectedIds = ref([])
const multiple = ref(true)
const allChecked = ref(false)
const hasSelected = ref(false)
const selectedCount = ref(0)
const selectedTotalPrice = ref(0)
const showCheckoutDialog = ref(false)  // 添加结算弹窗控制变量
const showPaymentDialog = ref(false)   // 添加支付弹窗控制变量
const buyDialogVisible = ref(false)    // 购买对话框控制变量
const selectedProduct = ref(null)      // 选中的商品
const paymentQrCode = ref(payImage)   // 支付二维码图片路径

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 9999, // 获取所有购物车项目
    userId: null, // 应该从当前用户获取
  }
})

const { queryParams } = toRefs(data)

/** 查询购物车列表 */
function getList() {
  loading.value = true
  // 获取当前用户ID
  queryParams.value.userId = getCurrentUserId()
  
  listCarts(queryParams.value).then(response => {
    const cartItems = response.rows
    total.value = response.total
    
    // 保存当前的选中状态
    const currentCheckedStatus = new Map()
    if (cartsList.value && cartsList.value.length > 0) {
      cartsList.value.forEach(cart => {
        if (cart.checked) {
          currentCheckedStatus.set(cart.id, true)
        }
      })
    }
    
    // 获取商品详细信息并合并到购物车数据中
    const cartPromises = cartItems.map(cart => {
      // 使用立即执行函数表达式（IIFE）或创建局部作用域防止变量共享
      const cartCopy = { ...cart }; // 创建cart的副本
      return new Promise((resolve) => {
        // 获取商品详细信息
        getProducts(cartCopy.productId).then(productResponse => {
          if (productResponse.data) {
            const product = productResponse.data
            // 将商品信息合并到购物车项目中
            // 使用与商品列表页面一致的图片处理逻辑
            const firstImageUrl = getImageUrls(product.imageUrls)[0] || product.imageUrls;
            Object.assign(cartCopy, {
              productTitle: product.title,
              productDescription: product.description,
              productPrice: product.price,
              productImageUrls: firstImageUrl,
              productCondition: product.conditions,
              productUserId: product.userId, // 保存商品的卖家ID
              // 保留原有的选中状态
              checked: currentCheckedStatus.has(cartCopy.id)
            })
          } else {
            // 如果没有找到商品信息，仍保留原有数据
            Object.assign(cartCopy, {
              productTitle: '未知商品',
              productDescription: '商品信息获取失败',
              productPrice: 0,
              productImageUrls: '',
              productCondition: '',
              productUserId: null, // 未知卖家ID
              // 保留原有的选中状态
              checked: currentCheckedStatus.has(cartCopy.id)
            })
          }
          resolve(cartCopy)
        }).catch(() => {
          // 如果获取商品信息失败，仍保留原有数据
          Object.assign(cartCopy, {
            productTitle: '未知商品',
            productDescription: '商品信息获取失败',
            productPrice: 0,
            productImageUrls: '',
            productCondition: '',
            productUserId: null, // 未知卖家ID
            // 保留原有的选中状态
            checked: currentCheckedStatus.has(cartCopy.id)
          })
          resolve(cartCopy)
        })
      })
    })
    
    Promise.all(cartPromises).then(results => {
      // 一次性更新整个列表，避免分步更新导致的不一致
      cartsList.value = results
      loading.value = false
      // 确保选中状态与新数据同步
      updateSelectionStatus()
    })
  })
}

// 获取当前用户ID
function getCurrentUserId() {
  // 从store中获取当前用户ID
  const userStore = useUserStore();
  return userStore.id;
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

// 图片加载错误处理
function onImageError(event) {
  if (event && event.target) {
    event.target.src = 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
    event.target.onerror = null; // 防止无限循环
  }
}

// 更新购物车中商品数量
function updateCartQuantity(cart) {
  // 数量选择功能已移除，固定为1
  proxy.$modal.msgWarning("数量已固定为1")
  cart.quantity = 1
  calculateSelectedInfo()
}

// 单个商品选择变化
function handleSelectionChange() {
  // 由于v-model绑定，cart.checked状态已经自动更新
  // 只需要更新选中状态即可
  updateSelectionStatus()
}

// 全选/取消全选
function handleSelectAll(checked) {
  if (cartsList.value && cartsList.value.length > 0) {
    cartsList.value.forEach(cart => {
      cart.checked = checked
    })
  }
  updateSelectionStatus()
}

// 更新选中状态
function updateSelectionStatus() {
  selectedIds.value = cartsList.value.filter(cart => cart.checked).map(cart => cart.id)
  multiple.value = selectedIds.value.length === 0
  hasSelected.value = selectedIds.value.length > 0
  selectedCount.value = selectedIds.value.length
  allChecked.value = cartsList.value.length > 0 && cartsList.value.every(cart => cart.checked)
  
  calculateSelectedInfo()
}

// 计算选中商品的总价
function calculateSelectedInfo() {
  if (cartsList.value && cartsList.value.length > 0) {
    selectedTotalPrice.value = cartsList.value
      .filter(cart => cart.checked)
      .reduce((total, cart) => total + (cart.productPrice || 0), 0)
  } else {
    selectedTotalPrice.value = 0
  }
}

// 立即购买
function buyNow(cart) {
  // 创建cart的副本，确保使用正确的数据
  const cartCopy = { ...cart };
  // 从商品API获取完整商品信息，确保有正确的商品详情
  getProducts(cartCopy.productId).then(productResponse => {
    if (productResponse.data) {
      const product = productResponse.data
      // 将完整商品信息赋值给selectedProduct
      // 使用与商品列表页面一致的图片处理逻辑
      const firstImageUrl = getImageUrls(product.imageUrls)[0] || product.imageUrls;
      selectedProduct.value = {
        id: product.id,
        title: product.title,
        price: product.price,
        imageUrls: firstImageUrl,
        userId: product.userId,
        description: product.description,
        condition: product.conditions
      }
      // 设置总价用于支付
      selectedTotalPrice.value = product.price
      buyDialogVisible.value = true
    } else {
      proxy.$modal.msgError("商品信息获取失败")
    }
  }).catch(error => {
    console.error("获取商品详情失败", error)
    proxy.$modal.msgError("商品信息获取失败")
  })
}

// 显示支付对话框
function showBuyPaymentDialog() {
  buyDialogVisible.value = false
  // 确保支付金额已设置
  if (selectedProduct.value) {
    selectedTotalPrice.value = selectedProduct.value.price
  }
  showCheckoutDialog.value = true // 使用与购物车结算相同的结算对话框
}

// 完成结算 - 现在用于立即购买流程
function completeCheckout() {
  showCheckoutDialog.value = false
  // 打开支付二维码弹窗
  showPaymentDialog.value = true
  proxy.$modal.msgSuccess("请扫描二维码完成支付")
}

// 确认支付 - 处理立即购买和购物车结算
function confirmPayment() {
  showPaymentDialog.value = false
  proxy.$modal.msgSuccess("支付成功！")
  
  // 区分是购物车结算还是立即购买
  const isFromCartCheckout = cartsList.value.some(cart => cart.checked) // 检查是否有选中的购物车项目
  let orderPromises = []
  let itemsToProcess = []
  
  if (isFromCartCheckout) {
    // 购物车结算流程
    const selectedCarts = cartsList.value.filter(cart => cart.checked)
    itemsToProcess = selectedCarts
    
    selectedCarts.forEach(cart => {
      // 创建cart的副本，确保使用正确的数据
      const cartCopy = { ...cart };
      const orderData = {
        buyerId: getCurrentUserId(),
        sellerId: cartCopy.productUserId, // 使用商品的卖家ID
        productId: cartCopy.productId,
        quantity: 1, // 固定为1
        totalPrice: cartCopy.productPrice, // 价格即为总价
        status: 2, // 已付款，待发货
        createdAt: new Date().toISOString().split('T')[0],
        completedAt: null
      }
      
      orderPromises.push(addOrders(orderData))
    })
  } else if (selectedProduct.value) {
    // 立即购买流程
    itemsToProcess = [selectedProduct.value]
    
    const orderData = {
      buyerId: getCurrentUserId(),
      sellerId: selectedProduct.value.userId, // 使用商品的卖家ID
      productId: selectedProduct.value.id,
      quantity: 1, // 固定为1
      totalPrice: selectedProduct.value.price, // 价格即为总价
      status: 2, // 已付款，待发货
      createdAt: new Date().toISOString().split('T')[0],
      completedAt: null
    }
    
    orderPromises.push(addOrders(orderData))
  }
  
  // 批量创建订单
  Promise.all(orderPromises)
    .then(() => {
      proxy.$modal.msgSuccess(`成功创建 ${itemsToProcess.length} 个订单`)
      
      // 更新对应商品的状态（将商品状态更新为已售罄）
      const updateProductPromises = itemsToProcess.map(item => {
        const productId = item.productId || item.id
        // 获取商品详细信息
        return getProducts(productId).then(productResponse => {
          const product = productResponse.data
          // 更新商品状态为已售罄
          product.status = 0 // 已售罄
          return updateProducts(product)
        }).catch(error => {
          console.error("更新商品状态失败", error)
        })
      })
      
      // 等待所有商品状态更新完成
      return Promise.all(updateProductPromises)
    })
    .then(() => {
      if (isFromCartCheckout) {
        // 清除已支付的商品（从购物车中删除选中的商品）
        const deletePromises = selectedIds.value.map(id => delCarts(id))
        return Promise.all(deletePromises)
      } else {
        // 立即购买不需要删除购物车项目（因为商品原本就不在购物车中选中状态）
        // 但需要重置selectedProduct
        selectedProduct.value = null
        return Promise.resolve()
      }
    })
    .then(() => {
      if (isFromCartCheckout) {
        proxy.$modal.msgSuccess(`成功清除 ${selectedIds.value.length} 件已支付商品`)
      }
      // 刷新购物车列表
      getList()
      // 重置选中状态
      selectedIds.value = []
      updateSelectionStatus()
    })
    .catch(error => {
      proxy.$modal.msgError("创建订单或清除购物车失败：" + error.message)
      // 仍然刷新列表以确保状态一致
      getList()
    })
}

// 批量删除
function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    proxy.$modal.msgError("请至少选择一件商品")
    return
  }
  
  proxy.$modal.confirm(`确定要删除选中的 ${selectedIds.value.length} 件商品吗？`)
    .then(() => {
      const deletePromises = selectedIds.value.map(id => delCarts(id))
      return Promise.all(deletePromises)
    })
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      // 清空选中状态，避免状态残留
      selectedIds.value = []
      // 重新获取数据以确保前后端一致
      getList()
    })
    .catch(() => {})
}

// 删除单个商品
function handleDelete(cart) {
  // 创建cart的副本，确保使用正确的数据
  const cartCopy = { ...cart };
  proxy.$modal.confirm(`确定要从购物车中删除 ${cartCopy.productTitle} 吗？`)
    .then(() => {
      return delCarts(cartCopy.id)
    })
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

// 去结算选中商品
function checkoutSelected() {
  if (selectedIds.value.length === 0) {
    proxy.$modal.msgError("请至少选择一件商品")
    return
  }
  
  // 打开结算弹窗
  proxy.$modal.msgSuccess("正在打开结算弹窗...")
  showCheckoutDialog.value = true
}

// 跳转到商品列表页
function goToProducts() {
  // 使用proxy访问router进行页面跳转到商品列表页
  proxy.$router.push('/products')
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
    
    // 如果是相对路径但不是以/开头，添加基础API路径
    if (!imageUrl.startsWith('http') && !imageUrl.startsWith('/')) {
      const basePath = import.meta.env.VITE_APP_BASE_API || '/dev-api';
      return `${basePath}${imageUrl}`;
    }
    
    return imageUrl;
  }
  
  return 'https://cube.elemecdn.com/e/fd/0fc72a63c3d713a467e6e7c37f6b4jpeg.jpeg';
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #e4e7ed;
  
  h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
  }
  
  .cart-summary {
    display: flex;
    align-items: center;
    gap: 15px;
    
    span {
      color: #606266;
    }
  }
}

.cart-items {
  margin: 20px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 15px;
  background-color: #fff;
  transition: all 0.3s;
  
  &.selected {
    border-color: #409EFF;
    box-shadow: 0 0 10px rgba(64, 158, 255, 0.2);
  }
  
  .cart-checkbox {
    margin-right: 20px;
    flex-shrink: 0;
  }
  
  .cart-item-image {
    width: 100px;
    height: 100px;
    margin-right: 20px;
    flex-shrink: 0;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 4px;
    }
  }
  
  .cart-item-info {
    flex: 1;
    
    .cart-item-title {
      margin: 0 0 8px 0;
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      cursor: pointer;
      
      &:hover {
        color: #409EFF;
      }
    }
    
    .cart-item-description {
      margin: 0 0 10px 0;
      font-size: 13px;
      color: #909399;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .cart-item-meta {
      display: flex;
      gap: 15px;
      
      .cart-item-price {
        font-size: 18px;
        font-weight: bold;
        color: #f56c6c;
      }
      
      .cart-item-condition {
        font-size: 12px;
        color: #909399;
        background-color: #f4f4f5;
        padding: 2px 6px;
        border-radius: 4px;
      }
    }
  }
  
  .cart-item-quantity {
    width: 80px;
    margin: 0 20px;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .cart-item-total {
    width: 120px;
    text-align: center;
    margin-right: 20px;
    flex-shrink: 0;
    
    .item-total-price {
      font-size: 16px;
      font-weight: bold;
      color: #f56c6c;
    }
  }
  
  .cart-item-actions {
    width: 160px;
    display: flex;
    gap: 10px;
    flex-shrink: 0;
  }
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-top: 20px;
  
  .cart-footer-left {
    display: flex;
    align-items: center;
    gap: 15px;
  }
  
  .cart-footer-right {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .cart-total-info {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      
      .total-price {
        font-size: 22px;
        color: #f56c6c;
      }
    }
  }
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  text-align: center;
}

.checkout-content {
  .selected-items {
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 20px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 10px;
    
    .selected-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px solid #f2f2f2;
      
      &:last-child {
        border-bottom: none;
      }
      
      .item-info {
        flex: 1;
        
        .item-title {
          margin: 0 0 5px 0;
          font-size: 14px;
          color: #303133;
        }
        
        .item-price {
          margin: 0;
          font-size: 12px;
          color: #909399;
        }
      }
      
      .item-total {
        font-weight: bold;
        color: #f56c6c;
      }
    }
  }
  
  .checkout-summary {
    border-top: 1px solid #ebeef5;
    padding-top: 15px;
    
    .summary-row {
      display: flex;
      justify-content: space-between;
      padding: 5px 0;
      font-size: 14px;
      color: #606266;
      
      &.total-row {
        font-weight: bold;
        font-size: 16px;
        color: #303133;
        padding-top: 10px;
        
        .total-amount {
          color: #f56c6c;
          font-size: 18px;
        }
      }
    }
  }
}

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

.buy-product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.payment-content {
    }

@media (max-width: 768px) {
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    
    .cart-checkbox {
      margin-bottom: 15px;
    }
    
    .cart-item-image {
      margin: 0 0 15px 30px;
    }
    
    .cart-item-info {
      width: 100%;
      margin-left: 30px;
    }
    
    .cart-item-quantity,
    .cart-item-total,
    .cart-item-actions {
      width: 100%;
      margin: 10px 0;
      display: flex;
      justify-content: center;
    }
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 15px;
    
    .cart-footer-right {
      flex-direction: column;
      gap: 15px;
      
      .cart-total-info {
        align-items: center;
      }
    }
  }
}
</style>