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
import com.ruoyi.system.domain.CampusCarts;
import com.ruoyi.system.service.ICampusCartsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 购物车Controller
 * 
 * @author ccc
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/carts")
public class CampusCartsController extends BaseController
{
    @Autowired
    private ICampusCartsService campusCartsService;

    /**
     * 查询购物车列表
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusCarts campusCarts)
    {
        startPage();
        List<CampusCarts> list = campusCartsService.selectCampusCartsList(campusCarts);
        return getDataTable(list);
    }

    /**
     * 导出购物车列表
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:export')")
    @Log(title = "购物车", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusCarts campusCarts)
    {
        List<CampusCarts> list = campusCartsService.selectCampusCartsList(campusCarts);
        ExcelUtil<CampusCarts> util = new ExcelUtil<CampusCarts>(CampusCarts.class);
        util.exportExcel(response, list, "购物车数据");
    }

    /**
     * 获取购物车详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusCartsService.selectCampusCartsById(id));
    }

    /**
     * 新增购物车
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:add')")
    @Log(title = "购物车", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusCarts campusCarts)
    {
        return toAjax(campusCartsService.insertCampusCarts(campusCarts));
    }

    /**
     * 修改购物车
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:edit')")
    @Log(title = "购物车", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusCarts campusCarts)
    {
        return toAjax(campusCartsService.updateCampusCarts(campusCarts));
    }

    /**
     * 删除购物车
     */
    @PreAuthorize("@ss.hasPermi('campus:carts:remove')")
    @Log(title = "购物车", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusCartsService.deleteCampusCartsByIds(ids));
    }
}
