/**
 * Created by chenkuan
 * on 16/6/20.
 */
package com.gigold.pay.autotest.filter;

import com.gigold.pay.autotest.util.Constant;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String[] allowUrls;

    /**
     * @return the allowUrls
     */
    public String[] getAllowUrls() {
        return allowUrls;
    }

    /**
     * @param allowUrls
     *            the allowUrls to set
     */
    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }

    /**
     * Default constructor.
     */
    public SessionFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean flag = false;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUrl = httpRequest.getRequestURI().replace(httpRequest.getContextPath(), "");
        if (null != allowUrls && allowUrls.length >= 1)
            for (String url : allowUrls) {
                if (requestUrl.contains(url)) {
                    flag = true;
                }
            }
        if (flag) {
            chain.doFilter(request, response);
        } else {
            HttpSession session = httpRequest.getSession();
            if (session.getAttribute(Constant.LOGIN_KEY) == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/new/index.html");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        String urls = fConfig.getInitParameter("allowUrlStr");
        this.allowUrls = urls.split(",");
    }
}
