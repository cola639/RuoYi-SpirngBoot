package com.ruoyi.web.controller.chat;


import com.ruoyi.chat.model.UserChat;
import com.ruoyi.chat.service.IUserChatService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 聊天信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/chat")
public class UserChatController extends BaseController
{
    @Autowired
    private IUserChatService chatService;

    /**
     * 获取用户聊天列表
     */
    @GetMapping("/list")
    public TableDataInfo list()
    {
        startPage();
        List<UserChat> list = chatService.selectChatList();
        return getDataTable(list);
    }
}
