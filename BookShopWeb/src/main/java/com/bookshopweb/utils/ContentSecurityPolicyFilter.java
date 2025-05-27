package com.bookshopweb.utils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class ContentSecurityPolicyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) response;
            // Set header Content-Security-Policy
            httpResp.setHeader("Content-Security-Policy",
                    "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self';");
        }

        // Tiếp tục xử lý request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Dọn dẹp khi filter bị huỷ (thường để trống)
    }
}
