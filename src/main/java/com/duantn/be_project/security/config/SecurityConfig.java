package com.duantn.be_project.security.config;

import java.util.Arrays;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

        @Value("${jwt.secretKey}")
        private String SignerKey;

        @Autowired
        customJwtDecoder customJwtDecoder;

        // phai dc tat truoc khi public Error *********************************
        private final String[] PUBLIC_SWAGGER = { "/swagger-ui/*", "/swagger-ui-custom.html", "/v3/api-docs/*",
                        "/api-docs/*", "/api-docs" };

        // Tự config post
        private final String[] PUBLIC_ENDPONIT = { "/form/login", "/user", "/loginByGoogle", "/api/*", "/form/refesh" };

        // Tự config get
        private final String[] Get_Public_endpoint = {
                        // category
                        "/category/**",
                        // banner
                        "/banners/**",
                        // image
                        "imageByProduct/{id}",
                        // product
                        "/home/product/list",
                        "/findMore/{name}",
                        "productPerMall/list",
                        "findMore/productPerMall/list",
                        "/showAllProduct/{slug}",
                        "/countBySlugProduct/{id}",
                        "/product/{slug}",
                        "/countOrderSuccess/{id}",
                        "/searchStore/{id}",
                        // productDetail
                        "/sidlerMinMax/{name}",
                        "/detailProduct",
                        "/detailProduct/{id}",
                        "/countDetailSoldOut/{id}",
                        "/findIdProductByIdProduct/{id}",
                        // Brand
                        "/brand/**",
                        // Voucher
                        "fillProductDetails/{idProduct}",
                        "fillVoucherPrice/{idProduct}",
                        "fillVoucherShop/{slug}",
                        // store
                        "/store/{id}",
                        "/store/checkIdUser/{id}",
                        "/store",
                        "/business/{taxcode}",
                        // warranties
                        "/warranties",
                        "/warranties/{id}",
                        "/CateProductInStore/{id}",
                        // Comments
                        "/comment/**",
                        //user
                        "/checkPass",

        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity.cors(); // Kích hoạt CORS trong Spring Security
                httpSecurity.csrf(AbstractHttpConfigurer::disable);
                httpSecurity.exceptionHandling(exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

                // Quy định quyền truy cập
                httpSecurity.authorizeHttpRequests(request -> request
                                .requestMatchers("/files/**").permitAll() // Cho phép truy cập vào các tệp trong thư mục
                                                                          // /files
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPONIT).permitAll()
                                .requestMatchers(PUBLIC_SWAGGER).permitAll()
                                .requestMatchers(HttpMethod.GET, Get_Public_endpoint).permitAll() // Cho phép các đường
                                                                                                  // dẫn công khai
                                .anyRequest().authenticated());

                // Cấu hình oauth2
                httpSecurity.oauth2ResourceServer(
                                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

                return httpSecurity.build();
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/files/**")
                                .addResourceLocations("file:" + System.getProperty("user.dir")
                                                + "/src/main/resources/static/files/");
        }

        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

                return jwtAuthenticationConverter;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Chỉ định nguồn hợp lệ
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Bao gồm
                                                                                                           // OPTIONS
                configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả các endpoint
                return source;
        }

        @Bean
        JwtDecoder jwtDecoder() {
                SecretKeySpec signingKey = new SecretKeySpec(SignerKey.getBytes(), "HS512");
                return NimbusJwtDecoder.withSecretKey(signingKey)
                                .macAlgorithm(MacAlgorithm.HS512)
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .components(new Components()
                                                .addSecuritySchemes("bearer-key",
                                                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer").bearerFormat("JWT")));
        }
}
