package com.ruoyi.chat.service;

import com.ruoyi.chat.model.UserChat;

import java.util.List;

public interface IUserChatService {
    /**
     * 获取用户聊天列表
     *
     * @return 聊天列表
     */
    public List<UserChat> selectChatList();

    public List<UserChat> selectUserChat(UserChat userChat);

    public int insertChat(UserChat userChat);

    public int deleteChatById(Long userId);
}
