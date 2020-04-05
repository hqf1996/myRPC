package com.Message;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author: hqf
 * @description:
 * @Data: Create in 19:48 2020/4/4
 * @Modified By:
 */

public class HessianSerializeDeserializeMain {
    /**
     * ���л�
     * @param msg
     * @return
     */
    public static byte[] serialize(MSG msg){
        ByteArrayOutputStream byteArrayOutputStream = null;
        HessianOutput hessianOutput = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            // Hessian�����л����
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(msg);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                hessianOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * �����л�
     * @param msgArray
     * @return
     */
    public static MSG deserialize(byte[] msgArray) {
        ByteArrayInputStream byteArrayInputStream = null;
        HessianInput hessianInput = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(msgArray);
            // Hessian�ķ����л���ȡ����
            hessianInput = new HessianInput(byteArrayInputStream);
            return (MSG) hessianInput.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                hessianInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
