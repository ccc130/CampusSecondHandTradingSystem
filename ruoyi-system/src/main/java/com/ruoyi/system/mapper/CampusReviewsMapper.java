package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CampusReviews;

/**
 * 商品评价Mapper接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface CampusReviewsMapper 
{
    /**
     * 查询商品评价
     * 
     * @param id 商品评价主键
     * @return 商品评价
     */
    public CampusReviews selectCampusReviewsById(Long id);

    /**
     * 查询商品评价列表
     * 
     * @param campusReviews 商品评价
     * @return 商品评价集合
     */
    public List<CampusReviews> selectCampusReviewsList(CampusReviews campusReviews);

    /**
     * 新增商品评价
     * 
     * @param campusReviews 商品评价
     * @return 结果
     */
    public int insertCampusReviews(CampusReviews campusReviews);

    /**
     * 修改商品评价
     * 
     * @param campusReviews 商品评价
     * @return 结果
     */
    public int updateCampusReviews(CampusReviews campusReviews);

    /**
     * 删除商品评价
     * 
     * @param id 商品评价主键
     * @return 结果
     */
    public int deleteCampusReviewsById(Long id);

    /**
     * 批量删除商品评价
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCampusReviewsByIds(Long[] ids);
}
