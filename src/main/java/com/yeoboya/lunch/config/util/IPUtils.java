package com.yeoboya.lunch.config.util;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IPUtils {

    public static String getClientIP(HttpServletRequest request) {
        String[] headers = {"Proxy-Client-IP",
        "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR",
                "X-Real-IP", "X-RealIP", "REMOTE_ADDR"};
        String ip = request.getHeader("X-Forwarded-For");

        for (String header : headers) {
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader(header);
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if(ip.equals("0:0:0:0:0:0:0:1")){
            ip = "127.0.0.1";
        }

        return ip;
    }

    public static String getHashedClientIP(HttpServletRequest request) {
        String ip = getClientIP(request);
        return hashSHA256(ip);
    }

    private static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // byte 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 해시를 생성할 수 없습니다.", e);
        }
    }

}
