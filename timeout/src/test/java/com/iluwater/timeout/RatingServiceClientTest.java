package com.iluwater.timeout;

import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.service.RatingService;
import com.iluwater.timeout.service.RatingServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class RatingServiceClientTest {

  @Autowired
  RatingServiceClient ratingServiceClient;

  @Test
  void getProductRating() throws Exception {

    CompletionStage<ProductRatingDto> productRating;

    final int productId = 1;
    productRating = ratingServiceClient.getProductRatingDto(productId);
    assertNotNull(productRating, "Verify productRating(s) for product 1 are not null");
    assertEquals("", productRating, "");
  }

  @Test
  void getProductRatingDefaultResponse() throws Exception {

    ProductRatingDto productRating;

    final int productId = 123;
    ratingServiceClient.getProductRatingDto(productId);
  }
}
