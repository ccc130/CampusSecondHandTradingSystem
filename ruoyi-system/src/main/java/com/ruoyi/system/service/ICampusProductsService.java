package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CampusProducts;

/**
 * 浏览商品Service接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface ICampusProductsService 
{
    /**
     * 查询浏览商品
     * 
     * @param id 浏览商品主键
     * @return 浏览商品
     */
    public CampusProducts selectCampusProductsById(Long id);

    /**
     * 查询浏览商品列表
     * 
     * @param campusProducts 浏览商品
     * @return 浏览商品集合
     */
    public List<CampusProducts> selectCampusProductsList(CampusProducts campusProducts);

    /**
     * 新增浏览商品
     * 
     * @param campusProducts 浏览商品
     * @return 结果
     */
    public int insertCampusProducts(CampusProducts campusProducts);

    /**
     * 修改浏览商品
     * 
     * @param campusProducts 浏览商品
     * @return 结果
     */
    public int updateCampusProducts(CampusProducts campusProducts);

    /**
     * 批量删除浏览商品
     * 
     * @param ids 需要删除的浏览商品主键集合
     * @return 结果
     */
    public int deleteCampusProductsByIds(Long[] ids);

    /**
     * 删除浏览商品信息
     * 
     * @param id 浏览商品主键
     * @return 结果
     */
    public int deleteCampusProductsById(Long id);
}
