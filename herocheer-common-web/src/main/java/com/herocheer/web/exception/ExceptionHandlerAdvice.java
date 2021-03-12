package com.herocheer.web.exception;

import com.herocheer.common.base.ResponseResult;
import com.herocheer.common.constants.ResponseCode;
import com.herocheer.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @desc 异常拦截处理
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@ResponseBody
@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    /**
     * Instantiates a new Exception handler advice.
     */
    public ExceptionHandlerAdvice() {
        log.debug("Enabled Exception Handler Advice [启动服务异常处理]");
    }

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(CommonException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseResult handleException(HttpServletRequest request, CommonException e) {
        if (e.getErrorClass() != null) {
            log.error("异常，uri：{},msg:{}",request.getRequestURI(),e.getMessage(), e);
        } else {
            log.error("异常，uri：{}", request.getRequestURI(), e);
        }
        return ResponseResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        String parameterType = e.getParameterType();
        log.error("缺少必填参数{} {}", parameterType, parameterName, e);
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "缺少必填参数:" + parameterName);
    }

    /**
     * Handle exception error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult handleException(HttpServletRequest request,HttpRequestMethodNotSupportedException e) {
        final String message = e.getMessage();
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),message, e);

        final String[] split = message.split("'");
        if (split.length >= 2) {
            return ResponseResult.fail(ResponseCode.SERVER_ERROR, "请求方式错误"+split[1]);
        }
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "请求方式错误");
    }

    /**
     * Handle exception error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleException(HttpServletRequest request,Throwable e) {
        final String message = e.getMessage() != null ? e.getMessage() : e.toString();
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),message, e);
        final String regEx = "[\u4e00-\u9fa5]";
        final Pattern p = Pattern.compile(regEx);
        if (p.matcher(message).find()) {
            return ResponseResult.fail(ResponseCode.SERVER_ERROR, message);
        }
        if (message.contains("timeout") || message.contains("timedout")) {
            return message.contains("refused") ? ResponseResult.fail(ResponseCode.SERVER_ERROR, "服务器拒绝连接")
                    : ResponseResult.fail(ResponseCode.SERVER_ERROR, "服务器拒绝连接");
        }
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "服务器内部异常");
    }

    /**
     * Handle exception failed result.
     *
     * @param e the e
     * @return the failed result
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult handleException(HttpServletRequest request,Exception e) {
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),e.getMessage(), e);
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, this.getBindMessage(e.getMessage()));
    }

    /**
     * No mapping error result.
     *
     * @param e the exception
     * @return the error result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseResult noMapping(HttpServletRequest request,NoHandlerFoundException e) {
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),e.getMessage(), e);
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "请求路径不存在");
    }

    /**
     * Error param error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult errorParam(HttpServletRequest request,MethodArgumentTypeMismatchException e) {
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),e.getMessage(), e);
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "请求参数不合法");
    }

    /**
     * Handle HttpMediaTypeNotSupportedException result
     *
     * @param e exception
     * @return the error result
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseResult handleException(HttpServletRequest request,HttpMediaTypeNotSupportedException e) {
        final String message = e.getMessage();
        log.error("异常，uri：{},msg:{}",request.getRequestURI(),e.getMessage(), e);
        final String[] split = message.split("'");
        if (split.length >= 2) {
            return ResponseResult.fail(ResponseCode.SERVER_ERROR, "参数文本类型错误"+split[1]);
        }
        return ResponseResult.fail(ResponseCode.SERVER_ERROR, "参数文本类型错误");
    }


    private String getBindMessage(String str) {
        if (StringUtils.hasText(str)) {
            String[] sa = str.split("message");
            if (sa.length > 0) {
                for (int i = sa.length - 1; i >= 0; --i) {
                    if (sa[i].getBytes().length != sa[i].length()) {
                        str = sa[i].trim().replace("[", "");
                        String[] st = str.split("]");
                        if (st.length > 0) {
                            str = st[0];
                        }
                        break;
                    }
                }
            }
        }
        return str;
    }

}
