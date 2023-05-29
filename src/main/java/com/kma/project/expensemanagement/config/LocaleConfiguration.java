package com.kma.project.expensemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class LocaleConfiguration implements WebMvcConfigurer {

    public LocaleConfiguration() {
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));
    }

//    @Bean
//    public MessageSource messageSource() {
//        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.setBasename("classpath:messages");
//        messageSource.setDefaultEncoding("UTF-8");
//        return messageSource;
//    }


//    @Bean
//    public LocaleResolver localeResolver() {
//        AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
//        acceptHeaderLocaleResolver.setDefaultLocale(Locale.);
//        return acceptHeaderLocaleResolver;
//    }
}
