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
package com.iluwater.timeout.controller;

import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.service.RatingService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing product ratings.
 * <p>
 * This controller handles HTTP requests related to product ratings and delegates
 * the business logic to the {@link RatingService}.
 * </p>
 */
@RestController
@RequestMapping(value = "/ratings")
@Slf4j
@SpringBootConfiguration
public class RatingController {

  private final RatingService ratingService;

  @Autowired
  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  /**
   * Get Product Rating REST API.
   */
  @GetMapping("{productId}")
  public CompletableFuture<ResponseEntity<ProductRatingDto>> getRating(
      @PathVariable int productId,
      @RequestParam(value = "forceTimeout", required = false, defaultValue = "false") boolean forceTimeout)
      throws InterruptedException {

    if (forceTimeout) {
      Thread.sleep(10000);
    }
    return this.ratingService.getRatingForProduct(productId)
        .thenApply(productRating -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(productRating));
  }

  /**
    Exception handler when productId is not found.
   */
  @ExceptionHandler(ResponseStatusException.class)
  public CompletionStage<ResponseEntity<Map<String, String>>> handleResponseStatusException(ResponseStatusException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", ex.getReason());
    return CompletableFuture.supplyAsync(() -> ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
  }
}
