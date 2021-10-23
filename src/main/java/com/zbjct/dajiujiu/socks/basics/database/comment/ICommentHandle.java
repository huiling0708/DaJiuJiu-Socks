package com.zbjct.dajiujiu.socks.basics.database.comment;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 注释处理
 */
public interface ICommentHandle {
    String getSchemaSql();

    void alterTableSql(String tableName, String comment);

    void alterColumnComment(String tableName, String columnName, String comment);

    void setSchema(String schema, JdbcTemplate jdbcTemplate);
}
