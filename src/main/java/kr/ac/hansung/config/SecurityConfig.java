package kr.ac.hansung.config;

import kr.ac.hansung.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/signup",
                                 "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 상품 등록 / 삭제 / 수정 은 ADMIN 전용
                .requestMatchers("/products/add",
                                 "/products/*/delete",
                                 "/products/*/edit").hasRole("ADMIN")   // ← 신규 추가
                .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                // 비밀번호 변경 등 그 외 모든 요청은 인증된 사용자만
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // 권한 부족(403) 시 접근 거부 안내 페이지로 이동
            .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
