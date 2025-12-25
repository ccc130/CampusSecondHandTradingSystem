package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusOrdersMapper;
import com.ruoyi.system.domain.CampusOrders;
import com.ruoyi.system.service.ICampusOrdersService;

/**
 * 我的订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@Service
public class CampusOrdersServiceImpl implements ICampusOrdersService 
{
    @Autowired
    private CampusOrdersMapper campusOrdersMapper;

    /**
     * 查询我的订单
     * 
     * @param id 我的订单主键
     * @return 我的订单
     */
    @Override
    public CampusOrders selectCampusOrdersById(Long id)
    {
        return campusOrdersMapper.selectCampusOrdersById(id);
    }

    /**
     * 查询我的订单列表
     * 
     * @param campusOrders 我的订单
     * @return 我的订单
     */
    @Override
    public List<CampusOrders> selectCampusOrdersList(CampusOrders campusOrders)
    {
        return campusOrdersMapper.selectCampusOrdersList(campusOrders);
    }

    /**
     * 新增我的订单
     * 
     * @param campusOrders 我的订单
     * @return 结果
     */
    @Override
    public int insertCampusOrders(CampusOrders campusOrders)
    {
        return campusOrdersMapper.insertCampusOrders(campusOrders);
    }

    /**
     * 修改我的订单
     * 
     * @param campusOrders 我的订单
     * @return 结果
     */
    @Override
    public int updateCampusOrders(CampusOrders campusOrders)
    {
        return campusOrdersMapper.updateCampusOrders(campusOrders);
    }

    /**
     * 批量删除我的订单
     * 
     * @param ids 需要删除的我的订单主键
     * @return 结果
     */
    @Override
    public int deleteCampusOrdersByIds(Long[] ids)
    {
        return campusOrdersMapper.deleteCampusOrdersByIds(ids);
    }

    /**
     * 删除我的订单信息
     * 
     * @param id 我的订单主键
     * @return 结果
     */
    @Override
    public int deleteCampusOrdersById(Long id)
    {
        return campusOrdersMapper.deleteCampusOrdersById(id);
    }
}
