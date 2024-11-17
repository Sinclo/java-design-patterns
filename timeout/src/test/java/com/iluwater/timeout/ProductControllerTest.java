package com.iluwater.timeout;

import com.iluwater.timeout.controller.ProductController;
import com.iluwater.timeout.model.ProductDto;
import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.model.ReviewDto;
import com.iluwater.timeout.service.ProductService;
import com.iluwater.timeout.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private ProductController productController;

//  @MockBean
//  private RatingService ratingService;

  @BeforeEach
  void SetUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.productController).build();
  }

  @Test
  void testGetProductResponse() throws Exception {

    final int productId = 1;

    ProductDto expectedProductResponse = ProductDto.of(productId, "Blood On The Dance Floor", "A dance floor full of life & adventure", 12.45, ProductRatingDto.of(
        productId, 4.5, List.of(
            ReviewDto.of("John", "Smith", 5, "Excellent"),
            ReviewDto.of("Joanna", "Marshall", 4, "Ok...but not perfect")
        )));

    // Performing the request to the controller
    mockMvc.perform(asyncDispatch(
            mockMvc.perform(get("/product/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(expectedProductResponse.getProductId()))
        .andExpect(jsonPath("$.productName").value(expectedProductResponse.getProductName()))
        .andExpect(jsonPath("$.productDescription").value(expectedProductResponse.getProductDescription()))
        .andExpect(jsonPath("$.productPrice").value(expectedProductResponse.getProductPrice()))
        .andExpect(jsonPath("$.productRating.productId").value(expectedProductResponse.getProductRating().getProductId()))
        .andExpect(jsonPath("$.productRating.averageRating").value(expectedProductResponse.getProductRating().getAverageRating()))
        .andExpect(jsonPath("$.productRating.reviews[0].userFirstName").value(expectedProductResponse.getProductRating().getReviews().get(0).getUserFirstName()))
        .andExpect(jsonPath("$.productRating.reviews[0].userLastName").value(expectedProductResponse.getProductRating().getReviews().get(0).getUserLastName()))
        .andExpect(jsonPath("$.productRating.reviews[0].rating").value(expectedProductResponse.getProductRating().getReviews().get(0).getRating()))
        .andExpect(jsonPath("$.productRating.reviews[0].comment").value(expectedProductResponse.getProductRating().getReviews().get(0).getComment()))
        .andExpect(jsonPath("$.productRating.reviews[1].userFirstName").value(expectedProductResponse.getProductRating().getReviews().get(1).getUserFirstName()))
        .andExpect(jsonPath("$.productRating.reviews[1].userLastName").value(expectedProductResponse.getProductRating().getReviews().get(1).getUserLastName()))
        .andExpect(jsonPath("$.productRating.reviews[1].rating").value(expectedProductResponse.getProductRating().getReviews().get(1).getRating()))
        .andExpect(jsonPath("$.productRating.reviews[1].comment").value(expectedProductResponse.getProductRating().getReviews().get(1).getComment()));
  }

  @Test
  void testGetProductResponseWithRatingServiceTimeout() throws Exception {

    final int productId = 1;

//    when(ratingService.getRatingForProduct(productId))
//        .thenReturn(CompletableFuture.completedFuture(ProductRatingDto.of(productId, 0, Collections.emptyList())));

//    when(ratingService.getRatingForProduct(productId)).thenThrow(TimeoutException.class);

    ProductDto expectedProductResponse = ProductDto.of(productId, "Blood On The Dance Floor", "A dance floor full of life & adventure", 12.45, ProductRatingDto.of(
        productId, 0.0, List.of()));

    // Performing the request to the controller
    mockMvc.perform(asyncDispatch(
            mockMvc.perform(get("/product/{productId}", productId)
                .queryParam("forceTimeout", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(expectedProductResponse.getProductId()))
        .andExpect(jsonPath("$.productName").value(expectedProductResponse.getProductName()))
        .andExpect(jsonPath("$.productDescription").value(expectedProductResponse.getProductDescription()))
        .andExpect(jsonPath("$.productPrice").value(expectedProductResponse.getProductPrice()))
        .andExpect(jsonPath("$.productRating.productId").value(expectedProductResponse.getProductRating().getProductId()))
        .andExpect(jsonPath("$.productRating.averageRating").value(expectedProductResponse.getProductRating().getAverageRating()))
        .andExpect(jsonPath("$.productRating.reviews").isArray())
        .andExpect(jsonPath("$.productRating.reviews").isEmpty());
  }
}
