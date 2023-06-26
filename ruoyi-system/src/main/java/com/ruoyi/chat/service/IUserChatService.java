package com.ruoyi.chat.service;

import java.util.List;
import com.ruoyi.chat.model.UserChat;

public interface IUserChatService
{
    /**
     * 获取用户聊天列表
     *
     * @return 聊天列表
     */
    List<UserChat> selectChatList();
}
