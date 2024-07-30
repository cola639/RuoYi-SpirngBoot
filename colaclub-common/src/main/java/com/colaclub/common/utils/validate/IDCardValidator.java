package com.colaclub.common.utils.validate;

import com.colaclub.common.annotation.ValidateIDCard;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IDCardValidator implements ConstraintValidator<ValidateIDCard, String> {

  private static final String ID_CARD_PATTERN = "^(\\d{15}|\\d{18}|\\d{17}[Xx])$"; // 简单的身份证号码正则表达式

  @Override
  public void initialize(ValidateIDCard constraintAnnotation) {}

  @Override
  public boolean isValid(String idCard, ConstraintValidatorContext context) {
    if (idCard == null) {
      return false; // 可以根据需要调整
    }
    return Pattern.matches(ID_CARD_PATTERN, idCard);
  }
}
