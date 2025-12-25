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
import com.ruoyi.system.domain.CampusCategories;
import com.ruoyi.system.service.ICampusCategoriesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品类别Controller
 * 
 * @author ccc
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/categories")
public class CampusCategoriesController extends BaseController
{
    @Autowired
    private ICampusCategoriesService campusCategoriesService;

    /**
     * 查询商品类别列表
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusCategories campusCategories)
    {
        startPage();
        List<CampusCategories> list = campusCategoriesService.selectCampusCategoriesList(campusCategories);
        return getDataTable(list);
    }

    /**
     * 导出商品类别列表
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:export')")
    @Log(title = "商品类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusCategories campusCategories)
    {
        List<CampusCategories> list = campusCategoriesService.selectCampusCategoriesList(campusCategories);
        ExcelUtil<CampusCategories> util = new ExcelUtil<CampusCategories>(CampusCategories.class);
        util.exportExcel(response, list, "商品类别数据");
    }

    /**
     * 获取商品类别详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusCategoriesService.selectCampusCategoriesById(id));
    }

    /**
     * 新增商品类别
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:add')")
    @Log(title = "商品类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusCategories campusCategories)
    {
        return toAjax(campusCategoriesService.insertCampusCategories(campusCategories));
    }

    /**
     * 修改商品类别
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:edit')")
    @Log(title = "商品类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusCategories campusCategories)
    {
        return toAjax(campusCategoriesService.updateCampusCategories(campusCategories));
    }

    /**
     * 删除商品类别
     */
    @PreAuthorize("@ss.hasPermi('campus:categories:remove')")
    @Log(title = "商品类别", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusCategoriesService.deleteCampusCategoriesByIds(ids));
    }
}
