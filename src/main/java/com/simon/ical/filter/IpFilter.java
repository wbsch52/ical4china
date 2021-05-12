package com.simon.ical.filter;

import com.simon.ical.commons.IpHolder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Component
public class IpFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip)) {
            ip = ip.split(",")[0];
        }

        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        try {
            IpHolder.set(ip);
            doFilter(request, response, filterChain);
        } finally {
            IpHolder.remove();
        }
    }
}
