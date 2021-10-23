package com.zbjct.dajiujiu.socks.basics.database.comment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * mysql 注释处理
 *
 */
@Component(JpaCommentHandle.COMMENT_HANDLE_BEAN_FLAG + "MYSQL")
public class MySqlCommentHandle implements ICommentHandle {

    private final static String ALTER_TABLE_COMMENT_SQL = " ALTER TABLE `%s`.`%s` COMMENT = ?";
    private final static String GET_COLUMN_ALTER_COMMENT_SQL = " SELECT CONCAT('ALTER TABLE `',a.TABLE_SCHEMA,'`.`',a.TABLE_NAME,'` MODIFY COLUMN `',a.COLUMN_NAME,'` ',a.COLUMN_TYPE,\n" +
            " (CASE WHEN a.IS_NULLABLE = 'NO' THEN ' NOT NULL ' ELSE\t'' END), \n" +
            " (CASE WHEN a.COLUMN_DEFAULT IS NOT NULL THEN CONCAT(' DEFAULT ''',a.COLUMN_DEFAULT,''' ') ELSE\t'' END) ,' COMMENT ?') ALTER_SQL\n" +
            "FROM information_schema.`COLUMNS` a\n" +
            "WHERE a.TABLE_SCHEMA = ? \n" +
            "AND a.TABLE_NAME = ?\n" +
            "AND a.COLUMN_NAME = ? ";

    private String schema;
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getSchemaSql() {
        return "select database() from dual";
    }

    @Override
    public void alterTableSql(String tableName, String comment) {
        String sql = String.format(ALTER_TABLE_COMMENT_SQL, schema, tableName);
        this.jdbcTemplate.update(sql,comment);
    }

    @Override
    public void alterColumnComment(String tableName, String columnName, String comment) {
        String alterColumnCommentSql = jdbcTemplate.queryForObject(GET_COLUMN_ALTER_COMMENT_SQL, String.class, schema, tableName, columnName);
        if (StringUtils.isNotBlank(alterColumnCommentSql)) {
            jdbcTemplate.update(alterColumnCommentSql, comment);
        }
    }

    @Override
    public void setSchema(String schema, JdbcTemplate jdbcTemplate) {
        this.schema = schema;
        this.jdbcTemplate = jdbcTemplate;
    }
}
