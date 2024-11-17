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

import com.iluwater.timeout.controller.RatingController;
import com.iluwater.timeout.model.ProductRatingDto;
import com.iluwater.timeout.model.ReviewDto;
import com.iluwater.timeout.service.RatingService;

import io.github.resilience4j.micrometer.annotation.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RatingControllerTest {

  private MockMvc mockMvc;

  @Autowired
  public RatingController ratingController;

  @BeforeEach
  void SetUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(this.ratingController).build();
  }

  @Test
  void testGetRatingAPIResponse() throws Exception {

    final int productId = 1;

    ProductRatingDto expectedRating = ProductRatingDto.of(productId, 4.5,
        List.of(
            ReviewDto.of("John", "Smith", 5, "Excellent"),
            ReviewDto.of("Joanna", "Marshall", 4, "Ok...but not perfect")
        ));

    // Performing the request to the controller
    mockMvc.perform(asyncDispatch(
        mockMvc.perform(get("/ratings/{productId}", productId)
            .accept(MediaType.APPLICATION_JSON))
            .andReturn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(expectedRating.getProductId()))
        .andExpect(jsonPath("$.averageRating").value(expectedRating.getAverageRating()))
        .andExpect(jsonPath("$.reviews[0].userFirstName").value(expectedRating.getReviews().get(0).getUserFirstName()))
        .andExpect(jsonPath("$.reviews[0].userLastName").value(expectedRating.getReviews().get(0).getUserLastName()))
        .andExpect(jsonPath("$.reviews[0].rating").value(expectedRating.getReviews().get(0).getRating()))
        .andExpect(jsonPath("$.reviews[0].comment").value(expectedRating.getReviews().get(0).getComment()))
        .andExpect(jsonPath("$.reviews[1].userFirstName").value(expectedRating.getReviews().get(1).getUserFirstName()))
        .andExpect(jsonPath("$.reviews[1].userLastName").value(expectedRating.getReviews().get(1).getUserLastName()))
        .andExpect(jsonPath("$.reviews[1].rating").value(expectedRating.getReviews().get(1).getRating()))
        .andExpect(jsonPath("$.reviews[1].comment").value(expectedRating.getReviews().get(1).getComment()));
  }

  @Test
  void testGetRatingAPINotFoundResponse() throws Exception {

    final int productId = 123;
    final HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;
    final String expectedReason = "Could not find productId " + productId;

    // Performing the request to the controller
    mockMvc.perform(get("/ratings/{productId}", productId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value(expectedReason));
  }

  @Test
  void testGetRatingAPITimeoutResponse() throws Exception {

    final int productId = 1;

    ProductRatingDto expectedRating = ProductRatingDto.of(productId, 0.0, List.of());

    // Performing the request to the controller
    mockMvc.perform(asyncDispatch(
            mockMvc.perform(get("/ratings/{productId}", productId)
                .queryParam("forceTimeout", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(expectedRating.getProductId()))
        .andExpect(jsonPath("$.averageRating").value(expectedRating.getAverageRating()))
        .andExpect(jsonPath("$.reviews").isArray())
        .andExpect(jsonPath("$.reviews").isEmpty());
  }
}
