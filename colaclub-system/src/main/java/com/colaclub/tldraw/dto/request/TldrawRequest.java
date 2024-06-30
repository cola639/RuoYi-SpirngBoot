package com.colaclub.tldraw.dto.request;

import com.colaclub.common.annotation.Excel;
import com.colaclub.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * tldraw对象 tldraw
 *
 * @author colaclub
 * @date 2024-06-27
 */
public class TldrawRequest extends BaseEntity {
  private static final long serialVersionUID = 1L;

  /** ID */
  private Long id;

  /** 创建用户ID */
  @Excel(name = "创建用户ID")
  private Long userId;

  /** 标题 */
  @NotBlank(message = "标题不能为空")
  @Size(min = 0, max = 30)
  @Excel(name = "标题")
  private String title;

  /** 房间ID */
  @Excel(name = "房间ID")
  private String roomId;

  /** 用户群组 */
  @Excel(name = "用户群组")
  private String members;

  /** 类型 (0文件夹 1白板) */
  @Excel(name = "类型 (0文件夹 1白板)")
  private String type;

  /** 封面 */
  @Excel(name = "封面")
  private String cover;

  /** 状态（0私有 1公开） */
  @NotBlank(message = "状态不能为空")
  @Excel(name = "状态", readConverterExp = "0=私有,1=公开")
  private String status;

  /** 备注 */
  @Excel(name = "备注")
  private String remarks;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public String getMembers() {
    return members;
  }

  public void setMembers(String members) {
    this.members = members;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("id", getId())
        .append("userId", getUserId())
        .append("title", getTitle())
        .append("roomId", getRoomId())
        .append("members", getMembers())
        .append("type", getType())
        .append("cover", getCover())
        .append("status", getStatus())
        .append("remarks", getRemarks())
        .append("createBy", getCreateBy())
        .append("updateBy", getUpdateBy())
        .append("createTime", getCreateTime())
        .append("updateTime", getUpdateTime())
        .toString();
  }
}
