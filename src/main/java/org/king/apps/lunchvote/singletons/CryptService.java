package org.king.apps.lunchvote.singletons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.jasypt.util.text.BasicTextEncryptor;

public class CryptService {
	
	public static CryptService instance;
	
	public static CryptService getInstance() {
		if(instance == null) {
			try {
				instance = new CryptService();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	private final BasicTextEncryptor cryptService;
	private final MessageDigest messageDigest;
	
	private CryptService() throws NoSuchAlgorithmException {
		this.cryptService = new BasicTextEncryptor();
		this.cryptService.setPassword(UUID.randomUUID().toString());
		
		this.messageDigest = MessageDigest.getInstance("SHA-256");
	}
	
	public String hash(String message) {
		messageDigest.update(message.getBytes());
		return new String(messageDigest.digest());
	}
	
	public String encrypt(String message) {
		return cryptService.encrypt(message);
	}
	
	public String decrypt(String encryptedMessage) {
		return cryptService.decrypt(encryptedMessage);
	}

}
