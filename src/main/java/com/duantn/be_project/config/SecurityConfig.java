package com.duantn.be_project.config;
// package com.duantn.be_project.Service;

// import java.util.Arrays;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig implements WebMvcConfigurer {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http.csrf().disable()
//                 .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                         .requestMatchers("/**").permitAll() // Cho phép truy cập không yêu cầu xác
//                         // thực đến tất cả các
//                         // endpoint
//                         .anyRequest().authenticated() // Các request còn lại yêu cầu xác thực
//                 )
//                 .cors(); // Kích hoạt CORS

//         return http.build();
//     }

//      @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         registry.addResourceHandler("/files/**")
//                 .addResourceLocations("file:" + System.getProperty("user.dir") + "/src/main/resources/static/files/");
//     }

//     // @Bean
//     // public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//     // http
//     // .csrf().disable()
//     // .cors() // Kích hoạt CORS
//     // .and()
//     // .authorizeRequests()
//     // .requestMatchers("/", "/public/**").permitAll()
//     // .requestMatchers("/loginByGoogle").authenticated()
//     // .anyRequest().authenticated()
//     // .and()
//     // .oauth2Login().defaultSuccessUrl("/loginByGoogle", true)
//     // .and()
//     // .logout().logoutSuccessUrl("/");

//     // return http.build();
//     // }

//     @Bean
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//         configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//         configuration.setAllowedHeaders(Arrays.asList("*"));
//         configuration.setAllowCredentials(true);
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration);
//         return source;
//     }
// }
