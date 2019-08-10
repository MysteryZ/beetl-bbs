package com.ibeetl.bbs.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeetl.bbs.common.WebUtils;
import com.ibeetl.bbs.model.BbsUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebMvcConfig implements WebMvcConfigurer {

    WebUtils webUtils;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        return mapper;
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                     Object handler) throws Exception {

                String requestURI = request.getServletPath();
                if (webUtils.currentUser() == null) {
                    //未登陆用户，记录访问地址，登陆后可以直接跳转到此页面
                    if (!requestURI.contains("/bbs/user/login.html")) {
                        request.getSession(true).setAttribute("lastAccess", requestURI);
                    }
                }
                if (requestURI.contains("/bbs/admin/") || requestURI.contains("/bbs/topic/add")) {
                    BbsUser user = webUtils.currentUser();
                    if (user == null) {
                        response.sendRedirect(request.getContextPath() + "/user/loginPage.html");
                        return false;
                    }
                }
                return true;
            }
        }).addPathPatterns("/bbs/**");
    }

    /**
     * 跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://ibeetl.com", "http://www.ibeetl.com")
                .allowedMethods("*");

    }
}
