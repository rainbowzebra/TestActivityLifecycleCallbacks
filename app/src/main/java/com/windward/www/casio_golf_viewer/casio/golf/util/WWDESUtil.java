package com.windward.www.casio_golf_viewer.casio.golf.util;

import android.text.TextUtils;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by ww on 2015/11/17.
 */
public class WWDESUtil {

    private static String iv = "b7n5shengchoupu8";// 虚拟的 iv (需更改)
    private static String secretKey = "3rt8shengchouty9";// 虚拟的 密钥 (需更改）

    public WWDESUtil() {

    }


    public static byte[] encrypt(String text) throws Exception {
        if (TextUtils.isEmpty(text))
            throw new Exception("Empty string");
        DESKeySpec desKey = new DESKeySpec(secretKey.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");

        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());

        try {
            cipher.init(Cipher.ENCRYPT_MODE, securekey, ivSpec);
            return cipher.doFinal(text.getBytes());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


}
