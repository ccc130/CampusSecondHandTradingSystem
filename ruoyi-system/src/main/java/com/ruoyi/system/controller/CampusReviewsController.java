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
import com.ruoyi.system.domain.CampusReviews;
import com.ruoyi.system.service.ICampusReviewsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 商品评价Controller
 * 
 * @author ccc
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/campus/reviews")
public class CampusReviewsController extends BaseController
{
    @Autowired
    private ICampusReviewsService campusReviewsService;

    /**
     * 查询商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:list')")
    @GetMapping("/list")
    public TableDataInfo list(CampusReviews campusReviews)
    {
        startPage();
        List<CampusReviews> list = campusReviewsService.selectCampusReviewsList(campusReviews);
        return getDataTable(list);
    }

    /**
     * 导出商品评价列表
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:export')")
    @Log(title = "商品评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CampusReviews campusReviews)
    {
        List<CampusReviews> list = campusReviewsService.selectCampusReviewsList(campusReviews);
        ExcelUtil<CampusReviews> util = new ExcelUtil<CampusReviews>(CampusReviews.class);
        util.exportExcel(response, list, "商品评价数据");
    }

    /**
     * 获取商品评价详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(campusReviewsService.selectCampusReviewsById(id));
    }

    /**
     * 新增商品评价
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:add')")
    @Log(title = "商品评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CampusReviews campusReviews)
    {
        return toAjax(campusReviewsService.insertCampusReviews(campusReviews));
    }

    /**
     * 修改商品评价
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:edit')")
    @Log(title = "商品评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CampusReviews campusReviews)
    {
        return toAjax(campusReviewsService.updateCampusReviews(campusReviews));
    }

    /**
     * 删除商品评价
     */
    @PreAuthorize("@ss.hasPermi('campus:reviews:remove')")
    @Log(title = "商品评价", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(campusReviewsService.deleteCampusReviewsByIds(ids));
    }
}
