package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entity.Category;
import com.devsuperior.dscatalog.entity.Product;

public class Factory {
	
	public static Product createProduct() {
		var product = new Product (1L, "Iphone", "Iphone muito caro", 4170.0, 
			"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/25-big.jpg",
			Instant.parse("2020-07-14T10:00:00Z"));
		product.getCategories().add(new Category(2L, "Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		var product = createProduct(); 
		return new ProductDTO(product, product.getCategories());
	}

}
