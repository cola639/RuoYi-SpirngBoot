package com.colaclub.web.controller.tldraw;

import com.colaclub.common.annotation.Log;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.page.TableDataInfo;
import com.colaclub.common.enums.BusinessType;
import com.colaclub.common.utils.poi.ExcelUtil;
import com.colaclub.tldraw.domain.Tldraw;
import com.colaclub.tldraw.service.ITldrawService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * tldrawController
 *
 * @author colaclub
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/tldraw")
public class TldrawController extends BaseController {
  @Autowired private ITldrawService tldrawService;

  /** 查询tldraw列表 */
  @PreAuthorize("@ss.hasPermi('tldraw:list')")
  @GetMapping("/list")
  public TableDataInfo list(Tldraw tldraw) {
    startPage();
    List<Tldraw> list = tldrawService.selectTldrawList(tldraw);
    return getDataTable(list);
  }

  /** 导出tldraw列表 */
  @PreAuthorize("@ss.hasPermi('tldraw:export')")
  @Log(title = "tldraw", businessType = BusinessType.EXPORT)
  @PostMapping("/export")
  public void export(HttpServletResponse response, Tldraw tldraw) {
    List<Tldraw> list = tldrawService.selectTldrawList(tldraw);
    ExcelUtil<Tldraw> util = new ExcelUtil<Tldraw>(Tldraw.class);
    util.exportExcel(response, list, "tldraw数据");
  }

  /** 获取tldraw详细信息 */
  @PreAuthorize("@ss.hasPermi('tldraw:query')")
  @GetMapping(value = "/{id}")
  public AjaxResult getInfo(@PathVariable("id") Long id) {
    return AjaxResult.success(tldrawService.selectTldrawById(id));
  }

  /** 新增tldraw */
  @PreAuthorize("@ss.hasPermi('tldraw:add')")
  @Log(title = "tldraw", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@RequestBody Tldraw tldraw) {
    return toAjax(tldrawService.insertTldraw(tldraw));
  }

  /** 修改tldraw */
  @PreAuthorize("@ss.hasPermi('tldraw:edit')")
  @Log(title = "tldraw", businessType = BusinessType.UPDATE)
  @PutMapping
  public AjaxResult edit(@RequestBody Tldraw tldraw) {
    return toAjax(tldrawService.updateTldraw(tldraw));
  }

  /** 删除tldraw */
  @PreAuthorize("@ss.hasPermi('tldraw:remove')")
  @Log(title = "tldraw", businessType = BusinessType.DELETE)
  @DeleteMapping("/{ids}")
  public AjaxResult remove(@PathVariable Long[] ids) {
    return toAjax(tldrawService.deleteTldrawByIds(ids));
  }
}
