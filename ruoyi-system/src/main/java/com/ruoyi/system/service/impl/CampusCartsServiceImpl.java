package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusCartsMapper;
import com.ruoyi.system.domain.CampusCarts;
import com.ruoyi.system.service.ICampusCartsService;

/**
 * 购物车Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusCartsServiceImpl implements ICampusCartsService 
{
    @Autowired
    private CampusCartsMapper campusCartsMapper;

    /**
     * 查询购物车
     * 
     * @param id 购物车主键
     * @return 购物车
     */
    @Override
    public CampusCarts selectCampusCartsById(Long id)
    {
        return campusCartsMapper.selectCampusCartsById(id);
    }

    /**
     * 查询购物车列表
     * 
     * @param campusCarts 购物车
     * @return 购物车
     */
    @Override
    public List<CampusCarts> selectCampusCartsList(CampusCarts campusCarts)
    {
        return campusCartsMapper.selectCampusCartsList(campusCarts);
    }

    /**
     * 新增购物车
     * 
     * @param campusCarts 购物车
     * @return 结果
     */
    @Override
    public int insertCampusCarts(CampusCarts campusCarts)
    {
        return campusCartsMapper.insertCampusCarts(campusCarts);
    }

    /**
     * 修改购物车
     * 
     * @param campusCarts 购物车
     * @return 结果
     */
    @Override
    public int updateCampusCarts(CampusCarts campusCarts)
    {
        return campusCartsMapper.updateCampusCarts(campusCarts);
    }

    /**
     * 批量删除购物车
     * 
     * @param ids 需要删除的购物车主键
     * @return 结果
     */
    @Override
    public int deleteCampusCartsByIds(Long[] ids)
    {
        return campusCartsMapper.deleteCampusCartsByIds(ids);
    }

    /**
     * 删除购物车信息
     * 
     * @param id 购物车主键
     * @return 结果
     */
    @Override
    public int deleteCampusCartsById(Long id)
    {
        return campusCartsMapper.deleteCampusCartsById(id);
    }
}
