package me.enferas.shortner.config;

import java.util.List;
import me.enferas.entities.ShortnerEntities;
import me.enferas.entities.ShortnerUtil;
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.site.SitePreferenceWebArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;  
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;  
  
@Configuration
@ComponentScan("me.enferas")
@EnableWebMvc
public class Config extends WebMvcConfigurerAdapter {  
      
    @Bean  
    public UrlBasedViewResolver setupViewResolver() {  
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();  
        resolver.setPrefix("/WEB-INF/jsp/");  
        resolver.setSuffix(".jsp");  
        resolver.setViewClass(JstlView.class);  
        return resolver;  
    }

    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHanderInterceptor() {
        return new DeviceResolverHandlerInterceptor();
    }
    
    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
        return new DeviceHandlerMethodArgumentResolver();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registery) {
        registery.addInterceptor(deviceResolverHanderInterceptor());
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/*");
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(
                deviceHandlerMethodArgumentResolver()
        );
        
        argumentResolvers.add(
                new ServletWebArgumentResolverAdapter(new DeviceWebArgumentResolver())
        );
        
        argumentResolvers.add(
                new ServletWebArgumentResolverAdapter(new SitePreferenceWebArgumentResolver())
        );
    }
}  