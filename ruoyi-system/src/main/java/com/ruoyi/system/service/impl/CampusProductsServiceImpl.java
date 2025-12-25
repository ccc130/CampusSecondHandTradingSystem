package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusProductsMapper;
import com.ruoyi.system.domain.CampusProducts;
import com.ruoyi.system.service.ICampusProductsService;

/**
 * 浏览商品Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusProductsServiceImpl implements ICampusProductsService 
{
    @Autowired
    private CampusProductsMapper campusProductsMapper;

    /**
     * 查询浏览商品
     * 
     * @param id 浏览商品主键
     * @return 浏览商品
     */
    @Override
    public CampusProducts selectCampusProductsById(Long id)
    {
        return campusProductsMapper.selectCampusProductsById(id);
    }

    /**
     * 查询浏览商品列表
     * 
     * @param campusProducts 浏览商品
     * @return 浏览商品
     */
    @Override
    public List<CampusProducts> selectCampusProductsList(CampusProducts campusProducts)
    {
        return campusProductsMapper.selectCampusProductsList(campusProducts);
    }

    /**
     * 新增浏览商品
     * 
     * @param campusProducts 浏览商品
     * @return 结果
     */
    @Override
    public int insertCampusProducts(CampusProducts campusProducts)
    {
        return campusProductsMapper.insertCampusProducts(campusProducts);
    }

    /**
     * 修改浏览商品
     * 
     * @param campusProducts 浏览商品
     * @return 结果
     */
    @Override
    public int updateCampusProducts(CampusProducts campusProducts)
    {
        return campusProductsMapper.updateCampusProducts(campusProducts);
    }

    /**
     * 批量删除浏览商品
     * 
     * @param ids 需要删除的浏览商品主键
     * @return 结果
     */
    @Override
    public int deleteCampusProductsByIds(Long[] ids)
    {
        return campusProductsMapper.deleteCampusProductsByIds(ids);
    }

    /**
     * 删除浏览商品信息
     * 
     * @param id 浏览商品主键
     * @return 结果
     */
    @Override
    public int deleteCampusProductsById(Long id)
    {
        return campusProductsMapper.deleteCampusProductsById(id);
    }
}
