package com.lianfeng.common.exception;

import com.lianfeng.common.response.ResponseCode;
import lombok.Data;

/**
 * 自定义全局业务异常类
 */
@Data
public class LFBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public LFBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public LFBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public LFBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public LFBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }

}
