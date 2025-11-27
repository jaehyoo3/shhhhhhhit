package com.foorend.api.common.repository;

import org.apache.ibatis.session.ResultHandler;

import java.util.List;

/**
 * 공통 MyBatis DAO 인터페이스
 * - Mapper 인터페이스 없이 XML만으로 쿼리 실행
 *
 * @param <T> 파라미터 타입
 * @param <R> 리턴 타입
 */
public interface GenericDAO<T, R> {

    // ==================== SELECT ====================

    /**
     * 다건 조회
     */
    List<R> selectList(String sqlId);

    List<R> selectList(String sqlId, T param);

    /**
     * 단건 조회
     */
    R selectOne(String sqlId);

    R selectOne(String sqlId, T param);

    /**
     * ResultHandler를 이용한 조회
     */
    void select(String sqlId, T params, @SuppressWarnings("rawtypes") ResultHandler objHandler);

    // ==================== INSERT ====================

    int insert(String sqlId);

    int insert(String sqlId, T param);

    // ==================== UPDATE ====================

    int update(String sqlId);

    int update(String sqlId, T param);

    // ==================== DELETE ====================

    int delete(String sqlId);

    int delete(String sqlId, T param);

    // ==================== BATCH ====================

    R batchSelectOne(String sqlId, T param);

    List<R> batchSelectList(String sqlId, T param);

    int batchInsert(String sqlId, T param);

    int batchUpdate(String sqlId, T param);

    int batchDelete(String sqlId, T param);

    void batchFlushStatements();
}
