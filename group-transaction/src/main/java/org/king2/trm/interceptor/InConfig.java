package org.king2.trm.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * =======================================================
 * 说明:
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/13:26          创建
 * =======================================================
 */
@Configuration
public class InConfig implements WebMvcConfigurer {

    @Bean
    public GroupIdInterceptor groupIdInterceptor() {
        return new GroupIdInterceptor ();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor (groupIdInterceptor ()).addPathPatterns ("/**");
    }
}
