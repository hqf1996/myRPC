package com.Message;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 23:47 2019/11/7
 * @Modified By:
 */
public class MSG implements Serializable {
    private static final long serialVersionUID = 1L;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] args;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "MSG{" +
                "methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
