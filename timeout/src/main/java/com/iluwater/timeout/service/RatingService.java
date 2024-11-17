/*
 * This project is licensed under the MIT license. Module model-view-viewmodel is using ZK framework licensed under LGPL (see lgpl-3.0.txt).
 *
 * The MIT License
 * Copyright © 2014-2022 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwater.timeout.service;

import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.model.ReviewDto;
import io.github.resilience4j.micrometer.annotation.Timer;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * The Review service class.
 */
@Service
public class RatingService {

  private static final Logger log = LoggerFactory.getLogger(RatingService.class);
  private static final List<ProductRatingDto> ratings = new ArrayList<>();

  static {
    ProductRatingDto ratingDto1 = ProductRatingDto.of(1, 4.5,
        List.of(
            ReviewDto.of("John", "Smith", 5, "Excellent"),
            ReviewDto.of("Joanna", "Marshall", 4, "Ok...but not perfect")
        )
    );

    // Product 2
    ProductRatingDto ratingDto2 = ProductRatingDto.of(2, 4,
        List.of(
            ReviewDto.of("Jane", "Doe", 5, "Best product ever!"),
            ReviewDto.of("Slim", "Shady", 3, "")
        )
    );

    ratings.add(ratingDto1);
    ratings.add(ratingDto2);
  }

  /**
   * getRatingForProduct method.
   */
  @TimeLimiter(name = "ratingService", fallbackMethod = "getDefault")
  @Retry(name = "ratingService")
  public CompletableFuture<ProductRatingDto> getRatingForProduct(int productId) throws InterruptedException {
    Thread.sleep(3000);
    return CompletableFuture.supplyAsync(() -> getRating(productId));
  }

  private ProductRatingDto getRating(int productId) {
    return ratings.stream()
        .filter(r -> r.getProductId() == productId)
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find productId " + productId));
  }

  private CompletableFuture<ProductRatingDto> getDefault(int productId, Throwable throwable) {
    log.error("error", throwable);
    return CompletableFuture.supplyAsync(() -> ProductRatingDto.of(productId, 0, Collections.emptyList()));
  }
}
