package com.shadow.web.config;

import com.shadow.web.api.authority.JwtAuthenticationTokenFilter;
import com.shadow.web.service.authority.JwtAuthenticationEntryPoint;
import com.shadow.web.utils.Constants;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    Environment env;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Resource
    private UserDetailsService userDetailsService;

    public WebSecurityConfig() {
        super(true);
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Value("${jwt.route.authentication.pre-auth}")
    private String preAuthUrl;
    @Value("${jwt.route.authentication.path}")
    private String authUrl;
    @Value("${jwt.route.authentication.phone-login}")
    private String phoneLogin;
    @Value("${jwt.route.authentication.send-code}")
    private String sendCode;
    @Value("${jwt.route.authentication.register}")
    private String register;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers(
                "/*",
                "/admin",
                "/*.html",
                "/**/*.png",
                "/**/*.jpg",
                "/**/*.ico",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
                preAuthUrl,
                authUrl,
                phoneLogin,
                sendCode,
                register,
                "/admin/user/valid/*",
                "/admin/authorities",
                "/admin/product/package/**",
                "/admin/test/file"
        );
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        String environment = env.getProperty("spring.profiles.active");
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and();

        if (Constants.SPRING_PROFILE_PRODUCT.equals(environment) || (null != environment)) {
            httpSecurity.authorizeRequests().anyRequest().authenticated();
        }
        //httpSecurity.authorizeRequests().antMatchers("/test/**").permitAll().anyRequest().authenticated();


        httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        // disable page caching
        httpSecurity.headers().cacheControl();
    }

}
