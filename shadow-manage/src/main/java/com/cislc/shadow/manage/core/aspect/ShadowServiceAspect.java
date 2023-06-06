package com.cislc.shadow.manage.core.aspect;

import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import com.cislc.shadow.manage.core.shadow.ShadowUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ShadowServiceAspect
 * @Description 影子服务切面
 * @Date 2019/7/16 13:06
 * @author szh
 **/
@Component
@Slf4j
@Aspect
public class ShadowServiceAspect {

    /**
     * @Description 影子服务完成后释放信号量
     * @param point 连接点
     * @author szh
     * @Date 2019/7/16 18:05
     */
    @After("@annotation(com.cislc.shadow.manage.core.aspect.ShadowService)")
    public void dealShadowService(JoinPoint point) {
        String threadName = Thread.currentThread().getName();
        List<String> topics = ShadowFactory.getThreadTopic(threadName);
        if (topics != null){
            for (String topic: topics){
                ShadowUtils.releaseSemaphore(topic);
            }
            Map<String, ArrayList<String>> map =  ShadowFactory.getThreadMap();
            map.remove(threadName);
        }

    }

    /**
     * @Description 影子服务抛出异常进行回滚
     * @param point 连接点
     * @param e 异常
     * @author szh
     * @Date 2019/7/16 18:06
     */
    @AfterThrowing(value = "@annotation(com.cislc.shadow.manage.core.aspect.ShadowService)", throwing = "e")
    public void dealServiceException(JoinPoint point, Exception e) throws Throwable {
        String threadName = Thread.currentThread().getName();
        List<String> topics = ShadowFactory.getThreadTopic(threadName);
        if (topics != null) {
            for (String topic : topics) {
                ShadowUtils.revert(topic);
            }
        }
        throw e;
    }

}
