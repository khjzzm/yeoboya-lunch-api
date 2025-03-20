package com.yeoboya.lunch.api.v1.member.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {

    String message() default "핸드폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}