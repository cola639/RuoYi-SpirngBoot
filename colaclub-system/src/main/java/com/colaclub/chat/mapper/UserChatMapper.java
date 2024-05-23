package com.colaclub.chat.mapper;

import com.colaclub.chat.domain.UserChat;

import java.util.List;

/**
 * 聊天表 数据层
 *
 * @author your-name
 */

public interface UserChatMapper {
    public List<UserChat> selectChatList();

    public List<UserChat> selectUserChat(UserChat userChat);

    public int insertChat(UserChat userChat);

    public int deleteChatById(Long userId);

}
