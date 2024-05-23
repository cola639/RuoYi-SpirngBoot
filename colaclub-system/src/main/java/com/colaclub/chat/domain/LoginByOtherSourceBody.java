package com.colaclub.chat.domain;

import lombok.Data;

@Data
public class LoginByOtherSourceBody {

    private String code;
    private String source;
    private String uuid;

}
