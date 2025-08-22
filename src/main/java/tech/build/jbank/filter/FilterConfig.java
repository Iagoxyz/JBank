package tech.build.jbank.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final IpFilter ipFilter;

    public FilterConfig(IpFilter ipFilter) {
        this.ipFilter = ipFilter;
    }

    @Bean
    public FilterRegistrationBean<IpFilter> filterFilterRegistrationBean() {
        var registration = new FilterRegistrationBean<IpFilter>();

        registration.setFilter(ipFilter);
        registration.setOrder(0);

        return registration;
    }
}
