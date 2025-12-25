package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 我的订单对象 campus_orders
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public class CampusOrders extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 买家ID */
    @Excel(name = "买家ID")
    private Long buyerId;

    /** 卖家ID */
    @Excel(name = "卖家ID")
    private Long sellerId;

    /** 商品ID */
    @Excel(name = "商品ID")
    private Long productId;

    /** 购买数量 */
    @Excel(name = "购买数量")
    private Long quantity;

    /** 总价 */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

    /** 订单状态 */
    @Excel(name = "订单状态")
    private Long status;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "下单时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 交易完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "交易完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setBuyerId(Long buyerId) 
    {
        this.buyerId = buyerId;
    }

    public Long getBuyerId() 
    {
        return buyerId;
    }

    public void setSellerId(Long sellerId) 
    {
        this.sellerId = sellerId;
    }

    public Long getSellerId() 
    {
        return sellerId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setQuantity(Long quantity) 
    {
        this.quantity = quantity;
    }

    public Long getQuantity() 
    {
        return quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setCompletedAt(Date completedAt) 
    {
        this.completedAt = completedAt;
    }

    public Date getCompletedAt() 
    {
        return completedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("buyerId", getBuyerId())
            .append("sellerId", getSellerId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("totalPrice", getTotalPrice())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("completedAt", getCompletedAt())
            .toString();
    }
}
