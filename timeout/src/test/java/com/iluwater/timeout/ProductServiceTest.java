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

import com.iluwater.timeout.model.ProductDto;
import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.model.ReviewDto;
import com.iluwater.timeout.service.ProductService;
import com.iluwater.timeout.service.RatingService;
import com.iluwater.timeout.service.RatingServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductServiceTest {

  @Autowired
  ProductService productService;

  @Autowired
  RatingService ratingService;

  @Autowired
  RatingServiceClient ratingServiceClient;

  @Test
  void RetrieveProduct() throws Exception {

    int productId;
    ProductDto expectedResult;
    ProductDto actualResult;

    // Get productId: 1
    productId = 1;
    expectedResult = ProductDto.of(productId, "Blood On The Dance Floor", "A dance floor full of life & adventure", 12.45, ProductRatingDto.of(
        productId, 4.5, List.of(
            ReviewDto.of("John", "Smith", 5, "Excellent"),
            ReviewDto.of("Joanna", "Marshall", 4, "Ok...but not perfect")
        )));
    actualResult = productService.getProductDto(productId).toCompletableFuture().get();

    assertNotNull(actualResult);
    assertEquals(expectedResult, actualResult);

    // Get productId: 2
    productId = 2;
    expectedResult = ProductDto.of(productId, "The Eminem Show", "The show about Slim Shady", 12.12, ProductRatingDto.of(
        productId, 4.0, List.of(
            ReviewDto.of("Jane", "Doe", 5, "Best product ever!"),
            ReviewDto.of("Slim", "Shady", 3, "")
        )));
    actualResult = productService.getProductDto(productId).toCompletableFuture().get();

    assertNotNull(actualResult);
    assertEquals(expectedResult, actualResult);
  }
}
