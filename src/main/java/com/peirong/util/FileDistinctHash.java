package com.peirong.util;

import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class FileDistinctHash {
    public String getFileContent(File file) {
        StringBuilder sb = new StringBuilder();
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getHash(String input) {
        if (input == null) {
            input = "paperhanging";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
