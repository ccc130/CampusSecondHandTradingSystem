package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CampusOrders;

/**
 * 我的订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
public interface CampusOrdersMapper 
{
    /**
     * 查询我的订单
     * 
     * @param id 我的订单主键
     * @return 我的订单
     */
    public CampusOrders selectCampusOrdersById(Long id);

    /**
     * 查询我的订单列表
     * 
     * @param campusOrders 我的订单
     * @return 我的订单集合
     */
    public List<CampusOrders> selectCampusOrdersList(CampusOrders campusOrders);

    /**
     * 新增我的订单
     * 
     * @param campusOrders 我的订单
     * @return 结果
     */
    public int insertCampusOrders(CampusOrders campusOrders);

    /**
     * 修改我的订单
     * 
     * @param campusOrders 我的订单
     * @return 结果
     */
    public int updateCampusOrders(CampusOrders campusOrders);

    /**
     * 删除我的订单
     * 
     * @param id 我的订单主键
     * @return 结果
     */
    public int deleteCampusOrdersById(Long id);

    /**
     * 批量删除我的订单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCampusOrdersByIds(Long[] ids);
}
