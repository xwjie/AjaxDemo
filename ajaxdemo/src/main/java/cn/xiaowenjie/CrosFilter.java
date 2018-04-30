package cn.xiaowenjie;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.cfg.context.CrossParameterConstraintMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

public class CrosFilter implements Filter {

	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		httpResponse.addHeader("Access-Control-Allow-Methods", "*");
		httpResponse.addHeader("Access-Control-Allow-Credentials", "true");

		// 来源
		String origin = httpRequest.getHeader("Origin");

		if (origin != null) {
			httpResponse.addHeader("Access-Control-Allow-Origin", origin);
		}

		// 自定义头
		String headers = httpRequest.getHeader("Access-Control-Request-Headers");

		if (headers != null) {
			httpResponse.addHeader("Access-Control-Allow-Headers", headers);
		}

		if (httpRequest.getMethod().equals(HttpMethod.OPTIONS)) {
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
