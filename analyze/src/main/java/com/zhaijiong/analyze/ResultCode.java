package com.zhaijiong.analyze;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-19.
 */
public enum ResultCode {
    /**
     * 200  OK 正常返回
     * 202  Accepted  异步调用执行任务已接受
     * 400  Bad Request  请求格式,传递参数错误(例如调试模板时缺少参数，或参数格式非法)
     * 401  Precondition Failed  授权认证失败
     * 402  Config Error  配置参数错误 (例如模板，数据源配置错误)
     * 403  Forbidden   禁止访问 (例如模板已下线)
     * 410  Security Failed  访问频率过高
     * 412  TimeOut Failed  访问超时失败
     * 413  Server Refused  服务器拒绝访问 (例如线上环境访问还未发布上线的模板)
     * 500  Internal Server Error  内部服务错误（系统内部服务错误）
     */
    OK(200, "OK"),
    ACCEPTED(202, "ACCEPTED"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    PRECONDITION_FAILED(401, "PRECONDITION_FAILED"),
    CONFIG_ERROR(402, "CONFIG_ERROR"),
    FORBIDDEN(403, "FORBIDDEN"),
    SECURITY_FAILED(410, "SECURITY_FAILED"),
    TIMEOUT_FAILED(412, "TIMEOUT_FAILED"),
    SERVER_REFUSED(413, "SERVER_REFUSED"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private int value;
    private String message;

    ResultCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
