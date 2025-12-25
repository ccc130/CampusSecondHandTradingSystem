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
import com.ruoyi.system.domain.CampusOrders;
import com.ruoyi.system.service.ICampusOrdersService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 我的订单Controller
 * 
 * @author ruoyi
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/orders")
public class CampusOrdersController extends BaseController
{
    @Autowired
    private ICampusOrdersService campusOrdersService;

    /**
     * 查询我的订单列表
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusOrders campusOrders)
    {
        startPage();
        List<CampusOrders> list = campusOrdersService.selectCampusOrdersList(campusOrders);
        return getDataTable(list);
    }

    /**
     * 导出我的订单列表
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:export')")
    @Log(title = "我的订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusOrders campusOrders)
    {
        List<CampusOrders> list = campusOrdersService.selectCampusOrdersList(campusOrders);
        ExcelUtil<CampusOrders> util = new ExcelUtil<CampusOrders>(CampusOrders.class);
        util.exportExcel(response, list, "我的订单数据");
    }

    /**
     * 获取我的订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusOrdersService.selectCampusOrdersById(id));
    }

    /**
     * 新增我的订单
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:add')")
    @Log(title = "我的订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusOrders campusOrders)
    {
        return toAjax(campusOrdersService.insertCampusOrders(campusOrders));
    }

    /**
     * 修改我的订单
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:edit')")
    @Log(title = "我的订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusOrders campusOrders)
    {
        return toAjax(campusOrdersService.updateCampusOrders(campusOrders));
    }

    /**
     * 删除我的订单
     */
    @PreAuthorize("@ss.hasPermi('campus:orders:remove')")
    @Log(title = "我的订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusOrdersService.deleteCampusOrdersByIds(ids));
    }
}
