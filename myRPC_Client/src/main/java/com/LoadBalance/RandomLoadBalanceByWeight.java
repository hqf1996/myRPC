package com.LoadBalance;

import java.util.*;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 10:29 2020/5/28
 * @Modified By:
 */
public class RandomLoadBalanceByWeight implements AbstractLoadBalance{
    @Override
    public String[] doSelect(List<String> Service) {
        String[] result = new String[2];  // 返回的主机+端口号
        Map<String, Integer> URLWeightMap = new LinkedHashMap<String, Integer>();   // URL与权重的映射表
        List<Integer> allWeight = new LinkedList<Integer>();      //记录所有主机的权重
        Random random = new Random();
        int length = Service.size();
        int totalWeight = 0;
        boolean sameWeight = true;
        for (int i = 0 ; i < length ; ++i) {
            String[] tmp = Service.get(i).split(":");
            String url = tmp[0] + ":" + tmp[1].split("#")[0];
            int weight = Integer.valueOf(tmp[1].split("#")[1]);         // 获得每一台服务器的权重
            totalWeight+=weight;
            URLWeightMap.put(url, weight);
            allWeight.add(weight);
            if (sameWeight && i>0 && weight!=allWeight.get(i-1)) {
                sameWeight = false;
            }
        }
        // 加权的随机选择
        if (!sameWeight && totalWeight > 0) {
            int selectI = -1;
            int offset = random.nextInt(totalWeight);
            // 我这边用的和dubbo官方实现的带权重的随机轮询略有不同，但是最终结果是一致的。
            // 比如有三台服务器，它们的权重分别是[5,2,3]，totalWeight=10。产生一个0-10的随机数offset，比如是4，与第一个进行比较，小于5，所以认为第一台就被选择到了，概率是50%。
            // 同理，第二台和第三台选择的概率分别是20%和30%。最终如果进行多次负载均衡可以发现概率满足5:2:3。
            outer:
            while (true) {
                for (int i = 0; i < length; ++i) {
                    if (offset <= allWeight.get(i)) {
                        selectI = i;
                        break outer;
                    }
                }
            }
            // 返回第i台服务器的host+port
            int ii = 0;
            for (Map.Entry<String, Integer> each : URLWeightMap.entrySet()) {
                if (ii == selectI) {
                    result[0] = each.getKey().split(":")[0];
                    result[1] = each.getKey().split(":")[1];
                    return result;
                }
            }
        }
        // 如果每台服务器的权重相等，则返回随机的一台。
        if (sameWeight) {
            String ipAndport = Service.get(random.nextInt(Service.size()));
            String[] split = ipAndport.split(":");
            result[0] = split[0];
            result[1] = split[1].split("#")[0];
            return result;
        }
        return result;
    }
}
