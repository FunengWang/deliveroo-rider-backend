package com.deliveroo.rider.configuration;

import com.deliveroo.rider.converter.DayOfWeekConverter;
import com.deliveroo.rider.converter.MonthConverter;
import com.deliveroo.rider.converter.WorkingTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new MonthConverter());
        registry.addConverter(new WorkingTypeConverter());
        registry.addConverter(new DayOfWeekConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
