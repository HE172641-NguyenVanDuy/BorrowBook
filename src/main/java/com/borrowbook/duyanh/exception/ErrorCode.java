package com.borrowbook.duyanh.exception;

public enum ErrorCode {
    NOT_FOUND(1005, "Not found!"),
    BOOK_ACTIVE(1005,"The book active successfully!"),
    BOOK_RETRIEVED(1005, "The book retrieved successfully!"),
    BOOK_DELETED(1005, "The book deleted successfully!"),
    BOOK_UPDATED(1005, "The book updated successfully!"),
    BOOK_CREATED(1005, "The book created successfully!"),
    ERROR_DELETE(1004, "The id doesn't exist!"),
    CATEGORY_RETRIEVED(1004, "The category retrieved successfully!"),
    CATEGORY_DELETED(1004, "The category deleted successfully!"),
    CATEGORY_CREATED(1004, "The category created successfully!"),
    NOT_EXIST_CATEGORY(1002, "The category does not exist!"),
    EXISTED_CATEGORY_NAME(1001, "The category name is already exist!"),
    NOT_BLANK(1003, "The field can not be blank.");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
