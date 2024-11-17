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

import com.iluwater.timeout.model.Product;
import com.iluwater.timeout.model.ProductDto;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Product service class.
 */
@Service
public class ProductService {
  private Map<Integer, Product> map;

  @Autowired
  private RatingServiceClient ratingServiceClient;

  @Autowired
  private RatingService ratingService;

  /**
   * Initializer.
   */
  @PostConstruct
  public void init() {
    this.map = Map.of(
        1, Product.of(1, "Blood On The Dance Floor", "A dance floor full of life & adventure", 12.45),
        2, Product.of(2, "The Eminem Show", "The show about Slim Shady", 12.12)
    );
  }

  /**
   * A method intended to return the product details response.
   * @param productId Unique identifier for a product
   * @return Product details
   */
  public CompletionStage<ProductDto> getProductDto(int productId) throws InterruptedException {
    return this.ratingService.getRatingForProduct(productId).thenApply(productRatingDto -> {
      Product product = this.map.get(productId);
      return ProductDto.of(product.getProductId(), product.getProductName(), product.getProductDescription(), product.getProductPrice(), productRatingDto);
    });
  }
}
