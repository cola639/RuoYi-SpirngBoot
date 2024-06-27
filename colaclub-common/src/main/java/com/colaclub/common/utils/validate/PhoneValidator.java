package com.colaclub.common.utils.validate;

import com.colaclub.common.annotation.ValidatePhone;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidatePhone, String> {

  private static final String PHONE_PATTERN = "^[+]?[0-9]{10,13}$"; // 简单的手机号正则表达式

  @Override
  public void initialize(ValidatePhone constraintAnnotation) {}

  @Override
  public boolean isValid(String phone, ConstraintValidatorContext context) {
    if (phone == null) {
      return false; // 可以根据需要调整
    }
    return Pattern.matches(PHONE_PATTERN, phone);
  }
}
