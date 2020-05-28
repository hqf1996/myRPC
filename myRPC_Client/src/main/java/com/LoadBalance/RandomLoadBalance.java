package com.LoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 10:29 2020/5/28
 * @Modified By:
 */
public class RandomLoadBalance implements AbstractLoadBalance{

    @Override
    public String[] doSelect(List<String> Service) {
        String[] result = new String[2];
        Random random = new Random();
        int randomi = random.nextInt(Service.size());
        String ipAndport = Service.get(randomi);
        String[] split = ipAndport.split(":");
        result[0] = split[0];
        result[1] = split[1].split("#")[0];
        System.out.println("当前随机访问的机器是：" + result[0] + ":" + result[1]);
        return result;
    }
}
