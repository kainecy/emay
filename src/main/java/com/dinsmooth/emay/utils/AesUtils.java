package com.dinsmooth.emay.utils;

import com.dinsmooth.common.exception.DinSmoothRuntimeException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES加解密工具
 *
 */
public class AesUtils {

    private static final String ALGORITHM = "AES";

    private AesUtils(){
        //empty
    }

    /**
     * Encrypts the given plain text
     *
     * @param key       key
     * @param plainText The plain text to encrypt
     */
    public static byte[] encrypt(byte[] key, byte[] plainText) {
        if(key==null || key.length==0){
            throw new DinSmoothRuntimeException("空的参数 key");
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new DinSmoothRuntimeException("加密发生错误",e);
        }
    }

    /**
     * Decrypts the given byte array
     *
     * @param cipherText The data to decrypt
     */
    public static byte[] decrypt(byte[] key, byte[] cipherText) {
        if(key==null || key.length==0){
            throw new DinSmoothRuntimeException("空的参数 key");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            return cipher.doFinal(cipherText);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new DinSmoothRuntimeException("解密发生错误",e);
        }
    }

}
