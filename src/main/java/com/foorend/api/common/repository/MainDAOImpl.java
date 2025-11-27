package com.foorend.api.common.repository;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

/**---------------------------------------------------------------------
 *  ■GenericDao 구현체 / mybatis의 SqlSession을 생성하여 구현함 ■ foodinko ■2018. 9. 28.
 ----------------------------------------------------------------------**/
@Repository("mainDB")
public class MainDAOImpl<T,R> implements GenericDAO<T,R> {
    private final SqlSession sqlSession;
    private final SqlSession sqlSessionBatch;

    public MainDAOImpl(@Qualifier("mainDBSqlSession") SqlSession sqlSession, @Qualifier("mainDBSqlSessionBatch") SqlSession sqlSessionBatch) {
        this.sqlSession      = sqlSession;
        this.sqlSessionBatch = sqlSessionBatch;
    }

    @Override
    public List<R> selectList(String sqlId) {
        return this.sqlSession.selectList(sqlId);
    }

    @Override
    public List<R> selectList(String sqlId, T param) { return this.sqlSession.selectList(sqlId, param); }

    @Override
    public R selectOne(String sqlId) {
        return this.sqlSession.selectOne(sqlId);
    }

    @Override
    public R selectOne(String sqlId, T param) {
        return this.sqlSession.selectOne(sqlId, param);
    }

    @Override
    public int insert(String sqlId) {
        return this.sqlSession.insert(sqlId);
    }

    @Override
    public int insert(String sqlId, T param) {
        return this.sqlSession.insert(sqlId, param);
    }

    @Override
    public int update(String sqlId) {
        return this.sqlSession.update(sqlId);
    }

    @Override
    public int update(String sqlId, T param) {
        return this.sqlSession.update(sqlId, param);
    }

    @Override
    public int delete(String sqlId) {
        return this.sqlSession.delete(sqlId);
    }

    @Override
    public int delete(String sqlId, T param) {
        return this.sqlSession.delete(sqlId, param);
    }

    @Override
    public void select(String sqlId, T params, @SuppressWarnings("rawtypes") ResultHandler objHandler) { this.sqlSession.select(sqlId, params, objHandler); }

    @Override
    public R batchSelectOne(String sqlId, T param) {
        return this.sqlSessionBatch.selectOne(sqlId, param);
    }

    @Override
    public List<R> batchSelectList(String sqlId, T param) { return this.sqlSessionBatch.selectList(sqlId, param); }

    @Override
    public int batchDelete(String sqlId, T param) {
        return this.sqlSessionBatch.delete(sqlId, param);
    }

    @Override
    public int batchUpdate(String sqlId, T param) {
        return this.sqlSessionBatch.update(sqlId, param);
    }

    @Override
    public int batchInsert(String sqlId, T param) {
        return this.sqlSessionBatch.insert(sqlId, param);
    }

    @Override
    public void batchFlushStatements() {
        this.sqlSessionBatch.flushStatements();
    }
}
