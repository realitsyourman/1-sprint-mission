package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 모든 웹 리소스 요청을 처리하는 핸들러를 등록합니다.
        // '/**'는 모든 경로의 요청을 의미합니다.
        registry.addResourceHandler("/**")
                // static 폴더에서 리소스를 찾도록 설정합니다.
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        // 요청된 리소스가 실제로 존재하면 해당 리소스를 반환합니다.
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        // 요청된 리소스가 없는 경우 index.html을 반환합니다.
                        // 이는 Vue Router를 사용할 때 필요한 설정입니다.
                        // 브라우저에서 직접 /about 같은 경로로 접근해도
                        // Vue 애플리케이션이 적절히 라우팅할 수 있게 됩니다.
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}