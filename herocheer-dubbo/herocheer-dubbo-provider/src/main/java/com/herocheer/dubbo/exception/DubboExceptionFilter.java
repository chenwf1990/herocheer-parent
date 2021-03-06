package com.herocheer.dubbo.exception;

import com.herocheer.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

/**
 * @desc dubbo异常处理
 * @author chenwf
 * @create 2020/12/21
 * @company 厦门熙重电子科技有限公司
 */
@Slf4j
@Activate(group = CommonConstants.PROVIDER)
public class DubboExceptionFilter extends ListenableFilter {

        public DubboExceptionFilter() {
            super.listener = new CurrExceptionListener();
        }

        @Override
        public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
            return invoker.invoke(invocation);
        }

        static class CurrExceptionListener extends ExceptionListener {

            @Override
            public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {

                // 发生异常，并且非泛化调用
                if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
                    try {
                        Throwable exception = appResponse.getException();
                        log.error("exception error: ", exception);
                        // 1 如果是 CommonException 异常，直接返回
                        if (exception instanceof CommonException) {
                            return;
                        }
                        appResponse.setException(new CommonException(200, "远程调用失败", StringUtils.toString(exception)));
                        return;
                    } catch (Throwable e) {
                        log.warn("Fail to DubboExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
                        return;
                    }
                }

            }
        }

        static class ExceptionListener implements Listener {

            @Override
            public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
                if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
                    try {
                        Throwable exception = appResponse.getException();

                        // directly throw if it's checked exception
                        if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                            return;
                        }
                        // directly throw if the exception appears in the signature
                        try {
                            Method method = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                            Class<?>[] exceptionClassses = method.getExceptionTypes();
                            for (Class<?> exceptionClass : exceptionClassses) {
                                if (exception.getClass().equals(exceptionClass)) {
                                    return;
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            return;
                        }

                        // for the exception not found in method's signature, print ERROR message in server's log.
                        log.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);

                        // directly throw if exception class and interface class are in the same jar file.
                        String serviceFile = ReflectUtils.getCodeBase(invoker.getInterface());
                        String exceptionFile = ReflectUtils.getCodeBase(exception.getClass());
                        if (serviceFile == null || exceptionFile == null || serviceFile.equals(exceptionFile)) {
                            return;
                        }
                        // directly throw if it's JDK exception
                        String className = exception.getClass().getName();
                        if (className.startsWith("java.") || className.startsWith("javax.")) {
                            return;
                        }
                        // directly throw if it's dubbo exception
                        if (exception instanceof RpcException) {
                            return;
                        }

                        // otherwise, wrap with RuntimeException and throw back to the client
                        appResponse.setException(new RuntimeException(StringUtils.toString(exception)));
                        return;
                    } catch (Throwable e) {
                        log.warn("Fail to ExceptionFilter when called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
                        return;
                    }
                }
            }

            @Override
            public void onError(Throwable e, Invoker<?> invoker, Invocation invocation) {
                log.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker.getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e.getMessage(), e);
            }

        }
}
