package com.clab.common.security.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.clab.common.security.CustomAuthenticationEntryPoint;
import com.clab.common.security.CustomUserDetailService;
import com.clab.common.security.filter.JWTVerificationFilter;
import com.clab.common.security.filter.SecurityExceptionHandlingFilter;

@Configuration
public class APISecurityConfig {
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
			@Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig,
			CustomUserDetailService userDetailsService,
			JWTVerificationFilter jwtVerifyFilter, SecurityExceptionHandlingFilter exceptionFilter,
			CustomAuthenticationEntryPoint authEntryPoint) throws Exception {
		http.securityMatcher("/**").userDetailsService(userDetailsService)
				.cors(cors -> cors.configurationSource(corsConfig))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable());

		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/member/join", "/auth/refresh", "/auth/login").permitAll()
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
						.anyRequest().authenticated());
		http.exceptionHandling(ex -> ex
				.authenticationEntryPoint(authEntryPoint));
		http.addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(exceptionFilter, JWTVerificationFilter.class);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
