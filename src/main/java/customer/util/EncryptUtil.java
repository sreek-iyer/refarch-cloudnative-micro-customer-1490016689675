package customer.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtil {
    private static Logger logger =  LoggerFactory.getLogger(EncryptUtil.class);
	/*
	 * AES-256 encrypt some text using some key 
	 */
	public static String encrypt(String key, String plaintext) throws Exception {
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			final byte[] keyBytes = md.digest(key.getBytes("UTF-8"));
			
			// use zero-byte IV (as we never use the same key twice) -- in practice you may
			// want to store separate IV in key-protect but we will save on key storage here
			final byte[] ivBytes = new byte[16];
			Arrays.fill(ivBytes, (byte)0);
			
			final String encryptionAlgorithm = "AES/CBC/PKCS5Padding";
			final IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(ivBytes, 16));
			final SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			final Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
			
			final byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/*
	 * AES-256 decrypt some text using a key 
	 */
	public static String decrypt(String key, String encryptedText) throws Exception {
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			final byte[] keyBytes = md.digest(key.getBytes("UTF-8"));
			
			// use zero-byte IV (as we never use the same key twice) -- in practice you may
			// want to store separate IV in key-protect but we will save on key storage here
			final byte[] ivBytes = new byte[16];
			Arrays.fill(ivBytes, (byte)0);
			
			final String encryptionAlgorithm = "AES/CBC/PKCS5Padding";
			final IvParameterSpec ivParameterSpec = new IvParameterSpec(Arrays.copyOf(ivBytes, 16));
			final SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			final Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
			
			final byte[] byteArr = Base64.getDecoder().decode(encryptedText);
			final byte[] original = cipher.doFinal(byteArr);
			
			return new String(original);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	
	}
	

}
