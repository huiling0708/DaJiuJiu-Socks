package com.zbjct.dajiujiu.socks.basics.database.comment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Oracle 注释处理
 *
 */
@Component(JpaCommentHandle.COMMENT_HANDLE_BEAN_FLAG + "ORACLE")
public class OracleCommentHandle implements ICommentHandle {

    private final static String ALTER_TABLE_COMMENT_SQL = "COMMENT ON TABLE %s.%s IS '%s'";
    private final static String ALTER_COLUMN_COMMENT_SQL = "COMMENT ON COLUMN %s.%s.%s IS '%s'";


    private String schema;
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getSchemaSql() {
        return "select SYS_CONTEXT('USERENV','CURRENT_SCHEMA') CURRENT_SCHEMA from dual";
    }

    @Override
    public void alterTableSql(String tableName, String comment) {
        String sql = String.format(ALTER_TABLE_COMMENT_SQL, schema, tableName.toUpperCase(), comment);
        this.jdbcTemplate.update(sql);
    }

    @Override
    public void alterColumnComment(String tableName, String columnName, String comment) {
        String sql = String.format(ALTER_COLUMN_COMMENT_SQL, schema, tableName.toUpperCase(), columnName.toUpperCase(), comment);
        this.jdbcTemplate.update(sql);
    }

    @Override
    public void setSchema(String schema, JdbcTemplate jdbcTemplate) {
        this.schema = schema;
        this.jdbcTemplate = jdbcTemplate;
    }
}
