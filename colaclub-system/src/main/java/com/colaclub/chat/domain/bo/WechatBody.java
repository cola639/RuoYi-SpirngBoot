package com.colaclub.chat.domain.bo;

import lombok.Data;

@Data
public class WechatBody {
    private String code;
    private String nickName;
    private String avatarUrl;

}

