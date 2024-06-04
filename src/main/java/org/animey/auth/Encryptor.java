package org.animey.auth;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

class Encryptor {
    private final String salt;
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final ComponentLogger log;
    protected Encryptor(String salt) {
        this.salt = salt;
        this.log = JavaPlugin.getPlugin(Auth.class).getComponentLogger();
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
            log.error(Component.text(e.getMessage(), Style.style(TextColor.color(200, 0, 0))));
        }
        return "error";
    }
}
