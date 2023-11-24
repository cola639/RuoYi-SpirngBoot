package com.ruoyi.web.controller.chat;


import com.ruoyi.chat.model.UserChat;
import com.ruoyi.chat.service.IUserChatService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 聊天信息
 *
 * @author 大帅和
 */

@RestController

@RequestMapping("/chat")
public class UserChatController extends BaseController {
    @Autowired
    private IUserChatService chatService;

    /**
     * 获取用户聊天列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<UserChat> list = chatService.selectChatList();
        return getDataTable(list);
    }

    @PostMapping("/item")
    public AjaxResult getChat(@RequestBody UserChat userChat) {
        List<UserChat> list = chatService.selectUserChat(userChat);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "聊天管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userId}")
    public AjaxResult remove(@PathVariable Long userId) {
        System.out.println("userId->" + userId);
        return toAjax(chatService.deleteChatById(userId));
    }

    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "新增聊天", businessType = BusinessType.INSERT)
    @PostMapping("/newChat")
    public AjaxResult add(@Validated @RequestBody UserChat userChat) {
        try {
            return toAjax(chatService.insertChat(userChat));
        } catch (Exception e) {
            return AjaxResult.error("参数有误");
        }

    }


}
