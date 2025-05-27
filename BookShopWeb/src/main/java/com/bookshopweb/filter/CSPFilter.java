package com.bookshopweb.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Annotation để tự động đăng ký filter mà không cần cấu hình trong web.xml
@WebFilter("/*")
public class CSPFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) response;
            httpResp.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data:; " +
                            "font-src 'self'; " +
                            "connect-src 'self';");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

