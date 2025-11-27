package com.foorend.api.common.util;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**-------------------------------------------------------------
 # FileName : RestCommUtil.java
 # Author   : foodinko
 # Desc     : Restful 통신 유틸
 # Date     : 2020-03-18
 /**-----------------------------------------------------------**/
@Component
public class RestCommUtil {
    private final RestTemplate restTemplate;

    public RestCommUtil(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        restTemplate.setInterceptors(interceptors);
    }

    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return restTemplate.getInterceptors();
    }

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        restTemplate.setRequestFactory(requestFactory);
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return restTemplate.getRequestFactory();
    }

    @Override
    public String toString() {
        return restTemplate.toString();
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        restTemplate.setMessageConverters(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return restTemplate.getMessageConverters();
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        restTemplate.setErrorHandler(errorHandler);
    }

    public ResponseErrorHandler getErrorHandler() {
        return restTemplate.getErrorHandler();
    }

    public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
        restTemplate.setDefaultUriVariables(defaultUriVariables);
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        restTemplate.setUriTemplateHandler(handler);
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return restTemplate.getUriTemplateHandler();
    }

    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        return restTemplate.getForObject(url, responseType);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        return restTemplate.getForEntity(url, responseType);
    }

    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        return restTemplate.headForHeaders(url, uriVariables);
    }

    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.headForHeaders(url, uriVariables);
    }

    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return restTemplate.headForHeaders(url);
    }

    public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
        return restTemplate.postForLocation(url, request, uriVariables);
    }

    public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForLocation(url, request, uriVariables);
    }

    public URI postForLocation(URI url, Object request) throws RestClientException {
        return restTemplate.postForLocation(url, request);
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType);
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        restTemplate.put(url, request, uriVariables);
    }

    public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        restTemplate.put(url, request, uriVariables);
    }

    public void put(URI url, Object request) throws RestClientException {
        restTemplate.put(url, request);
    }

    public void delete(String url, Object... uriVariables) throws RestClientException {
        restTemplate.delete(url, uriVariables);
    }

    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        restTemplate.delete(url, uriVariables);
    }

    public void delete(URI url) throws RestClientException {
        restTemplate.delete(url);
    }

    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        return restTemplate.optionsForAllow(url, uriVariables);
    }

    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.optionsForAllow(url, uriVariables);
    }

    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        return restTemplate.optionsForAllow(url);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
            throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
                                          Object... uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
                                          Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
            throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return restTemplate.exchange(requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        return restTemplate.exchange(requestEntity, responseType);
    }

    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... uriVariables)
            throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables)
            throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor);
    }
}
