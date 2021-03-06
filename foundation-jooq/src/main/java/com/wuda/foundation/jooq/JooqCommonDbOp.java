package com.wuda.foundation.jooq;

import org.jooq.*;
import org.jooq.types.ULong;

import javax.sql.DataSource;
import java.util.function.Consumer;

/**
 * 封装一些基本的数据库操作.
 *
 * @author wuda
 * @since 1.0.0
 */
public interface JooqCommonDbOp {

    /**
     * 如果记录不存在则insert,如果存在则更新.
     *
     * @param dataSource               datasource
     * @param table                    操作的表
     * @param insertIntoSelectFields   如果记录不存在,则使用这些字段新增一条记录,即insert into ...select fields语法的select fields.
     * @param existsRecordSelector     用于查询准备更新的记录,只能查询出一条,如果查询出多条记录会抛出异常
     * @param existsRecordUpdateAction 如果记录存在,则用于更新selector查询出的那条记录
     * @param idColumn                 该记录的id列,用于返回记录的ID
     * @param <R>                      被操作的记录的类型
     * @return 新增或者是更新的记录的ID
     */
    default <R extends Record> long insertOrUpdate(DataSource dataSource,
                                                   Table<R> table,
                                                   SelectSelectStep<R> insertIntoSelectFields,
                                                   SelectConditionStep<R> existsRecordSelector,
                                                   Consumer<R> existsRecordUpdateAction,
                                                   TableField<R, ULong> idColumn) {
        Long id = insertIfNotExists(dataSource, table, insertIntoSelectFields, existsRecordSelector, idColumn);
        if (id == null) {
            R affectedRecord = existsRecordSelector.fetchOne();
            existsRecordUpdateAction.accept(affectedRecord);
            id = affectedRecord.get(idColumn).longValue();
        }
        return id;
    }

    /**
     * 执行insert into ... select ... from where not exists语句.
     *
     * @param dataSource             datasource
     * @param table                  操作的表
     * @param existsRecordSelector   用于查询已经存在的记录,只能查询出一条,如果查询出多条记录会抛出异常
     * @param insertIntoSelectFields 如果记录不存在,则使用这些字段新增一条记录,即insert into ...select fields语法的select fields.
     * @param <R>                    被操作的记录的类型
     * @return 如果已经存在, 则返回<code>null</code>; 否则返回新增记录的ID
     */
    default <R extends Record> Long insertIfNotExists(DataSource dataSource,
                                                      Table<R> table,
                                                      SelectSelectStep<R> insertIntoSelectFields,
                                                      SelectConditionStep<R> existsRecordSelector,
                                                      TableField<R, ULong> idColumn) {

        DSLContext dslContext = JooqContext.getOrCreateDSLContext(dataSource);
        InsertOnDuplicateStep<R> insertSetStep = dslContext.insertInto(table)
                .select(
                        insertIntoSelectFields
                                // .from(table) from dual
                                .whereNotExists(
                                        existsRecordSelector
                                )
                );
        InsertResultStep<R> insertResultStep = insertSetStep.returning(idColumn);
        R r = insertResultStep.fetchOne();
        if (r != null) {
            return r.get(idColumn).longValue();
        }
        return null;
    }

}
