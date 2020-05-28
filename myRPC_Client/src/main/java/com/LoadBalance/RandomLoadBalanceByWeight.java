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
        String[] result = new String[2];  // ���ص�����+�˿ں�
        Map<String, Integer> URLWeightMap = new LinkedHashMap<String, Integer>();   // URL��Ȩ�ص�ӳ���
        List<Integer> allWeight = new LinkedList<Integer>();      //��¼����������Ȩ��
        Random random = new Random();
        int length = Service.size();
        int totalWeight = 0;
        boolean sameWeight = true;
        for (int i = 0 ; i < length ; ++i) {
            String[] tmp = Service.get(i).split(":");
            String url = tmp[0] + ":" + tmp[1].split("#")[0];
            int weight = Integer.valueOf(tmp[1].split("#")[1]);         // ���ÿһ̨��������Ȩ��
            totalWeight+=weight;
            URLWeightMap.put(url, weight);
            allWeight.add(weight);
            if (sameWeight && i>0 && weight!=allWeight.get(i-1)) {
                sameWeight = false;
            }
        }
        // ��Ȩ�����ѡ��
        if (!sameWeight && totalWeight > 0) {
            int selectI = -1;
            int offset = random.nextInt(totalWeight);
            // ������õĺ�dubbo�ٷ�ʵ�ֵĴ�Ȩ�ص������ѯ���в�ͬ���������ս����һ�µġ�
            // ��������̨�����������ǵ�Ȩ�طֱ���[5,2,3]��totalWeight=10������һ��0-10�������offset��������4�����һ�����бȽϣ�С��5��������Ϊ��һ̨�ͱ�ѡ���ˣ�������50%��
            // ͬ���ڶ�̨�͵���̨ѡ��ĸ��ʷֱ���20%��30%������������ж�θ��ؾ�����Է��ָ�������5:2:3��
            outer:
            while (true) {
                for (int i = 0; i < length; ++i) {
                    if (offset <= allWeight.get(i)) {
                        selectI = i;
                        break outer;
                    }
                }
            }
            // ���ص�į��������host+port
            int ii = 0;
            for (Map.Entry<String, Integer> each : URLWeightMap.entrySet()) {
                if (ii == selectI) {
                    result[0] = each.getKey().split(":")[0];
                    result[1] = each.getKey().split(":")[1];
                    return result;
                }
            }
        }
        // ���ÿ̨��������Ȩ����ȣ��򷵻������һ̨��
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
