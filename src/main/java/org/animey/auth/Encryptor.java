package org.animey.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class Encryptor {
    private final String salt;
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    protected Encryptor(String salt) {
        this.salt = salt;
    }
    protected static String generateSalt(){
        StringBuilder finalVal = new StringBuilder(50);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 50; i++) {
            finalVal.append(characters.charAt(random.nextInt(characters.length())));
        }
        return finalVal.toString();
    }
    protected String encrypt(String val) {
        try {
            val = val + salt;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(val.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return "error";
    }
}
