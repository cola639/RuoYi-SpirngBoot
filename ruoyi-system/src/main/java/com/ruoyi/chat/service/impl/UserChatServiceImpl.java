package com.ruoyi.chat.service.impl;

import com.ruoyi.chat.mapper.UserChatMapper;
import com.ruoyi.chat.model.UserChat;
import com.ruoyi.chat.service.IUserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserChatServiceImpl implements IUserChatService {
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

    @Override
    @Transactional
    public int insertChat(UserChat userChat) {
        return userChatMapper.insertChat(userChat);
    }

    @Override
    @Transactional
    public int deleteChatById(Long userId) {
        return userChatMapper.deleteChatById(userId);
    }
}
