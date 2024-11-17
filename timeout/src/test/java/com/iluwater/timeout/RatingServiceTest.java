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
package com.iluwater.timeout;

import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.model.ReviewDto;
import com.iluwater.timeout.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RatingServiceTest {

  @Autowired
  public RatingService ratingService;

  @Test
  void testGetRatingsForProduct() throws ExecutionException, InterruptedException {

    CompletionStage<ProductRatingDto> productRating;

    // Verify product reviews from 1st product
    productRating = ratingService.getRatingForProduct(1);
    ProductRatingDto productRatingDto1 = productRating.toCompletableFuture().join();

    assertNotNull(productRatingDto1, "Verify productRating(s) for product 1 are not null");
    assertEquals(4.5, productRatingDto1.getAverageRating());
    List<ReviewDto> product1Reviews = productRatingDto1.getReviews();

    // Verify 1st individual shopper review from 1st product
    assertEquals("John", product1Reviews.get(0).getUserFirstName());
    assertEquals("Smith", product1Reviews.get(0).getUserLastName());
    assertEquals(5, product1Reviews.get(0).getRating());
    assertEquals("Excellent", product1Reviews.get(0).getComment());

    // Verify 2nd individual shopper review from 1st product
    assertEquals("Joanna", product1Reviews.get(1).getUserFirstName());
    assertEquals("Marshall", product1Reviews.get(1).getUserLastName());
    assertEquals(4, product1Reviews.get(1).getRating());
    assertEquals("Ok...but not perfect", product1Reviews.get(1).getComment());

    // Verify product reviews from 2nd product
    productRating = ratingService.getRatingForProduct(2);
    ProductRatingDto productRatingDto2 = productRating.toCompletableFuture().get();

    assertNotNull(productRatingDto2, "Verify productRating(s) for product 2 are not null");
    assertEquals(4, productRatingDto2.getAverageRating());
    List<ReviewDto> product2Reviews = productRatingDto2.getReviews();

    // Verify 1st individual shopper review from 2nd product
    assertEquals("Jane", product2Reviews.get(0).getUserFirstName());
    assertEquals("Doe", product2Reviews.get(0).getUserLastName());
    assertEquals(5, product2Reviews.get(0).getRating());
    assertEquals("Best product ever!", product2Reviews.get(0).getComment());

    // Verify 2nd individual shopper review from 2nd product
    assertEquals("Slim", product2Reviews.get(1).getUserFirstName());
    assertEquals("Shady", product2Reviews.get(1).getUserLastName());
    assertEquals(3, product2Reviews.get(1).getRating());
    assertEquals("", product2Reviews.get(1).getComment());

  }

  @Test
  void testGetRatingsForNonExistingProduct() throws InterruptedException {
    final int productId = 123;

    CompletionStage<ProductRatingDto> productRating = ratingService.getRatingForProduct(productId);

    // Use assertThrows to check for ExecutionException
    ExecutionException executionException = assertThrows(
        ExecutionException.class,
        () -> productRating.toCompletableFuture().get() // Trigger the asynchronous call
    );

    // Unwrap the actual exception
    Throwable cause = executionException.getCause();

    // Verify the actual cause is ResponseStatusException
    assertTrue(cause instanceof ResponseStatusException);
    ResponseStatusException exception = (ResponseStatusException) cause;

    // Verify the HTTP status is 404 NOT_FOUND
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    // Verify the exception message contains the expected error message
    assertEquals("Could not find productId " + productId, exception.getReason());
  }

}
