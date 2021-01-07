package com.herocheer.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenwf
 * @date 2020/11/16
 */
@ApiModel("响应参数")
@Data
public class ResponseResult<T> {
    @ApiModelProperty("响应标识")
    private String success;
    @ApiModelProperty("响应编码")
    private Integer code;
    @ApiModelProperty("响应时间戳")
    private Long timestamp;
    @ApiModelProperty("响应描述")
    private String message;
    @ApiModelProperty("响应对象")
    private T data;

    public ResponseResult(Integer code) {
        this.code = code;
    }

    public ResponseResult() {
        this.code = 200;
        this.success = "T";
        this.message = "操作成功";
        this.timestamp = System.currentTimeMillis();
    }
    public ResponseResult(T data) {
        this.code = 200;
        this.success = "T";
        this.message = "操作成功";
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }

    public static <T> ResponseResult<T> ok(T data) {
        return new ResponseResult<T>(data);
    }

    public static <T> ResponseResult<T> ok() {
        return new ResponseResult<T>();
    }

    public static <T> ResponseResult<T> fail(String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess("F");
        responseResult.setCode(200);
        responseResult.setMessage(message);
        responseResult.setTimestamp(System.currentTimeMillis());
        return responseResult;
    }

    public static <T> ResponseResult<T> fail() {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess("F");
        responseResult.setCode(200);
        responseResult.setMessage("操作失败");
        responseResult.setTimestamp(System.currentTimeMillis());
        return responseResult;
    }

    public static <T> ResponseResult<T> fail(Integer code, String message) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess("F");
        responseResult.setCode(code);
        responseResult.setMessage(message);
        responseResult.setTimestamp(System.currentTimeMillis());
        return responseResult;
    }

    /**
     * 更新或新增判断
     * @param count
     * @return
     */
    public static <T> ResponseResult<T> isSuccess(long count) {
        if(count > 0){
            return ok();
        }
        return fail();
    }

    public static <T> ResponseResult<T> getResponse(Integer code) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess("F");
        responseResult.setCode(code);
        responseResult.setTimestamp(System.currentTimeMillis());
        return responseResult;
    }

    public boolean isSuccess() {
        return "T".equals(this.success);
    }


    public T getData() {
        return this.data;
    }

    public ResponseResult setData(T data) {
        this.data = data;
        return this;
    }

    public String getSuccess() {
        return success;
    }

    public ResponseResult setSuccess(String success) {
        this.success = success;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public ResponseResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
