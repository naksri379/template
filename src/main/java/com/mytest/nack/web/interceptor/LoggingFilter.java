package com.mytest.nack.web.interceptor;

        import com.google.common.base.Strings;
        import lombok.extern.slf4j.Slf4j;
        import org.slf4j.MDC;
        import org.springframework.http.HttpMethod;
        import org.springframework.http.HttpStatus;
        import org.springframework.stereotype.Component;
        import org.springframework.web.util.ContentCachingRequestWrapper;
        import org.springframework.web.util.ContentCachingResponseWrapper;
        import org.springframework.web.util.WebUtils;

        import javax.servlet.*;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter implements Filter {
    public static final String REQUEST_ID_HEADER_KEY = "X-Request-Id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";
    public static final String SERVICE_NAME_MDC_KEY = "serviceName";

    @SuppressWarnings("CPD-START")
    public static String getRequestData(final HttpServletRequest request) throws UnsupportedEncodingException {
        String payload = null;
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
            }
        }
        return payload == null ? "" : newlineSignRemover(payload);
    }

    @SuppressWarnings("CPD-END")
    private static String getResponseData(final HttpServletResponse response) throws IOException {
        String payload = null;
        if (HttpStatus.OK.value() != response.getStatus()) {
            ContentCachingResponseWrapper wrapper =
                    WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                }
            }
        }
        return payload == null ? "" : newlineSignRemover(payload);
    }

    private static String newlineSignRemover(String input) {
        return input.replace("\n", "").replace("\r", "");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LoggingFilter: init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        // get request id
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER_KEY);
        if (Strings.isNullOrEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(REQUEST_ID_MDC_KEY, requestId);

        log.info("Begin Processing {}", requestId);

        HttpServletRequest wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper((HttpServletResponse) response);
        wrappedResponse.addHeader(REQUEST_ID_HEADER_KEY, requestId);

        chain.doFilter(wrappedRequest, wrappedResponse);

        // log request
        StringBuilder builder = new StringBuilder("Request ")
                .append(wrappedRequest.getMethod())
                .append(" ")
                .append(wrappedRequest.getRequestURI())
                .append(" ")
                .append(wrappedRequest.getQueryString() == null ? "" : wrappedRequest.getQueryString());
        log.info(builder.toString());
        if (wrappedRequest.getMethod().equals(HttpMethod.POST.toString())) {
            builder.append(" ").append(getRequestData(wrappedRequest));
        }

        // log response
        String logString = new StringBuilder("Response ")
                .append(wrappedResponse.getStatus())
                .append(" ")
                .append(getResponseData(wrappedResponse))
                .toString();
        log.info(logString);

        writeResponse(wrappedResponse);

        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Processing time " + elapsedTime + " ms");

        MDC.remove(REQUEST_ID_MDC_KEY);
        MDC.remove(SERVICE_NAME_MDC_KEY);
    }

    private void writeResponse(final ContentCachingResponseWrapper wrappedResponse) throws IOException {
        wrappedResponse.copyBodyToResponse();
    }

    @Override
    public void destroy() {
        log.info("LoggingFilter: destroy");
    }
}
