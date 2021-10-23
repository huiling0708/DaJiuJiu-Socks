package com.zbjct.dajiujiu.socks.basics.database;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * JPA注释服务 用于生成数据库表注释与列注释
 */
public interface JpaCommentService {
    /**
     * 执行 生成注释
     *
     * @throws SQLException
     */
    @Transactional
    void invoke() throws SQLException;
}
