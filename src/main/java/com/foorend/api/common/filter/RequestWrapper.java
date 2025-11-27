package com.foorend.api.common.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RequestWrapper - HTTP 요청 바디를 여러 번 읽을 수 있도록 래핑
 * 최초 요청 시 바디를 메모리에 저장하여, 이후 여러 번 InputStream 재사용 가능하게 함
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] bodyBytes;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.bodyBytes = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(new ByteArrayInputStream(bodyBytes));
    }

    public byte[] getBodyBytes() {
        return this.bodyBytes;
    }
}

/**
 * CachedBodyServletInputStream - ByteArrayInputStream을 ServletInputStream으로 래핑
 */
class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream inputStream;
    private boolean finished;

    public CachedBodyServletInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.finished = false;
    }

    @Override
    public int read() throws IOException {
        int data = inputStream.read();
        if (data == -1) {
            finished = true;
        }
        return data;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException("Async read not supported");
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }
}


