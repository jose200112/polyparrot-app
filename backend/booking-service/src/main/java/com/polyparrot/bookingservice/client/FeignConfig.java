package com.polyparrot.bookingservice.client;

import org.springframework.beans.factory.annotation.Value;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignConfig implements RequestInterceptor {

 @Value("${internal.secret}")
 private String internalSecret;

 @Override
 public void apply(RequestTemplate template) {
     template.header("X-Internal-Secret", internalSecret);
 }
}
