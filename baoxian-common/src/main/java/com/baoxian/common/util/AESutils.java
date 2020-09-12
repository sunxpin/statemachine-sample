package com.baoxian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密解密工具类
 */
public class AESutils {
    private static Logger logger = LoggerFactory.getLogger(AESutils.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String KEY_AES = "AES";
    private static final int KEY_LENGTH = 16;
    private static final String KEY = "873kNkYro9r1bc59";

    /**
     * AES/ECB/PKCS5Padding 128位模式加密
     *
     * @param body 加密域
     * @param key  秘钥
     * @return 加密结果
     */
    public static String encrypt128(String body, String key) {
        try {
            if (StringUtils.isBlank(body) || StringUtils.isBlank(key)) {
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != KEY_LENGTH) {
                logger.error("Key长度不是16位");
                return null;
            }
            // 两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "AES");
            // 实例化加密类，参数为加密方式，要写全
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            // 初始化，此方法可以采用三种方式，按加密算法要求来添加。
            // （1）无第三个参数
            // （2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
            // （3）采用此代码中的IVParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            // 加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX,
            // UUE,7bit等等。此处看服务器需要什么编码方式
            byte[] encryptedData = cipher.doFinal(body.getBytes(DEFAULT_CHARSET));
            return new String(Base64.getEncoder().encode(encryptedData)).replaceAll("[\\s*\t\n\r]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt128(String body){
        return encrypt128(body, KEY);
    }

    /**
     * AES/ECB/PKCS5Padding 128位模式解密
     *
     * @param body 解密字符串
     * @param key  秘钥
     * @return 解密结果
     */
    public static String decrypt128(String body, String key) {
        try {
            if (StringUtils.isBlank(body) || StringUtils.isBlank(key)) {
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != KEY_LENGTH) {
                logger.error("Key长度不是16位");
                return null;
            }
            byte[] byteMi = Base64.getDecoder().decode(body.getBytes("UTF-8"));
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 与加密时不同MODE:Cipher.DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, DEFAULT_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt128(String body){
        return decrypt128(body, KEY);
    }

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return 加密结果
     */
    public static String encrypt(String data, String key) {
        return doAES(data, key, Cipher.ENCRYPT_MODE);
    }

    public static String encrypt(String data){
        return encrypt(data, KEY);
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return 解密结果
     */
    public static String decrypt(String data, String key) {
        return doAES(data, key, Cipher.DECRYPT_MODE);
    }

    public static String decrypt(String data){
        return decrypt(data, KEY);
    }

    /**
     * 加解密
     *
     * @param data 待处理数据
     * @param mode 加解密mode
     * @return 加解密结果
     */
    private static String doAES(String data, String key, int mode) {
        try {
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                return null;
            }
            //判断是加密还是解密  
            boolean encrypt = mode == Cipher.ENCRYPT_MODE;
            byte[] content;
            //true 加密内容 false 解密内容  
            if (encrypt) {
                content = data.getBytes(DEFAULT_CHARSET);
            } else {
                content = parseHexStr2Byte(data);
            }
            if (content == null) {
                return null;
            }
            //1.构造密钥生成器，指定为AES算法,不区分大小写  
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            //2.根据ecnodeRules规则初始化密钥生成器  
            //生成一个128位的随机源,根据传入的字节数组  
            kgen.init(128, random);
            //3.产生原始对称密钥  
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组  
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥  
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, KEY_AES);
            //6.根据指定算法AES自成密码器  
            Cipher cipher = Cipher.getInstance(KEY_AES);
            // 创建密码器
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY  
            cipher.init(mode, keySpec);
            // 初始化
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                //将二进制转换成16进制  
                return parseByte2HexStr(result);
            } else {
                return new String(result, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param b 2进制字节
     * @return 16进制
     */
    private static String parseByte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param str 16进制字符串
     * @return 结果
     */
    private static byte[] parseHexStr2Byte(String str) {
        if (str.length() < 1) {
            return null;
        }
        int length = str.length() / 2;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Integer.parseInt(str.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(str.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) {
        String content = "com.baoxian.demo.dbservices.SyUserroleService.queryUserByRoleid";
        System.out.println("加密前：" + content);
        String encrypt = encrypt(content, KEY);
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(encrypt, KEY);
        System.out.println("解密后：" + decrypt);
        String encrypt128 = encrypt128(content, KEY);
        System.out.println("128方式加密后：" + encrypt128);
        String decrypt128 = decrypt128(encrypt128, KEY);
        System.out.println("128方式解密后：" + decrypt128);
    }
}  
