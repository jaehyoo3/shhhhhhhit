package com.foorend.api.common.util;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * JDBC ResultSet을 JPA-like Entity 클래스로 매핑하는 유틸리티
 * @author foodinko
 * @since 2025-08-05
 */
@Slf4j
public class MapperUtil {

    // 클래스별 컬럼-필드 매핑 캐시
    private static final Map<Class<?>, Map<String, Field>> MAPPED_FIELDS = new HashMap<>();

    /**
     * ResultSet을 특정 클래스 타입으로 매핑
     * @param rs JDBC ResultSet
     * @param outputClass 변환 대상 클래스
     * @param <T> 클래스 타입
     * @return 매핑된 객체 리스트
     * @throws SQLException SQL 처리 중 예외
     */
    public static <T> List<T> mapResultSetToObject(ResultSet rs, Class<T> outputClass) throws SQLException {
        if (rs == null || !rs.isBeforeFirst()) {
            return Collections.emptyList();
        }

        if (!outputClass.isAnnotationPresent(Entity.class)) {
            log.warn("Class {} is not annotated with @Entity — proceeding anyway.", outputClass.getSimpleName());
        }

        List<T> outputList = new ArrayList<>();
        Map<String, Field> columnToFieldMap = getColumnToFieldMapping(outputClass, rs.getMetaData());

        try {
            while (rs.next()) {
                T bean = outputClass.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                    Field field = entry.getValue();
                    field.setAccessible(true);
                    Object columnValue = rs.getObject(entry.getKey());
                    if (columnValue != null) {
                        field.set(bean, columnValue);
                    }
                }
                outputList.add(bean);
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("객체 매핑 중 오류 발생: " + outputClass.getSimpleName(), e);
        }

        return outputList;
    }

    /**
     * 클래스의 필드와 ResultSet의 컬럼명을 매핑
     * @param outputClass 대상 클래스
     * @param rsmd ResultSet 메타데이터
     * @return 컬럼명-필드 맵핑
     * @throws SQLException SQL 예외
     */
    private static Map<String, Field> getColumnToFieldMapping(Class<?> outputClass, ResultSetMetaData rsmd) throws SQLException {
        if (MAPPED_FIELDS.containsKey(outputClass)) {
            return MAPPED_FIELDS.get(outputClass);
        }

        Map<String, Field> columnToFieldMap = new HashMap<>();
        Field[] fields = outputClass.getDeclaredFields();

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i).trim();

            for (Field field : fields) {
                String mappedColumn = getMappedColumnName(field);
                if (mappedColumn.equalsIgnoreCase(columnName)) {
                    columnToFieldMap.put(columnName, field);
                    break;
                }
            }
        }

        MAPPED_FIELDS.put(outputClass, columnToFieldMap);
        return columnToFieldMap;
    }

    /**
     * 필드에 설정된 @Column 이름 또는 필드명 반환
     * @param field 대상 필드
     * @return 매핑될 컬럼명
     */
    private static String getMappedColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!column.name().trim().isEmpty()) {
                return column.name().trim();
            }
        }
        return field.getName();
    }
}
