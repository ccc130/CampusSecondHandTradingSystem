package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 投诉商品对象 campus_complaints
 * 
 * @author ccc
 * @date 2025-12-24
 */
public class CampusComplaints extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 投诉ID */
    private Long id;

    /** 投诉人 */
    @Excel(name = "投诉人")
    private Long complainantId;

    /** 被投诉人 */
    @Excel(name = "被投诉人")
    private Long accusedId;

    /** 涉及商品 */
    @Excel(name = "涉及商品")
    private Long productId;

    /** 涉及订单 */
    @Excel(name = "涉及订单")
    private Long orderId;

    /** 投诉标题 */
    @Excel(name = "投诉标题")
    private String title;

    /** 详细描述 */
    @Excel(name = "详细描述")
    private String content;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private Long status;

    /** 处理人 */
    @Excel(name = "处理人")
    private Long handlerId;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date handledAt;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setComplainantId(Long complainantId) 
    {
        this.complainantId = complainantId;
    }

    public Long getComplainantId() 
    {
        return complainantId;
    }

    public void setAccusedId(Long accusedId) 
    {
        this.accusedId = accusedId;
    }

    public Long getAccusedId() 
    {
        return accusedId;
    }

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setHandlerId(Long handlerId) 
    {
        this.handlerId = handlerId;
    }

    public Long getHandlerId() 
    {
        return handlerId;
    }

    public void setHandledAt(Date handledAt) 
    {
        this.handledAt = handledAt;
    }

    public Date getHandledAt() 
    {
        return handledAt;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("complainantId", getComplainantId())
            .append("accusedId", getAccusedId())
            .append("productId", getProductId())
            .append("orderId", getOrderId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("status", getStatus())
            .append("handlerId", getHandlerId())
            .append("handledAt", getHandledAt())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
