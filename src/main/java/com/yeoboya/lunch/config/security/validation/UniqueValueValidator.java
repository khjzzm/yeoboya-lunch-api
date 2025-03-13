package com.yeoboya.lunch.config.security.validation;

import com.yeoboya.lunch.api.v1.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueValueValidator implements ConstraintValidator<CustomUniqueValue, String> {

    private final MemberRepository memberRepository;
    private FieldType fieldType;

    @Override
    public void initialize(CustomUniqueValue constraintAnnotation) {
        this.fieldType = constraintAnnotation.fieldType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        switch (fieldType) {
            case LOGIN_ID:
                return !memberRepository.existsMemberByLoginId(value);
            case EMAIL:
                return !memberRepository.existsMemberByEmail(value);
            default:
                return false;
        }
    }
}