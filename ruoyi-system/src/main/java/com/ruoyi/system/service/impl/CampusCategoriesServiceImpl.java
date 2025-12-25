package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusCategoriesMapper;
import com.ruoyi.system.domain.CampusCategories;
import com.ruoyi.system.service.ICampusCategoriesService;

/**
 * 商品类别Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusCategoriesServiceImpl implements ICampusCategoriesService 
{
    @Autowired
    private CampusCategoriesMapper campusCategoriesMapper;

    /**
     * 查询商品类别
     * 
     * @param id 商品类别主键
     * @return 商品类别
     */
    @Override
    public CampusCategories selectCampusCategoriesById(Long id)
    {
        return campusCategoriesMapper.selectCampusCategoriesById(id);
    }

    /**
     * 查询商品类别列表
     * 
     * @param campusCategories 商品类别
     * @return 商品类别
     */
    @Override
    public List<CampusCategories> selectCampusCategoriesList(CampusCategories campusCategories)
    {
        return campusCategoriesMapper.selectCampusCategoriesList(campusCategories);
    }

    /**
     * 新增商品类别
     * 
     * @param campusCategories 商品类别
     * @return 结果
     */
    @Override
    public int insertCampusCategories(CampusCategories campusCategories)
    {
        return campusCategoriesMapper.insertCampusCategories(campusCategories);
    }

    /**
     * 修改商品类别
     * 
     * @param campusCategories 商品类别
     * @return 结果
     */
    @Override
    public int updateCampusCategories(CampusCategories campusCategories)
    {
        return campusCategoriesMapper.updateCampusCategories(campusCategories);
    }

    /**
     * 批量删除商品类别
     * 
     * @param ids 需要删除的商品类别主键
     * @return 结果
     */
    @Override
    public int deleteCampusCategoriesByIds(Long[] ids)
    {
        return campusCategoriesMapper.deleteCampusCategoriesByIds(ids);
    }

    /**
     * 删除商品类别信息
     * 
     * @param id 商品类别主键
     * @return 结果
     */
    @Override
    public int deleteCampusCategoriesById(Long id)
    {
        return campusCategoriesMapper.deleteCampusCategoriesById(id);
    }
}
