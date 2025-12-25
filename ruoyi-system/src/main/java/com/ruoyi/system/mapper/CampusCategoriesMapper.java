package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CampusCategories;

/**
 * 商品类别Mapper接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface CampusCategoriesMapper 
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
     * 删除商品类别
     * 
     * @param id 商品类别主键
     * @return 结果
     */
    public int deleteCampusCategoriesById(Long id);

    /**
     * 批量删除商品类别
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCampusCategoriesByIds(Long[] ids);
}
