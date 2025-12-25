package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品评价对象 campus_reviews
 * 
 * @author ccc
 * @date 2025-12-24
 */
public class CampusReviews extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long id;

    /** 关联订单 */
    @Excel(name = "关联订单")
    private Long orderId;

    /** 评价人 */
    @Excel(name = "评价人")
    private Long reviewerId;

    /** 被评价人 */
    @Excel(name = "被评价人")
    private Long reviewedUserId;

    /** 评分 */
    @Excel(name = "评分")
    private Long rating;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String comment;

    /** 评价时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评价时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setReviewerId(Long reviewerId) 
    {
        this.reviewerId = reviewerId;
    }

    public Long getReviewerId() 
    {
        return reviewerId;
    }

    public void setReviewedUserId(Long reviewedUserId) 
    {
        this.reviewedUserId = reviewedUserId;
    }

    public Long getReviewedUserId() 
    {
        return reviewedUserId;
    }

    public void setRating(Long rating) 
    {
        this.rating = rating;
    }

    public Long getRating() 
    {
        return rating;
    }

    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
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
            .append("orderId", getOrderId())
            .append("reviewerId", getReviewerId())
            .append("reviewedUserId", getReviewedUserId())
            .append("rating", getRating())
            .append("comment", getComment())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
