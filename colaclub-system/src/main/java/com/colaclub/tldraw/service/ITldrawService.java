package com.colaclub.tldraw.service;

import java.util.List;
import com.colaclub.tldraw.domain.Tldraw;

/**
 * tldrawService接口
 * 
 * @author colaclub
 * @date 2024-06-27
 */
public interface ITldrawService 
{
    /**
     * 查询tldraw
     * 
     * @param id tldraw主键
     * @return tldraw
     */
    public Tldraw selectTldrawById(Long id);

    /**
     * 查询tldraw列表
     * 
     * @param tldraw tldraw
     * @return tldraw集合
     */
    public List<Tldraw> selectTldrawList(Tldraw tldraw);

    /**
     * 新增tldraw
     * 
     * @param tldraw tldraw
     * @return 结果
     */
    public int insertTldraw(Tldraw tldraw);

    /**
     * 修改tldraw
     * 
     * @param tldraw tldraw
     * @return 结果
     */
    public int updateTldraw(Tldraw tldraw);

    /**
     * 批量删除tldraw
     * 
     * @param ids 需要删除的tldraw主键集合
     * @return 结果
     */
    public int deleteTldrawByIds(Long[] ids);

    /**
     * 删除tldraw信息
     * 
     * @param id tldraw主键
     * @return 结果
     */
    public int deleteTldrawById(Long id);
}
