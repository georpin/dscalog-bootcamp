package com.devsuperior.dscatalog.repository;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entity.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	private long existsId;
	private long notExistsId;
	private long countTotalProducts;


	@BeforeEach
	void setup() throws Exception {
		notExistsId = 10000L;
		existsId = 1L;
		countTotalProducts = 25L;
	}

	@Test
	public void deleteShouldDeleteObjctWhenExists() {
		productRepository.deleteById(existsId);
		Optional<Product> result = productRepository.findById(existsId);
		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			productRepository.deleteById(notExistsId);
		});
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		var product = Factory.createProduct();
		product.setId(null);
		product = productRepository.save(product);
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
		
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdNotExists() {
		Optional<Product> result = productRepository.findById(notExistsId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
		Optional<Product> result = productRepository.findById(existsId);
		Assertions.assertTrue(result.isPresent());
	}
	
}
