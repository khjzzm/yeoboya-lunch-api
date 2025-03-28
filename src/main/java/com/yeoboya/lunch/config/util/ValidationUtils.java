package com.yeoboya.lunch.config.util;

import com.yeoboya.lunch.api.v1.common.response.ValidationError;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidationUtils {

    /**
     * 스프링의 Errors 객체에서 필드별 유효성 검사 실패 정보를 추출하여
     * ValidationError 리스트로 변환합니다.
     * <p>
     * 주로 @Valid, @Validated 사용 시 발생하는 MethodArgumentNotValidException,
     * BindException 등의 오류를 처리할 때 사용됩니다.
     *
     * @param errors 스프링 Errors 객체
     * @return ValidationError 리스트 (field + message 포함)
     */
    public static List<ValidationError> refineErrors(final Errors errors) {
        return errors.getFieldErrors().stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    /**
     * javax.validation.ConstraintViolationException에서 유효성 검사 실패 정보를 추출하여
     * ValidationError 리스트로 변환합니다.
     * <p>
     * 주로 @RequestParam, @PathVariable 등의 파라미터에 대한 유효성 검증 실패 시 사용됩니다.
     * 예: @Min, @NotBlank, @Size 등의 제약 조건 위반
     *
     * @param ex ConstraintViolationException 예외 객체
     * @return ValidationError 리스트 (field + message 포함)
     */
    public static List<ValidationError> refineErrors(final ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(e -> {
                    String propertyPath = String.valueOf(e.getPropertyPath());
                    String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1); // 마지막 필드 이름 추출
                    return new ValidationError(field, e.getMessage());
                })
                .collect(Collectors.toList());
    }

    public static void printBeanNames(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.stream(beanNames)
                .filter(n -> !n.contains("springframework"))
                .forEach(System.out::println);
    }


}