package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.CampusComplaints;

/**
 * 投诉商品Mapper接口
 * 
 * @author ccc
 * @date 2025-12-24
 */
public interface CampusComplaintsMapper 
{
    /**
     * 查询投诉商品
     * 
     * @param id 投诉商品主键
     * @return 投诉商品
     */
    public CampusComplaints selectCampusComplaintsById(Long id);

    /**
     * 查询投诉商品列表
     * 
     * @param campusComplaints 投诉商品
     * @return 投诉商品集合
     */
    public List<CampusComplaints> selectCampusComplaintsList(CampusComplaints campusComplaints);

    /**
     * 新增投诉商品
     * 
     * @param campusComplaints 投诉商品
     * @return 结果
     */
    public int insertCampusComplaints(CampusComplaints campusComplaints);

    /**
     * 修改投诉商品
     * 
     * @param campusComplaints 投诉商品
     * @return 结果
     */
    public int updateCampusComplaints(CampusComplaints campusComplaints);

    /**
     * 删除投诉商品
     * 
     * @param id 投诉商品主键
     * @return 结果
     */
    public int deleteCampusComplaintsById(Long id);

    /**
     * 批量删除投诉商品
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCampusComplaintsByIds(Long[] ids);
}
