package com.colaclub.thirdlogin;

import lombok.Data;

@Data
public class LoginByOtherSourceBody {

  private String code;
  private String source;
  private String uuid;
}
