package com.yeoboya.lunch.api.v1.common.service;

import java.beans.PropertyEditorSupport;

public class LowerCaseEnumEditor<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> enumType;

    public LowerCaseEnumEditor(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public void setAsText(String text) {
        if (text == null || text.isBlank()) {
            setValue(null);
            return;
        }

        try {
            T value = Enum.valueOf(enumType, text.toUpperCase());
            setValue(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName() + ": " + text);
        }
    }
}