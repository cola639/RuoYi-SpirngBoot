package com.colaclub.web.controller.tldraw;

import com.colaclub.common.annotation.Log;
import com.colaclub.common.core.controller.BaseController;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.core.page.TableDataInfo;
import com.colaclub.common.core.redis.RedisCache;
import com.colaclub.common.enums.BusinessType;
import com.colaclub.common.utils.SecurityUtils;
import com.colaclub.common.utils.poi.ExcelUtil;
import com.colaclub.common.utils.uuid.CombinationGenerator;
import com.colaclub.common.utils.uuid.IdUtils;
import com.colaclub.tldraw.domain.Tldraw;
import com.colaclub.tldraw.service.ITldrawService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * tldrawController
 *
 * @author colaclub
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/tldraw")
public class TldrawController extends BaseController {
  private static final String ROOM_UUID_KEY = "tldraw:room:";
  private static final String UUID_ROOM_KEY = "tldraw:uuid:";
  @Autowired private ITldrawService tldrawService;
  @Autowired private RedisCache redisCache;

  /** 生成房间 */
  @PreAuthorize("@ss.hasPermi('tldraw:generate')")
  @Log(title = "tldraw", businessType = BusinessType.INSERT)
  @PostMapping("/generateRoom")
  public AjaxResult generateRoom(@Validated @RequestBody Tldraw requestTldraw) {
    // 生成uuid房间号
    String uuid = IdUtils.randomUUID();
    Long userId = SecurityUtils.getUserId();

    // 创建新的 Tldraw 实例，并设置属性
    Tldraw tldraw = new Tldraw();
    tldraw.setUserId(userId);
    tldraw.setTitle(requestTldraw.getTitle());
    tldraw.setRoomId(uuid);
    tldraw.setMembers(String.valueOf(userId));
    tldraw.setStatus(requestTldraw.getStatus());

    // redis 创建15分钟临时房间号
    Integer expireTime = 15;
    String randomNum;
    String roomKey;
    String uuidKey;

    do {
      randomNum = CombinationGenerator.generateRandomCombination(6);
      roomKey = ROOM_UUID_KEY + randomNum;
      uuidKey = UUID_ROOM_KEY + uuid;
    } while (redisCache.hasKey(roomKey));

    redisCache.setCacheObject(roomKey, uuid, expireTime, TimeUnit.MINUTES);
    redisCache.setCacheObject(uuidKey, randomNum, expireTime, TimeUnit.MINUTES);

    // 插入 tldraw 数据
    tldrawService.insertTldraw(tldraw);

    // 根据 roomId 查询插入的数据并返回
    Tldraw selectedTldraw = tldrawService.selectTldrawByRoomId(uuid);
    AjaxResult ajax = AjaxResult.success();
    ajax.put("data", selectedTldraw);
    ajax.put("tempRoomNum", randomNum);
    return ajax;
  }

  /** roomNum获取UUID */
  @PreAuthorize("@ss.hasPermi('tldraw:join')")
  @Log(title = "tldraw", businessType = BusinessType.UPDATE)
  @GetMapping("/getUUID")
  public AjaxResult join(@RequestParam String roomNum) {
    String uuid = redisCache.getCacheObject(ROOM_UUID_KEY + roomNum);

    if (uuid == null) {
      return AjaxResult.error("房间号不存在");
    } else {
      AjaxResult ajax = AjaxResult.success();
      ajax.put("data", uuid);
      return ajax;
    }
  }

  /** uuid加入房间验证 */
  @Log(title = "tldraw", businessType = BusinessType.UPDATE)
  @GetMapping("/joinByUUID/{uuid}")
  public AjaxResult joinRoom(@PathVariable String uuid) {
    String roomId = uuid;
    Tldraw tldraw = tldrawService.selectTldrawByRoomId(roomId);

    if (tldraw == null) {
      return AjaxResult.error("房间不存在");
    }

    Long userId = SecurityUtils.getUserId();
    String members = tldraw.getMembers();

    // members 转化为数组 判断userId 是否成员之一
    List<String> memberList = Arrays.asList(members.split(","));

    if (memberList.contains(String.valueOf(userId))) {
      return AjaxResult.success(tldraw);
    } else {
      return AjaxResult.error("您不在此房间的成员列表中");
    }
  }

  /** 分享房间号 */
  @PreAuthorize("@ss.hasPermi('tldraw:share')")
  @Log(title = "tldraw", businessType = BusinessType.UPDATE)
  @GetMapping("/share")
  public AjaxResult shareRoom(@PathVariable String uuid) {
    //  查询redisKey tldraw:roomId:xx 存在 对应uuid值  存在即还在时效性 返回房间号
    if (redisCache.hasKey(UUID_ROOM_KEY + uuid)) {
      return AjaxResult.success(redisCache.getCacheObject(UUID_ROOM_KEY + uuid));
    } else {
      // redis 创建15分钟临时房间号
      Integer expireTime = 15;
      String randomNum;
      String roomKey;
      String uuidKey;

      //  不存在即过期 则生成后返回
      //  生成不重复房间号 并存入redis
      do {
        randomNum = CombinationGenerator.generateRandomCombination(6);
        roomKey = ROOM_UUID_KEY + randomNum;
        uuidKey = UUID_ROOM_KEY + uuid;
      } while (redisCache.hasKey(roomKey));

      redisCache.setCacheObject(roomKey, uuid, expireTime, TimeUnit.MINUTES);
      redisCache.setCacheObject(uuidKey, randomNum, expireTime, TimeUnit.MINUTES);

      AjaxResult ajax = AjaxResult.success();
      ajax.put("data", randomNum);
      return ajax;
    }
  }

  /** 更新封面 */
  @PreAuthorize("@ss.hasPermi('tldraw:cover')")
  @Log(title = "tldraw", businessType = BusinessType.UPDATE)
  @PutMapping("/cover")
  public AjaxResult updateCover(@RequestBody Tldraw reqTldraw) {
    Tldraw tldraw = tldrawService.selectTldrawById(reqTldraw.getId());
    tldraw.setCover(reqTldraw.getCover());

    return toAjax(tldrawService.updateTldraw(tldraw));
  }

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
  public AjaxResult add(@Validated @RequestBody Tldraw tldraw) {
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
