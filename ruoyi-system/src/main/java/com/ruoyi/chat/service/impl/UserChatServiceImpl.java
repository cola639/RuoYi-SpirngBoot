package com.ruoyi.chat.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.ruoyi.chat.model.UserChat;
import com.ruoyi.chat.mapper.UserChatMapper;
import com.ruoyi.chat.service.IUserChatService;

@Service
public class UserChatServiceImpl implements IUserChatService
{
    @Autowired
    private UserChatMapper userChatMapper;

    @Override
    public List<UserChat> selectChatList() {
        return userChatMapper.selectChatList();
    }

    @Override
    public List<UserChat> selectUserChat(UserChat userChat) {
        return userChatMapper.selectUserChat(userChat);
    }


}
