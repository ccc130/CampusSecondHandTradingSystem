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
import com.ruoyi.system.domain.CampusFavorites;
import com.ruoyi.system.service.ICampusFavoritesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 我的收藏Controller
 * 
 * @author ccc
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/favorites")
public class CampusFavoritesController extends BaseController
{
    @Autowired
    private ICampusFavoritesService campusFavoritesService;

    /**
     * 查询我的收藏列表
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusFavorites campusFavorites)
    {
        startPage();
        List<CampusFavorites> list = campusFavoritesService.selectCampusFavoritesList(campusFavorites);
        return getDataTable(list);
    }

    /**
     * 导出我的收藏列表
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:export')")
    @Log(title = "我的收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusFavorites campusFavorites)
    {
        List<CampusFavorites> list = campusFavoritesService.selectCampusFavoritesList(campusFavorites);
        ExcelUtil<CampusFavorites> util = new ExcelUtil<CampusFavorites>(CampusFavorites.class);
        util.exportExcel(response, list, "我的收藏数据");
    }

    /**
     * 获取我的收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusFavoritesService.selectCampusFavoritesById(id));
    }

    /**
     * 新增我的收藏
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:add')")
    @Log(title = "我的收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusFavorites campusFavorites)
    {
        return toAjax(campusFavoritesService.insertCampusFavorites(campusFavorites));
    }

    /**
     * 修改我的收藏
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:edit')")
    @Log(title = "我的收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusFavorites campusFavorites)
    {
        return toAjax(campusFavoritesService.updateCampusFavorites(campusFavorites));
    }

    /**
     * 删除我的收藏
     */
    @PreAuthorize("@ss.hasPermi('campus:favorites:remove')")
    @Log(title = "我的收藏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusFavoritesService.deleteCampusFavoritesByIds(ids));
    }
}
