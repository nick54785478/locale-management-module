package com.example.demo.application.shared.exception;

/**
 * 語系已存在例外
 */
public class LocaleAlreadyExistsException extends ValidationException {
    
    private static final long serialVersionUID = 1L;

    public LocaleAlreadyExistsException() {
        super("400", "LOCALE_ALREADY_EXISTS");
    }
}
