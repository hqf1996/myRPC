package com.spi.loader;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 14:09 2020/1/1
 * @Modified By:
 */
public class Holder<T> {
    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
            return value;
        }
}
