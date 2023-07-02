package com.ruoyi.chat.mapper;

import java.util.List;
import com.ruoyi.chat.model.UserChat;

/**
 * 聊天表 数据层
 *
 * @author your-name
 */

public interface UserChatMapper
{
    List<UserChat> selectChatList();
    List<UserChat> selectUserChat(UserChat userChat);
}
