package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CampusFavorites;

/**
 * 我的收藏Service接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface ICampusFavoritesService 
{
    /**
     * 查询我的收藏
     * 
     * @param id 我的收藏主键
     * @return 我的收藏
     */
    public CampusFavorites selectCampusFavoritesById(Long id);

    /**
     * 查询我的收藏列表
     * 
     * @param campusFavorites 我的收藏
     * @return 我的收藏集合
     */
    public List<CampusFavorites> selectCampusFavoritesList(CampusFavorites campusFavorites);

    /**
     * 新增我的收藏
     * 
     * @param campusFavorites 我的收藏
     * @return 结果
     */
    public int insertCampusFavorites(CampusFavorites campusFavorites);

    /**
     * 修改我的收藏
     * 
     * @param campusFavorites 我的收藏
     * @return 结果
     */
    public int updateCampusFavorites(CampusFavorites campusFavorites);

    /**
     * 批量删除我的收藏
     * 
     * @param ids 需要删除的我的收藏主键集合
     * @return 结果
     */
    public int deleteCampusFavoritesByIds(Long[] ids);

    /**
     * 删除我的收藏信息
     * 
     * @param id 我的收藏主键
     * @return 结果
     */
    public int deleteCampusFavoritesById(Long id);
}
