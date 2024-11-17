package com.iluwater.timeout.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for Product.
 * This class stores all details for a product.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class Product {

  private int productId;
  private String productName;
  private String productDescription;
  private double productPrice;

}
