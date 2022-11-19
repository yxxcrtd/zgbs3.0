package com.joyintech.tams.systemtools.util;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.tams.common.base.mapper.BaseBatchMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Hanyu
 */
public class TamsDbUtil {
    private TamsDbUtil() {
    }

    /**
     * 最大提交条数
     */
    public static final int MAX_COUNT = 1000;
    /**
     * 新增
     */
    protected static final String I = "I";
    /**
     * 新增
     */
    protected static final String IS = "IS";
    /**
     * 删除
     */
    protected static final String D = "D";
    /**
     * 删除后新增
     */
    protected static final String DIS = "DIS";
    /**
     * 修改
     */
    protected static final String U = "U";
    /**
     * 修改
     */
    protected static final String US = "US";

    /**
     * 批量插入（有回调）
     *
     * @param template
     * @param mapperClz
     * @param list
     * @param consumer
     */
    public static void insertBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Consumer consumer) {
        executeDataBatch(template, mapperClz, list, consumer, null, I);
    }

    /**
     * 批量插入（无回调）
     *
     * @param template
     * @param mapperClz
     * @param list
     */
    public static void insertSelectiveBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list) {
        executeDataBatch(template, mapperClz, list, null, null, IS);
    }

    /**
     * 批量插入（有回调）
     *
     * @param template
     * @param mapperClz
     * @param list
     */
    public static void insertSelectiveBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Consumer consumer) {
        executeDataBatch(template, mapperClz, list, consumer, null, IS);
    }

    /**
     * 先删除后插入（无回调）
     *
     * @param template
     * @param mapperClz
     * @param list
     * @param getPk
     */
    public static void deleteAndInsertSelectiveBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Function getPk) {
        if (getPk == null) {
            throw new BusinessException("删除需提供主键！");
        }
        executeDataBatch(template, mapperClz, list, null, getPk, DIS);
    }

    /**
     * 先删除后插入（有回调）
     *
     * @param template
     * @param mapperClz
     * @param list
     * @param getPk
     */
    public static void deleteAndInsertSelectiveBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Consumer consumer, Function getPk) {
        if (getPk == null) {
            throw new BusinessException("删除需提供主键！");
        }
        executeDataBatch(template, mapperClz, list, consumer, getPk, DIS);
    }

    /**
     * 批量删除
     *
     * @param template
     * @param mapperClz
     * @param list
     * @param getPk
     */
    public static void deleteBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Function getPk) {
        if (getPk == null) {
            throw new BusinessException("删除需提供主键！");
        }
        executeDataBatch(template, mapperClz, list, null, getPk, D);
    }

    /**
     * 批量更新
     *
     * @param template
     * @param mapperClz
     * @param list
     */
    public static void updateBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list) {
        executeDataBatch(template, mapperClz, list, null, null, U);
    }

    /**
     * 批量更新
     *
     * @param template
     * @param mapperClz
     * @param list
     */
    public static void updateSelectiveBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list) {
        executeDataBatch(template, mapperClz, list, null, null, US);
    }

    private static void executeDataBatch(SqlSessionTemplate template, Class<? extends BaseBatchMapper> mapperClz, List list, Consumer consumer, Function getPk, String type) {
        SqlSession session = template.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            BaseBatchMapper mapper = session.getMapper(mapperClz);
            int count = 0;
            if (ObjectUtils.isEmpty(list)) {
                return;
            }
            for (Object entity : list) {
                if (entity == null) {
                    continue;
                }
                if (consumer != null) {
                    consumer.accept(entity);
                }
                if (I.equals(type)) {
                    mapper.insert(entity);
                } else if (IS.equals(type)) {
                    mapper.insertSelective(entity);
                } else if (D.equals(type)) {
                    mapper.deleteByPrimaryKey(getPk.apply(entity));
                } else if (DIS.equals(type)) {
                    mapper.deleteByPrimaryKey(getPk.apply(entity));
                    mapper.insertSelective(entity);
                } else if (U.equals(type)) {
                    mapper.updateByPrimaryKey(entity);
                } else if (US.equals(type)) {
                    mapper.updateByPrimaryKeySelective(entity);
                }
                count++;
                if (count % MAX_COUNT == 0) {
                    session.commit();
                    session.clearCache();
                }
            }
            session.commit();
            session.clearCache();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
