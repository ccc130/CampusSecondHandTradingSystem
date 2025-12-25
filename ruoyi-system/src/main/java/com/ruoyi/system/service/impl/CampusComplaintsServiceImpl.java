package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CampusComplaintsMapper;
import com.ruoyi.system.domain.CampusComplaints;
import com.ruoyi.system.service.ICampusComplaintsService;

/**
 * 投诉商品Service业务层处理
 * 
 * @author ccc
 * @date 2025-12-24
 */
@Service
public class CampusComplaintsServiceImpl implements ICampusComplaintsService 
{
    @Autowired
    private CampusComplaintsMapper campusComplaintsMapper;

    /**
     * 查询投诉商品
     * 
     * @param id 投诉商品主键
     * @return 投诉商品
     */
    @Override
    public CampusComplaints selectCampusComplaintsById(Long id)
    {
        return campusComplaintsMapper.selectCampusComplaintsById(id);
    }

    /**
     * 查询投诉商品列表
     * 
     * @param campusComplaints 投诉商品
     * @return 投诉商品
     */
    @Override
    public List<CampusComplaints> selectCampusComplaintsList(CampusComplaints campusComplaints)
    {
        return campusComplaintsMapper.selectCampusComplaintsList(campusComplaints);
    }

    /**
     * 新增投诉商品
     * 
     * @param campusComplaints 投诉商品
     * @return 结果
     */
    @Override
    public int insertCampusComplaints(CampusComplaints campusComplaints)
    {
        return campusComplaintsMapper.insertCampusComplaints(campusComplaints);
    }

    /**
     * 修改投诉商品
     * 
     * @param campusComplaints 投诉商品
     * @return 结果
     */
    @Override
    public int updateCampusComplaints(CampusComplaints campusComplaints)
    {
        return campusComplaintsMapper.updateCampusComplaints(campusComplaints);
    }

    /**
     * 批量删除投诉商品
     * 
     * @param ids 需要删除的投诉商品主键
     * @return 结果
     */
    @Override
    public int deleteCampusComplaintsByIds(Long[] ids)
    {
        return campusComplaintsMapper.deleteCampusComplaintsByIds(ids);
    }

    /**
     * 删除投诉商品信息
     * 
     * @param id 投诉商品主键
     * @return 结果
     */
    @Override
    public int deleteCampusComplaintsById(Long id)
    {
        return campusComplaintsMapper.deleteCampusComplaintsById(id);
    }
}
