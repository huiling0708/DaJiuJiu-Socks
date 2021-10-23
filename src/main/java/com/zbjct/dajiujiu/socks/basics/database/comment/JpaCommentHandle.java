package com.zbjct.dajiujiu.socks.basics.database.comment;

import com.zbjct.dajiujiu.socks.basics.database.JpaCommentService;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import com.zbjct.dajiujiu.socks.basics.helper.Check;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import com.zbjct.dajiujiu.socks.basics.utils.SpringContextUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JPA 注释处理
 */
@Service
@Slf4j
public class JpaCommentHandle implements JpaCommentService {

    public final static String COMMENT_HANDLE_BEAN_FLAG = "COMMENT_HANDLE_BEAN_";

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private DataSource dataSource;

    @Override
    @Transactional
    public void invoke() throws SQLException {
        log.info("===============开始更新数据库表注释=================");
        ICommentHandle handleBean = this.getHandleBean();
        Map<String, NameCommentBean> tables = this.loadTable();
        tables.entrySet().forEach(map -> {
            NameCommentBean value = map.getValue();
            handleBean.alterTableSql(value.name, value.comment);
            log.info("表[{}]: {}", value.name, value.comment);
            value.columns.forEach(c -> {
                handleBean.alterColumnComment(value.name, c.name, c.comment);
                log.info("---字段[{}]:{}", c.name, c.comment);
            });
        });
        log.info("===============数据库表注释更新结束=================");
    }

    private ICommentHandle getHandleBean() throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String databaseType = metaData.getDatabaseProductName().toUpperCase();
        //获取数据库类型，读取相应的bean处理
        String beanName = COMMENT_HANDLE_BEAN_FLAG + databaseType;
        ICommentHandle bean = SpringContextUtils.getBean(beanName, ICommentHandle.class);
        //获取数据库sql
        String schemaSql = bean.getSchemaSql();
        String schema = jdbcTemplate.queryForObject(schemaSql, String.class);
        bean.setSchema(schema, jdbcTemplate);
        return bean;
    }

    private Map<String, NameCommentBean> loadTable() {
        //从实体工厂中获取所有实体
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        SessionFactoryImpl sessionFactory = (SessionFactoryImpl) entityManagerFactory.unwrap(SessionFactory.class);
        Map<String, EntityPersister> entityMap = sessionFactory.getMetamodel().entityPersisters();
        Map<String, NameCommentBean> tables = new HashMap<>();
        //处理
        entityMap.entrySet().forEach(x -> {
            SingleTableEntityPersister ep = (SingleTableEntityPersister) x.getValue();
            if (tables.containsKey(ep.getTableName())) {
                return;
            }
            NameCommentBean bean = this.buildTableBean(ep);
            tables.put(ep.getTableName(), bean);
        });
        return tables;
    }

    private NameCommentBean buildTableBean(SingleTableEntityPersister ep) {
        Class entityClass = ep.getMappedClass();//目标类
        ApiModel ann = AnnotationUtils.getAnnotation(entityClass, ApiModel.class);
        Check.notNull(ann, String.format("[%s]缺少@ApiModel注解", entityClass.getSimpleName()));
        NameCommentBean bean = new NameCommentBean(ep.getTableName(), ann.description());
        List<NameCommentBean> childList = new ArrayList<>(50);

        //处理表字段
        List<Field> allFields = ClassUtils.
                getAllFields(entityClass, Column.class, true);
        allFields.forEach(x -> {
            ApiModelProperty annColumn = AnnotationUtils.getAnnotation(x, ApiModelProperty.class);
            Check.notNull(annColumn,
                    String.format("[%s]类中[%s]字段缺少@ApiModelProperty注解", entityClass.getSimpleName(), x.getName()));
            String columnComment = annColumn.value();
            //处理数据字典
            if (IDict.class.isAssignableFrom(x.getType())) {
                Class<? extends Enum> dict = (Class<? extends Enum>) x.getType();
                Enum[] enumConstants = dict.getEnumConstants();
                StringBuilder sub = null;
                for (Enum constant : enumConstants) {
                    IDict d = (IDict) constant;
                    if (sub == null) {
                        sub = new StringBuilder("  [");
                    } else {
                        sub.append(",");
                    }
                    sub.append(d.getDescribe());
                    sub.append(" - ");
                    sub.append(constant.name());
                }
                sub.append("]");
                columnComment += sub.toString();

            }
            String[] propertyColumnNames = ep.getPropertyColumnNames(x.getName());
            String columnName = "-";
            for (String key : propertyColumnNames) {
                if (columnName.equals(key)) {
                    continue;
                }
                columnName = key;
                childList.add(new NameCommentBean(columnName, columnComment));
            }
            bean.columns = childList;
        });
        return bean;
    }


    @ToString
    private class NameCommentBean {
        private String name;
        private String comment;
        private List<NameCommentBean> columns;

        public NameCommentBean(String name, String comment) {
            this.name = name;
            this.comment = comment;
        }
    }
}
