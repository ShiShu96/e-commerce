package com.xy.ecommerce.common;

/**
 * response code enums, used by Response
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    //...
    ILLEGAL_ARGUMENT(101,"ILLEGAL ARGUMENT"),
    //...
    USER_NOT_FOUND(201, "USER NOT FOUND"),
    INCORRECT_PASSWORD(202, "INCORRECT PASSWORD"),
    USER_NAME_TAKEN(203, "USER NAME IS ALREADY TAKEN"),
    EMAIL_TAKEN(204, "EMAIL IS ALREADY TAKEN"),
    EMPTY_QUESTION(205, "EMPTY QUESTION"),
    INCORRECT_ANSWER(206, "INCORRECT ANSWER"),
    EMPTY_TOKEN(207, "EMPTY TOKEN"),
    INVALID_TOKEN(208, "INVALID TOKEN"),
    TOKEN_NOT_MATCH(209, "TOKEN NOT MATCH"),
    //...
    PRODUCT_NOT_FOUND(301,"PRODUCT NOT FOUND"),
    PRODUCT_SOLD_OUT_OR_DELETED(302, "PRODUCT SOLD OUT OR DELETED"),
    PRODUCT_STOCK_NOT_ENOUGH(303,"PRODUCT STOCK NOT ENOUGH"),
    //....
    EMPTY_CART(501,"EMPTY CART"),
    //...
    ORDER_NOT_EXISTS(602, "ORDER NOT EXISTS"),
    //...
    NEED_LOGIN(901,"NEED LOGIN"),
    NOT_AUTHORIZED(902, "NOT AUTHORIZED");


    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseCode getResponseCode(int code){
        for (ResponseCode c:ResponseCode.values()){
            if (c.getCode()==code)
                return c;
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
