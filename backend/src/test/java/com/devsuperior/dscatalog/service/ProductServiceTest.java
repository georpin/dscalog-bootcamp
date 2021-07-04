package com.devsuperior.dscatalog.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entity.Category;
import com.devsuperior.dscatalog.entity.Product;
import com.devsuperior.dscatalog.repository.CategoryRepository;
import com.devsuperior.dscatalog.repository.ProductRepository;
import com.devsuperior.dscatalog.service.exception.DatabaseException;
import com.devsuperior.dscatalog.service.exception.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private PageImpl<Product> page;
	private ProductDTO productDTO;
	private Category category;

	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(categoryRepository.getById(ArgumentMatchers.any())).thenReturn(category);
		Mockito.when(repository.getById(ArgumentMatchers.any())).thenReturn(product);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).getById(nonExistingId);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.of(product));
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistingId);
				
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void updateByIdShoulResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});

		Mockito.verify(repository, Mockito.times(1)).getById(nonExistingId);
	}
	
	@Test
	public void updateByIdShouldReturnProdutoDTOWhenIdExists() {

		ProductDTO result = service.update(existingId, productDTO);

		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).getById(existingId);
	}
	
	@Test
	public void findByIdShouldResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(existingId);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}

	@Test
	public void findAlPagedShoudReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

}
