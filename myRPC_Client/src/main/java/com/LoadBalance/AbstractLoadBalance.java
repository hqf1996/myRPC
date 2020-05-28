package com.LoadBalance;

import com.spi.annotation.mySPI;

import java.util.List;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 10:27 2020/5/28
 * @Modified By:
 */
@mySPI(value = "defaultmethod")
public interface AbstractLoadBalance {
    public String[] doSelect(List<String> Service);
}
