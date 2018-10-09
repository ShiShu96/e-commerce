package com.xy.ecommerce.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * highly reusable response object
 * @param <T>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private Response(int status) {
        this.status = status;
    }

    private Response(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private Response(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private Response(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public boolean isSuccess(){
        return status==ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static <T> Response<T> createBySuccess(){
        return new Response<>(ResponseCode.SUCCESS.getCode());
    }


    public static <T> Response<T> createBySuccessMessage(String msg){
        return new Response<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> Response<T> createBySuccess(T data){
        return new Response<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> Response<T> createBySuccess(String msg,T data){
        return new Response<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }


    public static <T> Response<T> createByError(){
        return new Response<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getMsg());
    }


    public static <T> Response<T> createByErrorMessage(String errorMessage){
        return new Response<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> Response<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new Response<T>(errorCode,errorMessage);
    }
}
