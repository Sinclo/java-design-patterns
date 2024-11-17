package com.iluwater.timeout;

import com.iluwater.timeout.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TestServiceTest {

  @Autowired
  TestService testService;

  @Test
  void test() throws InterruptedException, ExecutionException {

    String expectedResponse = "fallback method reached";
    String actualResponse = testService.slowService().toCompletableFuture().get();
    assertEquals(expectedResponse, actualResponse);
  }
}
