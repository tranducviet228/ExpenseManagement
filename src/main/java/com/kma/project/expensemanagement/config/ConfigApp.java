package com.kma.project.expensemanagement.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigApp {

    @Value(value = "${viet.app.cloud_name}")
    private String cloudName;

    @Value(value = "${viet.app.api_key}")
    private String apiKey;

    @Value(value = "${viet.app.api_secret}")
    private String apiScret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiScret);
        return new Cloudinary(config);
    }

    @Bean
    public FilterRegistrationBean<ReferrerPolicyFilter> referrerPolicyFilterRegistrationBean() {
        FilterRegistrationBean<ReferrerPolicyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReferrerPolicyFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
