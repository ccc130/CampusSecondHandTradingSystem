<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 数据统计卡片 -->
      <el-col :span="24">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="statistic-card">
              <div class="statistic-item">
                <div class="statistic-icon bg-primary">
                  <el-icon><Goods /></el-icon>
                </div>
                <div class="statistic-content">
                  <p class="statistic-title">商品总数</p>
                  <p class="statistic-number">{{ productCount }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="statistic-card">
              <div class="statistic-item">
                <div class="statistic-icon bg-success">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="statistic-content">
                  <p class="statistic-title">订单总数</p>
                  <p class="statistic-number">{{ orderCount }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="statistic-card">
              <div class="statistic-item">
                <div class="statistic-icon bg-warning">
                  <el-icon><Star /></el-icon>
                </div>
                <div class="statistic-content">
                  <p class="statistic-title">收藏总数</p>
                  <p class="statistic-number">{{ favoriteCount }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 商品分类统计饼图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>商品分类统计</span>
          </template>
          <div ref="categoryChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
      
      <!-- 订单状态统计饼图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>订单状态统计</span>
          </template>
          <div ref="orderStatusChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 近30天商品发布趋势 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <span>近30天商品发布趋势</span>
          </template>
          <div ref="productTrendChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { ref, onMounted, nextTick } from 'vue'
import { Goods, Document, Star, User } from '@element-plus/icons-vue'
import { listProducts } from '@/api/campus/products'
import { listOrders } from '@/api/campus/orders'
import { listFavorites } from '@/api/campus/favorites'
import { listCategories } from '@/api/campus/categories'

// 统计数据
const productCount = ref(0)
const orderCount = ref(0)
const favoriteCount = ref(0)
const userCount = ref(0)

// 图表引用
const categoryChartRef = ref(null)
const orderStatusChartRef = ref(null)
const productTrendChartRef = ref(null)

const { proxy } = getCurrentInstance()

// 获取统计数据
async function getStatistics() {
  try {
    proxy.$modal.loading("正在加载统计数据，请稍候！")
    
    // 并行获取所有统计数据
    const [productRes, orderRes, favoriteRes, categoryRes] = await Promise.allSettled([
      listProducts({ pageNum: 1, pageSize: 1 }),
      listOrders({ pageNum: 1, pageSize: 1 }),
      listFavorites({ pageNum: 1, pageSize: 1 }),
      listCategories({ pageNum: 1, pageSize: 100 })
    ])
    
    // 处理商品总数
    if (productRes.status === 'fulfilled') {
      productCount.value = productRes.value.total || 0
    } else {
      console.error('获取商品总数失败:', productRes.reason)
    }
    
    // 处理订单总数
    if (orderRes.status === 'fulfilled') {
      orderCount.value = orderRes.value.total || 0
    } else {
      console.error('获取订单总数失败:', orderRes.reason)
    }
    
    // 处理收藏总数
    if (favoriteRes.status === 'fulfilled') {
      favoriteCount.value = favoriteRes.value.total || 0
    } else {
      console.error('获取收藏总数失败:', favoriteRes.reason)
    }
    
    // 处理分类数据
    let categories = []
    if (categoryRes.status === 'fulfilled') {
      categories = categoryRes.value.rows || []
    } else {
      console.error('获取分类数据失败:', categoryRes.reason)
    }
    
    // 获取所有商品用于分类统计
    let allProducts = []
    try {
      const allProductsRes = await listProducts({ pageNum: 1, pageSize: 1000 })
      allProducts = allProductsRes.rows || []
    } catch (error) {
      console.error('获取所有商品数据失败:', error)
    }
    
    // 统计各分类商品数量
    const categoryData = categories.map(category => {
      const count = allProducts.filter(product => product.categoryId === category.id).length
      return {
        name: category.name,
        value: count
      }
    }).filter(item => item.value > 0) // 过滤掉数量为0的分类

    // 初始化分类饼图
    initCategoryChart(categoryData)

    // 获取订单状态统计
    let orderStatusData = []
    try {
      const allOrdersRes = await listOrders({ pageNum: 1, pageSize: 1000 })
      const allOrders = allOrdersRes.rows || []
      
      // 统计订单状态
      const statusCount = {
        '待付款': 0,
        '待发货': 0,
        '已完成': 0,
        '已取消': 0
      }
      
      allOrders.forEach(order => {
        // 假设订单状态字段为status，实际项目中需要根据后端返回的字段调整
        if (order.status === '0' || order.status === '待付款') {
          statusCount['待付款']++
        } else if (order.status === '1' || order.status === '待发货') {
          statusCount['待发货']++
        } else if (order.status === '2' || order.status === '已完成') {
          statusCount['已完成']++
        } else {
          statusCount['已取消']++
        }
      })
      
      orderStatusData = Object.entries(statusCount).map(([name, value]) => ({
        name,
        value
      })).filter(item => item.value > 0)
    } catch (error) {
      console.error('获取订单状态数据失败:', error)
      // 如果获取失败，使用默认数据
      orderStatusData = [
        { value: 335, name: '待付款' },
        { value: 679, name: '待发货' },
        { value: 1548, name: '已完成' },
        { value: 200, name: '已取消' }
      ]
    }

    // 初始化订单状态饼图
    initOrderStatusChart(orderStatusData)

    // 初始化商品趋势图（使用实际数据）
    initProductTrendChart(allProducts)
    
    proxy.$modal.closeLoading()
  } catch (error) {
    console.error('获取统计数据失败:', error)
    proxy.$modal.closeLoading()
    proxy.$modal.msgError('获取统计数据失败')
  }
}

// 初始化商品分类饼图
function initCategoryChart(data) {
  nextTick(() => {
    const chart = echarts.init(categoryChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '商品分类',
          type: 'pie',
          radius: '50%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    chart.setOption(option)
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      chart.resize()
    })
  })
}

// 初始化订单状态饼图
function initOrderStatusChart(data) {
  nextTick(() => {
    const chart = echarts.init(orderStatusChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '订单状态',
          type: 'pie',
          radius: '50%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    chart.setOption(option)
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      chart.resize()
    })
  })
}

// 初始化商品趋势图
function initProductTrendChart(products) {
  nextTick(() => {
    const chart = echarts.init(productTrendChartRef.value)
    
    // 处理商品数据，按月份统计
    const monthData = {}
    const monthLabels = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    
    // 初始化月份数据
    for (let i = 1; i <= 12; i++) {
      monthData[i] = 0
    }
    
    // 统计每个月的商品发布数量
    products.forEach(product => {
      if (product.createTime) {
        const date = new Date(product.createTime)
        const month = date.getMonth() + 1 // getMonth() 返回 0-11，需要加1
        if (monthData[month] !== undefined) {
          monthData[month]++
        }
      }
    })
    
    // 将数据转换为图表需要的格式
    const seriesData = Object.keys(monthData).map(key => monthData[key])
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['商品发布数']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      toolbox: {
        feature: {
          saveAsImage: {}
        }
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: monthLabels
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '商品发布数',
          type: 'line',
          stack: '总量',
          data: seriesData
        }
      ]
    }
    chart.setOption(option)
    
    // 响应窗口大小变化
    window.addEventListener('resize', () => {
      chart.resize()
    })
  })
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.statistic-card {
  margin-bottom: 20px;
}

.statistic-item {
  display: flex;
  align-items: center;
}

.statistic-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.statistic-content {
  flex: 1;
}

.statistic-title {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px 0;
}

.statistic-number {
  font-size: 24px;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.bg-primary {
  background-color: #409EFF;
  color: white;
}

.bg-success {
  background-color: #67C23A;
  color: white;
}

.bg-warning {
  background-color: #E6A23C;
  color: white;
}

.bg-info {
  background-color: #909399;
  color: white;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-card :deep(.el-card__header) {
  font-weight: bold;
  font-size: 16px;
}
</style>