package com.example.clubsite.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CachingLogFilter implements Filter {// request-response 로그 출력

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        requestWrapper.setCharacterEncoding("utf-8");
        responseWrapper.setCharacterEncoding("utf-8");
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(requestWrapper, responseWrapper);
            long end = System.currentTimeMillis();
            log.info("\n\n" +
                            "[REQUEST] {} - {} {} - DelayTime {}\n" +
                            "Headers : {}\n" +
                            "Request : {}\n" +
                            "Response : {}\n",
                    ((HttpServletRequest) request).getMethod(),
                    ((HttpServletRequest) request).getRequestURI(),
                    responseWrapper.getStatus(),
                    (end - start) / 1000.0,
                    getHeaders((HttpServletRequest) request),
                    getRequestBody(requestWrapper),
                    (responseWrapper.getContentSize() < 10000) ?
                            getResponseBody(responseWrapper)
                            : getResponseBody(responseWrapper).substring(0, 999)
            );
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            responseWrapper.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("\n\n" +
                            "[UNHANDLED EXCEPTION] {} - {} {} - DelayTime {}\n" +
                            "Headers : {}\n" +
                            "Request : {}\n" +
                            "Error Span : {}\n",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    responseWrapper.getStatus(),
                    (end - start) / 1000.0,
                    getHeaders(requestWrapper),
                    getRequestBody(requestWrapper),
                    e.getMessage(),
                    e.getCause()
            );
        }
    }

    @Override
    public void destroy() {

    }

    private Map getHeaders(HttpServletRequest request) {
        HashMap<String, String> headerMap = new HashMap<>();

        Enumeration headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = (String) headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse();
            }
        }
        return null == payload ? " - " : payload;
    }
}

