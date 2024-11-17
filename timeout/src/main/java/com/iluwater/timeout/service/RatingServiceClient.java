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
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service client for Reviews.
 */
@Service
public class RatingServiceClient {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${rating.service.endpoint}")
  private String ratingService;

  /**
   * Retrieves the {@link ProductRatingDto} for a given product asynchronously.
   * <p>
   * This method is annotated with {@code @TimeLimiter} to ensure that if the
   * request to the rating service takes too long, it will fall back to the
   * {@code getDefault} method.
   * </p>
   *
   * @return a {@link CompletionStage} that will complete with the {@link ProductRatingDto}
   *                          for the specified product
   * @throws RuntimeException if the rating service is unavailable or if there is an error
   *                          in processing the request
   */
  @TimeLimiter(name = "ratingService", fallbackMethod = "getDefault")
  public CompletionStage<ProductRatingDto> getProductRatingDto(int productId) {

    String url = this.ratingService + productId;
    Supplier<ProductRatingDto> supplier = () ->
        this.restTemplate.getForEntity(url, ProductRatingDto.class)
            .getBody();
    return CompletableFuture.supplyAsync((supplier));
  }
  
  public CompletionStage<ProductRatingDto> getDefault(int productId) {
    return CompletableFuture.supplyAsync(() -> ProductRatingDto.of(productId, 0, Collections.emptyList()));
  }
}
