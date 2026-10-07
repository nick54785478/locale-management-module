package com.example.demo.application.shared.exception;

/**
 * 語系不存在例外
 */
public class LocaleNotFoundException extends ValidationException {
    
    private static final long serialVersionUID = 1L;

    public LocaleNotFoundException() {
        super("404", "LOCALE_NOT_FOUND");
    }
}
