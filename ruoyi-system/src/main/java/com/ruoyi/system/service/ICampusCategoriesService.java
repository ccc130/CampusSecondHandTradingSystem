package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CampusCategories;

/**
 * 商品类别Service接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface ICampusCategoriesService 
{
    /**
     * 查询商品类别
     * 
     * @param id 商品类别主键
     * @return 商品类别
     */
    public CampusCategories selectCampusCategoriesById(Long id);

    /**
     * 查询商品类别列表
     * 
     * @param campusCategories 商品类别
     * @return 商品类别集合
     */
    public List<CampusCategories> selectCampusCategoriesList(CampusCategories campusCategories);

    /**
     * 新增商品类别
     * 
     * @param campusCategories 商品类别
     * @return 结果
     */
    public int insertCampusCategories(CampusCategories campusCategories);

    /**
     * 修改商品类别
     * 
     * @param campusCategories 商品类别
     * @return 结果
     */
    public int updateCampusCategories(CampusCategories campusCategories);

    /**
     * 批量删除商品类别
     * 
     * @param ids 需要删除的商品类别主键集合
     * @return 结果
     */
    public int deleteCampusCategoriesByIds(Long[] ids);

    /**
     * 删除商品类别信息
     * 
     * @param id 商品类别主键
     * @return 结果
     */
    public int deleteCampusCategoriesById(Long id);
}
