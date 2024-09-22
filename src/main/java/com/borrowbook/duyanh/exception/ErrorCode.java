package com.borrowbook.duyanh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    ONGOING_BORROWINGS(1008,"You already have ongoing borrowings.",HttpStatus.BAD_REQUEST),
    BOOK_LIMIT_EXCEEDED(1008, "You can only borrow up to 5 books.",HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1008,"You do not have permission",HttpStatus.FORBIDDEN),
    USER_BANNED(1006, "Your account has been banned", HttpStatus.FORBIDDEN),  // 403 Forbidden
    ENOUGH_BOOK(1007, "Not enough book available!", HttpStatus.BAD_REQUEST),  // 400 Bad Request
    ERROR(1001, "Error!", HttpStatus.INTERNAL_SERVER_ERROR),  // 500 Internal Server Error
    EXIST_PHONE_NUMBER(1006, "Phone number already exists!", HttpStatus.CONFLICT),  // 409 Conflict
    EXIST_EMAIL(1006, "Email already exists!", HttpStatus.CONFLICT),  // 409 Conflict
    USER_EXISTED(1006, "User updated successfully!", HttpStatus.OK),
    USER_UPDATED(1006, "User updated successfully!", HttpStatus.CONFLICT),  // 200 OK
    USER_CREATED(1006, "User created successfully!", HttpStatus.CREATED),  // 201 Created
    USER_RETRIEVED(1006, "User retrieved successfully!", HttpStatus.OK),  // 200 OK
    SUCCESS(1002, "Successfully!", HttpStatus.OK),  // 200 OK
    NOT_FOUND(1005, "Not found!", HttpStatus.NOT_FOUND),  // 404 Not Found
    BOOK_ACTIVE(1005, "The book activated successfully!", HttpStatus.OK),  // 200 OK
    BOOK_DELETED(1005, "The book deleted successfully!", HttpStatus.OK),  // 200 OK
    BOOK_UPDATED(1005, "The book updated successfully!", HttpStatus.OK),  // 200 OK
    BOOK_CREATED(1005, "The book created successfully!", HttpStatus.CREATED),  // 201 Created
    ERROR_DELETE(1004, "The id doesn't exist!", HttpStatus.NOT_FOUND),  // 404 Not Found
    CATEGORY_EXIST_DELETED(1004, "The category already exists and is in the deleted list!", HttpStatus.CONFLICT),  // 409 Conflict
    CATEGORY_RETRIEVED(1004, "The category retrieved successfully!", HttpStatus.OK),  // 200 OK
    CATEGORY_DELETED(1004, "The category deleted successfully!", HttpStatus.OK),  // 200 OK
    CATEGORY_CREATED(1004, "The category created successfully!", HttpStatus.CREATED),  // 201 Created
    EXISTED_CATEGORY_NAME(1001, "The category name already exists!", HttpStatus.CONFLICT),  // 409 Conflict
    NOT_BLANK(1003, "The field cannot be blank.", HttpStatus.BAD_REQUEST);


    private final int code;
    private final String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
