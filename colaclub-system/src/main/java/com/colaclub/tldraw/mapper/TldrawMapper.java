package com.colaclub.tldraw.mapper;

import java.util.List;
import com.colaclub.tldraw.domain.Tldraw;

/**
 * tldrawMapper接口
 * 
 * @author colaclub
 * @date 2024-06-27
 */
public interface TldrawMapper 
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
     * 删除tldraw
     * 
     * @param id tldraw主键
     * @return 结果
     */
    public int deleteTldrawById(Long id);

    /**
     * 批量删除tldraw
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTldrawByIds(Long[] ids);
}
