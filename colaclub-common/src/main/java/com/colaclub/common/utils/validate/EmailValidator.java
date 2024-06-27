package com.colaclub.common.utils.validate;

import com.colaclub.common.annotation.ValidateEmail;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidateEmail, String> {

  private static final String EMAIL_PATTERN =
      "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"; // 简单的邮箱正则表达式

  @Override
  public void initialize(ValidateEmail constraintAnnotation) {}

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null) {
      return false; // 可以根据需要调整
    }
    return Pattern.matches(EMAIL_PATTERN, email);
  }
}
