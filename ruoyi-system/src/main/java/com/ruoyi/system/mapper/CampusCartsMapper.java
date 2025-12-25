package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CampusCarts;

/**
 * 购物车Mapper接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface CampusCartsMapper 
{
    /**
     * 查询购物车
     * 
     * @param id 购物车主键
     * @return 购物车
     */
    public CampusCarts selectCampusCartsById(Long id);

    /**
     * 查询购物车列表
     * 
     * @param campusCarts 购物车
     * @return 购物车集合
     */
    public List<CampusCarts> selectCampusCartsList(CampusCarts campusCarts);

    /**
     * 新增购物车
     * 
     * @param campusCarts 购物车
     * @return 结果
     */
    public int insertCampusCarts(CampusCarts campusCarts);

    /**
     * 修改购物车
     * 
     * @param campusCarts 购物车
     * @return 结果
     */
    public int updateCampusCarts(CampusCarts campusCarts);

    /**
     * 删除购物车
     * 
     * @param id 购物车主键
     * @return 结果
     */
    public int deleteCampusCartsById(Long id);

    /**
     * 批量删除购物车
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCampusCartsByIds(Long[] ids);
}
