package com.yeoboya.lunch.config.security.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValueValidator.class)
public @interface CustomUniqueValue {
    FieldType fieldType();
    String message() default "이미 존재하는 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String providerField() default ""; // ✅ 추가
}