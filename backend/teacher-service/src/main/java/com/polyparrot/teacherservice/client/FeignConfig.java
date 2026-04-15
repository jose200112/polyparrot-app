package com.polyparrot.teacherservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
