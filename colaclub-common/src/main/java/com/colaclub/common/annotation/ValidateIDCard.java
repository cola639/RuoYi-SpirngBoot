package com.colaclub.common.annotation;

import com.colaclub.common.utils.validate.IDCardValidator;
import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = IDCardValidator.class)
@Target({
  ElementType.METHOD,
  ElementType.FIELD,
  ElementType.ANNOTATION_TYPE,
  ElementType.CONSTRUCTOR,
  ElementType.PARAMETER,
  ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateIDCard {

  String message() default "Invalid ID card number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.PARAMETER,
    ElementType.TYPE_USE
  })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface List {
    ValidateIDCard[] value();
  }
}
