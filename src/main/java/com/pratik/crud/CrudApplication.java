package com.pratik.crud;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	// SECURITY: every endpoint requires an administrator bearer token supplied through the
	// 'crud.api.admin-token' property (e.g. env CRUD_API_ADMIN_TOKEN). If it is not configured, all requests are denied.
	@Bean
	public FilterRegistrationBean<OncePerRequestFilter> apiTokenFilter(@Value("${crud.api.admin-token:}") String adminToken) {
		final byte[] expected = adminToken == null ? new byte[0] : adminToken.trim().getBytes(StandardCharsets.UTF_8);
		OncePerRequestFilter filter = new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws ServletException, IOException {
				if (expected.length == 0) {
					response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "API authentication is not configured");
					return;
				}
				String header = request.getHeader("Authorization");
				if (header == null || !header.startsWith("Bearer ")) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
				byte[] presented = header.substring(7).trim().getBytes(StandardCharsets.UTF_8);
				if (!MessageDigest.isEqual(expected, presented)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
				chain.doFilter(request, response);
			}
		};
		FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}

}

