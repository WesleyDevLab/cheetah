package com.zhaijiong.analyze;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-19.
 */
public class Result {
    /** @Fields code: 状态码 */
    private int code;
    /** @Fields message: 异常信息*/
    private String message;

    private Object result;

    public Result(Object pojo) {
        this.result = pojo;
    }

    public static Result failResult(ResultCode resultCode,String message){
        Result r = new Result("");
        r.setCode(resultCode.getValue());
        r.setMessage(message);
        return r;
    }
    public static Result successResult(Object obj){
        Result r = new Result(obj);
        r.setCode(ResultCode.OK.getValue());
        r.setMessage("success");
        return r;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
