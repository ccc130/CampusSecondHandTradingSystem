package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.CampusComplaints;
import com.ruoyi.system.service.ICampusComplaintsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 投诉商品Controller
 * 
 * @author ccc
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/complaints")
public class CampusComplaintsController extends BaseController
{
    @Autowired
    private ICampusComplaintsService campusComplaintsService;

    /**
     * 查询投诉商品列表
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusComplaints campusComplaints)
    {
        startPage();
        List<CampusComplaints> list = campusComplaintsService.selectCampusComplaintsList(campusComplaints);
        return getDataTable(list);
    }

    /**
     * 导出投诉商品列表
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:export')")
    @Log(title = "投诉商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusComplaints campusComplaints)
    {
        List<CampusComplaints> list = campusComplaintsService.selectCampusComplaintsList(campusComplaints);
        ExcelUtil<CampusComplaints> util = new ExcelUtil<CampusComplaints>(CampusComplaints.class);
        util.exportExcel(response, list, "投诉商品数据");
    }

    /**
     * 获取投诉商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusComplaintsService.selectCampusComplaintsById(id));
    }

    /**
     * 新增投诉商品
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:add')")
    @Log(title = "投诉商品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusComplaints campusComplaints)
    {
        return toAjax(campusComplaintsService.insertCampusComplaints(campusComplaints));
    }

    /**
     * 修改投诉商品
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:edit')")
    @Log(title = "投诉商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusComplaints campusComplaints)
    {
        return toAjax(campusComplaintsService.updateCampusComplaints(campusComplaints));
    }

    /**
     * 删除投诉商品
     */
    @PreAuthorize("@ss.hasPermi('campus:complaints:remove')")
    @Log(title = "投诉商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusComplaintsService.deleteCampusComplaintsByIds(ids));
    }
}
