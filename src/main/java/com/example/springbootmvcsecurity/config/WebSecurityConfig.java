package com.example.springbootmvcsecurity.config;

import com.example.springbootmvcsecurity.service.AuthUserDetailsService;
import com.example.springbootmvcsecurity.utils.Constants;
import com.example.springbootmvcsecurity.web.LoggingAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        "/",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/webjars/**").permitAll()
                .antMatchers(
                        "/members/**").access("hasRole('" + Constants.MEMBER_ROLE + "') or hasRole('" + Constants.ADMIN_ROLE + "')")
                .antMatchers(
                        "/admin/**").hasRole(Constants.ADMIN_ROLE)
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String userName = "admin1";
        String password = "1234";
        String hashedPassword = passwordEncoder().encode(password);
        auth.inMemoryAuthentication().withUser(userName).password(hashedPassword).roles(Constants.ADMIN_ROLE);
        auth.userDetailsService(authUserDetailsService);
    }
}
