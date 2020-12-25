package com.herocheer.common.exception;

import com.herocheer.common.constants.ResponseCode;

/**
 * @desc 通用异常类
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
public class CommonException extends RuntimeException {

    private int errorCode;

    private Class<?> errorClass;

    private String errorMsg;

    private String detailErrorMsg;

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param cause     the cause (which is saved for later retrieval by the              {@link #getCause()} method).  (A <tt>null</tt> value is              permitted, and indicates that the cause is nonexistent or              unknown.)
     * @param errorCode the error code
     * @param errorMsg  the error msg
     * @since 1.4
     */
    public CommonException(Throwable cause, int errorCode, String errorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode  the error code
     * @param errorMsg   the error msg
     * @param errorClass the error class
     */
    public CommonException(int errorCode, String errorMsg, Class<?> errorClass) {
        this.errorClass = errorClass;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param cause      the cause (which is saved for later retrieval by the              {@link #getCause()} method).  (A <tt>null</tt> value is              permitted, and indicates that the cause is nonexistent or              unknown.)
     * @param errorCode  the error code
     * @param errorClass the error class
     * @param errorMsg   the error msg
     * @since 1.4
     */
    public CommonException(Throwable cause, int errorCode, Class<?> errorClass, String errorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorClass = errorClass;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode the error code
     * @param errorMsg  the error msg
     */
    public CommonException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorMsg  the error msg
     */
    public CommonException(String errorMsg) {
        this.errorCode = ResponseCode.SUCCESS;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode  the error code
     * @param errorClass the error class
     * @param errorMsg   the error msg
     */
    public CommonException(int errorCode, Class<?> errorClass, String errorMsg) {
        this.errorCode = errorCode;
        this.errorClass = errorClass;
        this.errorMsg = errorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode      the error code
     * @param errorMsg       the error msg
     * @param detailErrorMsg the detail error msg
     */
    public CommonException(int errorCode, String errorMsg, String detailErrorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.detailErrorMsg = detailErrorMsg;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorCode      the error code
     * @param errorClass     the error class
     * @param errorMsg       the error msg
     * @param detailErrorMsg the detail error msg
     */
    public CommonException(int errorCode, Class<?> errorClass, String errorMsg, String detailErrorMsg) {
        this.errorCode = errorCode;
        this.errorClass = errorClass;
        this.errorMsg = errorMsg;
        this.detailErrorMsg = detailErrorMsg;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param cause          the cause (which is saved for later retrieval by the              {@link #getCause()} method).  (A <tt>null</tt> value is              permitted, and indicates that the cause is nonexistent or              unknown.)
     * @param errorCode      the error code
     * @param errorMsg       the error msg
     * @param detailErrorMsg the detail error msg
     * @since 1.4
     */
    public CommonException(Throwable cause, int errorCode, String errorMsg, String detailErrorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.detailErrorMsg = detailErrorMsg;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param cause          the cause (which is saved for later retrieval by the              {@link #getCause()} method).  (A <tt>null</tt> value is              permitted, and indicates that the cause is nonexistent or              unknown.)
     * @param errorCode      the error code
     * @param errorClass     the error class
     * @param errorMsg       the error msg
     * @param detailErrorMsg the detail error msg
     * @since 1.4
     */
    public CommonException(Throwable cause, int errorCode, Class<?> errorClass, String errorMsg, String detailErrorMsg) {
        super(cause);
        this.errorCode = errorCode;
        this.errorClass = errorClass;
        this.errorMsg = errorMsg;
        this.detailErrorMsg = detailErrorMsg;
    }

    @Override
    public String getMessage() {
        return this.errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Class<?> getErrorClass() {
        return errorClass;
    }

    public void setErrorClass(Class<?> errorClass) {
        this.errorClass = errorClass;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDetailErrorMsg() {
        return detailErrorMsg;
    }

    public void setDetailErrorMsg(String detailErrorMsg) {
        this.detailErrorMsg = detailErrorMsg;
    }

}
