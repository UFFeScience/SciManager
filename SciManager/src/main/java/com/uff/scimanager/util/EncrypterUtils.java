package com.uff.scimanager.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.base.Throwables;
import com.uff.scimanager.exception.ExpiredTokenException;

public class EncrypterUtils {
	
	private static final Logger log = LoggerFactory.getLogger(EncrypterUtils.class);
	
	private static final byte[] SALT = {
		(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
        (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
	};
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	private static final String APP_ENCODE_SECRET_KEY = "token-key";
	private static final String PBE_ALGORITHM = "PBEWithMD5AndDES";
	private static final int ITERATION_COUNT = 19;
	
	public static String encryptPassword(String passwordToHash) {
		return new BCryptPasswordEncoder().encode(passwordToHash);
	}
	
	public static String generateUserResetPasswordToken(Long userId, String email) {
		log.info("Iniciando processo de criptografia do token de userId {} e email{}:", userId, email);
		
		Calendar limitDate = Calendar.getInstance();
		limitDate.add(Calendar.DATE, 2);
		
		return encryptpWithPBE(userId + "_" + email + "_" + limitDate.getTimeInMillis());
	}
	
	public static Map<String, Object> getUserResetPasswordToken(String token) throws ExpiredTokenException {
		log.info("Iniciando processo de decriptografia do token: {}", token);
		String decryptedToken = decryptpWithPBE(token);
		
		if (decryptedToken == null || "".equals(decryptedToken)) {
			return null;
		}
		
		String[] userTokenPayload = decryptedToken.split("_");
		
		if (userTokenPayload.length < 3) {
			return null;
		}
		
		Calendar currentDate = Calendar.getInstance();
		Calendar limitDate = Calendar.getInstance();
		limitDate.setTimeInMillis(Long.parseLong(userTokenPayload[2]));
		
		if (limitDate.before(currentDate)) {
			log.info("Token expirado: {}", token);
			throw new ExpiredTokenException("O token [" + token + "] expirou");
		}
		
		Map<String, Object> userData = new HashMap<String, Object>();
		userData.put("userId", Long.parseLong(userTokenPayload[0]));
		userData.put("email", userTokenPayload[1]);
		
		return userData;
	}
	
	public static String encryptpWithPBE(String text) {
		try {
			KeySpec keySpec = new PBEKeySpec(APP_ENCODE_SECRET_KEY.toCharArray(), SALT, ITERATION_COUNT);
			
			SecretKey key = SecretKeyFactory.getInstance(PBE_ALGORITHM).generateSecret(keySpec);        
	        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

	        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);      
	        
	        byte[] in = text.getBytes(DEFAULT_ENCODING);
	        byte[] out = cipher.doFinal(in);
	        
	        return Base64.encodeBase64String(out);
		} 
		catch (Exception e) {
			log.error("Error while encrypting text with AES algorithm\n{}", Throwables.getStackTraceAsString(e));
		}
		
		return null;
	}
	
	public static String decryptpWithPBE(String encryptedText) {
		try {
			KeySpec keySpec = new PBEKeySpec(APP_ENCODE_SECRET_KEY.toCharArray(), SALT, ITERATION_COUNT);

			SecretKey key = SecretKeyFactory.getInstance(PBE_ALGORITHM).generateSecret(keySpec);        
	        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

	        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
	        
	        byte[] enc =  Base64.decodeBase64(encryptedText);
	        byte[] utf8 = cipher.doFinal(enc);
	        
	        return new String(utf8, DEFAULT_ENCODING);
		} 
		catch (Exception e) {
			log.error("Error while decrypting text with AES algorithm\n{}", Throwables.getStackTraceAsString(e));
		}
		
		return null;
	}
	
	public static String encodeValueToURL(String value) {
		try {
			return URLEncoder.encode(value, DEFAULT_ENCODING);
		} 
		catch (UnsupportedEncodingException e) {
			log.error("Erro ao codificar valor {} para URL\n{}", value, Throwables.getStackTraceAsString(e));
			return null;
		}
	}
}