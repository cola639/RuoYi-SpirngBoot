package com.colaclub.tldraw.service.impl;

import java.util.List;
import com.colaclub.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.colaclub.tldraw.mapper.TldrawMapper;
import com.colaclub.tldraw.domain.Tldraw;
import com.colaclub.tldraw.service.ITldrawService;

/**
 * tldrawService业务层处理
 *
 * @author colaclub
 * @date 2024-06-27
 */
@Service
public class TldrawServiceImpl implements ITldrawService {
  @Autowired private TldrawMapper tldrawMapper;

  /**
   * 查询tldraw
   *
   * @param id tldraw主键
   * @return tldraw
   */
  @Override
  public Tldraw selectTldrawById(Long id) {
    return tldrawMapper.selectTldrawById(id);
  }

  /**
   * 根据房间ID查询tldraw
   *
   * @param roomId 房间ID
   * @return tldraw
   */
  @Override
  public Tldraw selectTldrawByRoomId(String roomId) {
    return tldrawMapper.selectTldrawByRoomId(roomId);
  }

  /**
   * 查询tldraw列表
   *
   * @param tldraw tldraw
   * @return tldraw
   */
  @Override
  public List<Tldraw> selectTldrawList(Tldraw tldraw) {
    return tldrawMapper.selectTldrawList(tldraw);
  }

  /**
   * 新增tldraw
   *
   * @param tldraw tldraw
   * @return 结果
   */
  @Override
  public int insertTldraw(Tldraw tldraw) {
    tldraw.setCreateTime(DateUtils.getNowDate());
    return tldrawMapper.insertTldraw(tldraw);
  }

  /**
   * 修改tldraw
   *
   * @param tldraw tldraw
   * @return 结果
   */
  @Override
  public int updateTldraw(Tldraw tldraw) {
    tldraw.setUpdateTime(DateUtils.getNowDate());
    return tldrawMapper.updateTldraw(tldraw);
  }

  /**
   * 批量删除tldraw
   *
   * @param ids 需要删除的tldraw主键
   * @return 结果
   */
  @Override
  public int deleteTldrawByIds(Long[] ids) {
    return tldrawMapper.deleteTldrawByIds(ids);
  }

  /**
   * 删除tldraw信息
   *
   * @param id tldraw主键
   * @return 结果
   */
  @Override
  public int deleteTldrawById(Long id) {
    return tldrawMapper.deleteTldrawById(id);
  }
}
