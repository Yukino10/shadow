package com.shadow.web.config;

import com.shadow.web.model.core.PbInterceptor;
import com.shadow.web.model.core.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: wangzhendong
 * @Date: 2019/11/12 17:10
 * @Description:
 */
@Component
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    PermissionInterceptor permInterceptor;

    @Autowired
    PbInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permInterceptor).addPathPatterns("/**");
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }
}