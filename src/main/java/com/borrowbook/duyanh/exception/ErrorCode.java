package com.borrowbook.duyanh.exception;

import com.borrowbook.duyanh.configuration.Translator;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    DELETE_POST_SUCCESS(1010,Translator.toLocale("delete.post.success"),HttpStatus.BAD_REQUEST),
    DELETE_POST_FAIL(1010,Translator.toLocale("delete.post.fail"),HttpStatus.BAD_REQUEST),
    DELETE_COMMENT_SUCCESS(1009,Translator.toLocale("delete.comment.success"),HttpStatus.BAD_REQUEST),
    DELETE_COMMENT_FAIL(1009,Translator.toLocale("delete.comment.fail"),HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006,Translator.toLocale("unauthenticated"),HttpStatus.NOT_FOUND),
    USER_NOT_EXITED(1006, Translator.toLocale("user.not.existed"), HttpStatus.BAD_REQUEST),
    ONGOING_BORROWINGS(1008,Translator.toLocale("ongoing.borrowings"),HttpStatus.BAD_REQUEST),
    BOOK_LIMIT_EXCEEDED(1008, Translator.toLocale("book.limit.exceeded"),HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1008,Translator.toLocale("unauthorized"),HttpStatus.FORBIDDEN),
    USER_BANNED(1006, Translator.toLocale("user.banned"), HttpStatus.FORBIDDEN),  // 403 Forbidden
    ENOUGH_BOOK(1007, Translator.toLocale("enough.book"), HttpStatus.BAD_REQUEST),  // 400 Bad Request
    ERROR(1001, Translator.toLocale("error"), HttpStatus.INTERNAL_SERVER_ERROR),  // 500 Internal Server Error
    EXIST_PHONE_NUMBER(1006, Translator.toLocale("exist.phone.number"), HttpStatus.CONFLICT),  // 409 Conflict
    EXIST_EMAIL(1006, Translator.toLocale("exist.email"), HttpStatus.CONFLICT),  // 409 Conflict
    USER_EXISTED(1006, Translator.toLocale("user.existed"), HttpStatus.CONFLICT),
    USER_UPDATED(1006, Translator.toLocale("user.updated"), HttpStatus.OK),  // 200 OK
    USER_CREATED(1006, Translator.toLocale("user.created"), HttpStatus.CREATED),  // 201 Created
    USER_RETRIEVED(1006, Translator.toLocale("user.retrieved"), HttpStatus.OK),  // 200 OK
    SUCCESS(1002, Translator.toLocale("success"), HttpStatus.OK),  // 200 OK
    NOT_FOUND(1005, Translator.toLocale("not.found"), HttpStatus.NOT_FOUND),  // 404 Not Found
    NO_BOOK_DELETE(1005, Translator.toLocale("not.book.delete"), HttpStatus.BAD_REQUEST),
    BOOK_ACTIVE(1005, Translator.toLocale("book.active"), HttpStatus.OK),  // 200 OK
    BOOK_DELETED(1005, Translator.toLocale("book.delete"), HttpStatus.OK),  // 200 OK
    BOOK_UPDATED(1005, Translator.toLocale("book.updated"), HttpStatus.OK),  // 200 OK
    BOOK_CREATED(1005, Translator.toLocale("book.created"), HttpStatus.CREATED),  // 201 Created
    ERROR_DELETE(1004, Translator.toLocale("error.delete"), HttpStatus.NOT_FOUND),  // 404 Not Found
    CATEGORY_EXIST_DELETED(1004, Translator.toLocale("category.exist.deleted"), HttpStatus.CONFLICT),  // 409 Conflict
    CATEGORY_RETRIEVED(1004, Translator.toLocale("category.retrieved"), HttpStatus.OK),  // 200 OK
    CATEGORY_DELETED(1004, Translator.toLocale("category.deleted"), HttpStatus.OK),  // 200 OK
    CATEGORY_CREATED(1004, Translator.toLocale("category.created"), HttpStatus.CREATED),  // 201 Created
    EXISTED_CATEGORY_NAME(1001, Translator.toLocale("category.name.exist"), HttpStatus.CONFLICT);  // 409 Conflict
    //NOT_BLANK(1003, "The field cannot be blank.", HttpStatus.BAD_REQUEST);


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
