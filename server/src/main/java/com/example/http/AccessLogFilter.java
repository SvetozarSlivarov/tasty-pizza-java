package com.example.http;

import com.sun.net.httpserver.*;
import java.io.IOException;

public class AccessLogFilter extends Filter {
    @Override public void doFilter(HttpExchange ex, Chain chain) throws IOException {
        long t = System.nanoTime();
        String m = ex.getRequestMethod();
        String p = ex.getRequestURI().toString();
        try {
            chain.doFilter(ex);
        } finally {
            long ms=(System.nanoTime()-t)/1_000_000;
            System.out.printf("%s %s (%d ms)%n", m, p, ms);
        }
    }
    @Override public String description() {
        return "access log";
    }
}
