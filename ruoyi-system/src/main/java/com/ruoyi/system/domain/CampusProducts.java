package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 浏览商品对象 campus_products
 * 
 * @author ccc
 * @date 2025-12-24
 */
public class CampusProducts extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long id;

    /** 发布者ID */
    @Excel(name = "发布者ID")
    private Long userId;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 商品标题 */
    @Excel(name = "商品标题")
    private String title;

    /** 商品描述 */
    private String description;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 新旧程度 */
    @Excel(name = "新旧程度")
    private Long conditions;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 图片 */
    @Excel(name = "图片")
    private String imageUrls;

    /** 浏览次数 */
    @Excel(name = "浏览次数")
    private Long viewCount;

    /** 发布时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setConditions(Long conditions) 
    {
        this.conditions = conditions;
    }

    public Long getConditions() 
    {
        return conditions;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setImageUrls(String imageUrls) 
    {
        this.imageUrls = imageUrls;
    }

    public String getImageUrls() 
    {
        return imageUrls;
    }

    public void setViewCount(Long viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount() 
    {
        return viewCount;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("categoryId", getCategoryId())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("price", getPrice())
            .append("conditions", getConditions())
            .append("status", getStatus())
            .append("imageUrls", getImageUrls())
            .append("viewCount", getViewCount())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
