package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusFavoritesMapper;
import com.ruoyi.system.domain.CampusFavorites;
import com.ruoyi.system.service.ICampusFavoritesService;

/**
 * 我的收藏Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusFavoritesServiceImpl implements ICampusFavoritesService 
{
    @Autowired
    private CampusFavoritesMapper campusFavoritesMapper;

    /**
     * 查询我的收藏
     * 
     * @param id 我的收藏主键
     * @return 我的收藏
     */
    @Override
    public CampusFavorites selectCampusFavoritesById(Long id)
    {
        return campusFavoritesMapper.selectCampusFavoritesById(id);
    }

    /**
     * 查询我的收藏列表
     * 
     * @param campusFavorites 我的收藏
     * @return 我的收藏
     */
    @Override
    public List<CampusFavorites> selectCampusFavoritesList(CampusFavorites campusFavorites)
    {
        return campusFavoritesMapper.selectCampusFavoritesList(campusFavorites);
    }

    /**
     * 新增我的收藏
     * 
     * @param campusFavorites 我的收藏
     * @return 结果
     */
    @Override
    public int insertCampusFavorites(CampusFavorites campusFavorites)
    {
        return campusFavoritesMapper.insertCampusFavorites(campusFavorites);
    }

    /**
     * 修改我的收藏
     * 
     * @param campusFavorites 我的收藏
     * @return 结果
     */
    @Override
    public int updateCampusFavorites(CampusFavorites campusFavorites)
    {
        return campusFavoritesMapper.updateCampusFavorites(campusFavorites);
    }

    /**
     * 批量删除我的收藏
     * 
     * @param ids 需要删除的我的收藏主键
     * @return 结果
     */
    @Override
    public int deleteCampusFavoritesByIds(Long[] ids)
    {
        return campusFavoritesMapper.deleteCampusFavoritesByIds(ids);
    }

    /**
     * 删除我的收藏信息
     * 
     * @param id 我的收藏主键
     * @return 结果
     */
    @Override
    public int deleteCampusFavoritesById(Long id)
    {
        return campusFavoritesMapper.deleteCampusFavoritesById(id);
    }
}
