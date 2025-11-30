package com.foorend.api.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foorend.api.common.constants.GlobalConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * 공용 Utils
 */
@Slf4j
public class CommonUtil {

    /**
     * E-mail 주소의 유효성을 검사
     */
    public static Boolean isEmailAddress(String email) {
        return email != null && email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * 010으로 시작하는 전화번호의 유효성을 검사
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (!org.springframework.util.StringUtils.hasText(phoneNumber)) {
            return false;
        }
        String regex = "^010[0-9]{8}$";
        return phoneNumber.matches(regex);
    }

    /**
     * 알림톡 발송용 010으로 시작하는 전화번호를 정상화
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        if (digitsOnly.matches("^010\\d{8}$")) {
            return digitsOnly;
        }
        return null;
    }

    /**
     * 검색 타입 확인 (1:단어, 2:자음 또는 초성)
     */
    public static int getSearchType(String text) {
        if (text.matches("^[ㄱ-ㅎ]*$")) {
            return 2;
        } else if (text.matches("^[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣\\s]*$")) {
            if (text.matches(".*[ㄱ-ㅎㅏ-ㅣ]+.*")) {
                return -1;
            }
            return 1;
        }
        return 0;
    }

    /**
     * Object의 빈 값, Null Check
     */
    public static boolean isNullOrEmpty(Object value) {
        if (value == null) {
            return true;
        }
        // Java 16+ 부터 표준인 instanceof 패턴 매칭 사용
        if (value instanceof String s) {
            return s.trim().isEmpty();
        }
        if (value instanceof Map<?, ?> m) {
            return m.isEmpty();
        }
        if (value instanceof List<?> l) {
            return l.isEmpty();
        }
        if (value instanceof Object[] arr) {
            return arr.length == 0;
        }

        return false;
    }

    /**
     * 문자열이 Json 형식인지 확인
     */
    public static boolean isJsonType(String jsonData) {
        JSONParser jsonParser = new JSONParser();
        try {
            jsonParser.parse(jsonData);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 문자열을 Json Object로 변환
     */
    public static JSONObject stringToJson(String jsonData) {
        JSONParser jsonParser = new JSONParser();
        try {
            return (JSONObject) jsonParser.parse(jsonData);
        } catch (ParseException e) {
            return new JSONObject();
        }
    }

    /**
     * 랜덤 문자열 생성
     */
    public static String getRandomString(Integer stringLength) {
        StringBuilder sb = new StringBuilder();
        Random rn = new Random();
        char[] characters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

        for (int i = 0; i < stringLength; i++) {
            sb.append(characters[rn.nextInt(characters.length)]);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Request IP 주소 조회
     */
    public static String getIPAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        log.debug("IP Address: {}", ip);
        return ip;
    }

    /**
     * UUID 생성
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * SHA256 해싱
     */
    public static String getSHA256Hash(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(value.getBytes());
            byte[] bytData = messageDigest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : bytData) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    /**
     * 입력값이 날짜 형식인지 확인
     */
    public static boolean isLegalDateType(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Request Body 읽기
     */
    public static String getRequestBody(HttpServletRequest httpServletRequest) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();

        String buffer;
        while ((buffer = bufferedReader.readLine()) != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

    /**
     * HttpRequest 헤더의 특정 문자 추출
     */
    public static String getExtract(String header, String headerPrefix) {
        if (StringUtils.isBlank(header)) {
            return GlobalConstants.API_AUTHORIZATION_500_MSG;
        }
        if (header.length() < headerPrefix.length()) {
            return GlobalConstants.API_AUTHORIZATION_501_MSG;
        }
        if (headerPrefix.equals(header.substring(0, headerPrefix.length() - 1))) {
            return GlobalConstants.API_AUTHORIZATION_502_MSG;
        }
        return header.substring(headerPrefix.length()).trim();
    }

    /**
     * Timestamp 조회
     */
    public static long getTimestamp() {
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        return utcTime.toEpochSecond() * 1000;
    }

    /**
     * 날짜 포맷 Enum
     */
    public enum DateFormat {
        YYYYMMDD,
        YYYYMM,
        YYYY,
        YYYYMMDDHH24MISS,
        YYYYMMDDHH24MISS_NOSPACE,
        HH24MISS,
        YYYYMMDDHH24MISSMS,
        yyyy_MM_dd_HH_mm_ss_SSS,
        YYYY_MM_DD
    }

    /**
     * 날짜 변환
     */
    public static String convertDate(DateFormat format, Date date) {
        String pattern = switch (format) {
            case YYYYMMDDHH24MISS -> "yyyyMMdd HH:mm:ss";
            case YYYYMMDDHH24MISS_NOSPACE -> "yyyyMMddHHmmss";
            case HH24MISS -> "HH:mm:ss";
            case YYYYMM -> "yyyyMM";
            case YYYY -> "yyyy";
            case YYYYMMDDHH24MISSMS -> "yyyyMMddHHmmssSSS";
            case yyyy_MM_dd_HH_mm_ss_SSS -> "yyyy-MM-dd HH:mm:ss:SSS";
            case YYYY_MM_DD -> "yyyy-MM-dd";
            default -> "yyyyMMdd";
        };
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 현재시간 기본
     */
    public static String getCurrentDate() {
        return convertDate(DateFormat.yyyy_MM_dd_HH_mm_ss_SSS, new Date());
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Object를 JSON 문자열로 변환
     */
    public static String convertToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        // 이미 String이면 그대로 반환
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열을 Object로 변환
     */
    public static <T> T convertFromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패: " + e.getMessage(), e);
        }
    }
}
