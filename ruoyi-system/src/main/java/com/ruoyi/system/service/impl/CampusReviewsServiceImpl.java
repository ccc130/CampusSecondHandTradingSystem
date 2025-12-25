package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusReviewsMapper;
import com.ruoyi.system.domain.CampusReviews;
import com.ruoyi.system.service.ICampusReviewsService;

/**
 * 商品评价Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusReviewsServiceImpl implements ICampusReviewsService 
{
    @Autowired
    private CampusReviewsMapper campusReviewsMapper;

    /**
     * 查询商品评价
     * 
     * @param id 商品评价主键
     * @return 商品评价
     */
    @Override
    public CampusReviews selectCampusReviewsById(Long id)
    {
        return campusReviewsMapper.selectCampusReviewsById(id);
    }

    /**
     * 查询商品评价列表
     * 
     * @param campusReviews 商品评价
     * @return 商品评价
     */
    @Override
    public List<CampusReviews> selectCampusReviewsList(CampusReviews campusReviews)
    {
        return campusReviewsMapper.selectCampusReviewsList(campusReviews);
    }

    /**
     * 新增商品评价
     * 
     * @param campusReviews 商品评价
     * @return 结果
     */
    @Override
    public int insertCampusReviews(CampusReviews campusReviews)
    {
        return campusReviewsMapper.insertCampusReviews(campusReviews);
    }

    /**
     * 修改商品评价
     * 
     * @param campusReviews 商品评价
     * @return 结果
     */
    @Override
    public int updateCampusReviews(CampusReviews campusReviews)
    {
        return campusReviewsMapper.updateCampusReviews(campusReviews);
    }

    /**
     * 批量删除商品评价
     * 
     * @param ids 需要删除的商品评价主键
     * @return 结果
     */
    @Override
    public int deleteCampusReviewsByIds(Long[] ids)
    {
        return campusReviewsMapper.deleteCampusReviewsByIds(ids);
    }

    /**
     * 删除商品评价信息
     * 
     * @param id 商品评价主键
     * @return 结果
     */
    @Override
    public int deleteCampusReviewsById(Long id)
    {
        return campusReviewsMapper.deleteCampusReviewsById(id);
    }
}
