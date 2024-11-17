package com.iluwater.timeout.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class TestService {

  @TimeLimiter(name = "ratingService")
  @Retry(name = "ratingService", fallbackMethod = "futureFallback")
  public CompletableFuture<String> slowService() throws InterruptedException {

    Thread.sleep(5000);
    return CompletableFuture.supplyAsync(() -> "success!");
  }

  private CompletableFuture<String> futureFallback() throws InterruptedException {
    return CompletableFuture.supplyAsync(()-> "fallback method reached");
  }
}
