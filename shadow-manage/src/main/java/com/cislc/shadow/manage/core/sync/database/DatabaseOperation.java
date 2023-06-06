package com.cislc.shadow.manage.core.sync.database;

import com.cislc.shadow.manage.common.utils.BeanUtils;
import com.cislc.shadow.manage.common.utils.DatabaseUtils;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName DatabaseOperation
 * @Description 数据库实体操作
 * @Date 2019/6/27 16:12
 * @author szh
 **/
@Slf4j
@Component
public class DatabaseOperation {

    /**
     * @Description 保存实体到数据库
     * @param entity 实体
     * @author szh
     * @Date 2019/6/27 22:26
     */
    @Async("asyncServiceExecutor")
    public void saveEntity(ShadowEntity entity) {
        // 获取repository名
        String repositoryName = DatabaseUtils.generateRepositoryName(entity.getClass().getSimpleName());
        if (Character.isUpperCase(repositoryName.charAt(0))) {
            repositoryName = Character.toLowerCase(repositoryName.charAt(0)) + repositoryName.substring(1);
        }
        try {
            // 从ioc容器中取出
            Object repository = BeanUtils.getBean(repositoryName);
            if (null != repository) {
                // 调用save方法
                Method saveMethod = repository.getClass().getMethod("save", Object.class);
                saveMethod.invoke(repository, entity);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("save to database error: {}", e.getMessage(), e);
        }
    }

}
