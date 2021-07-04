package com.devsuperior.dscatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.service.ProductService;
import com.devsuperior.dscatalog.service.exception.DatabaseException;
import com.devsuperior.dscatalog.service.exception.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private PageImpl<ProductDTO> page;
	
	private ProductDTO productDTO;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;

	@BeforeEach
	void setup() throws Exception {
		
		existingId = 1L;
		nonExistingId = 100L;
		dependentId = 4L;
		
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(service.findById(existingId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.update(eq(existingId), any())).thenReturn(productDTO);
		Mockito.when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
		
		Mockito.when(service.insert(any())).thenReturn(productDTO);
			
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
//		Pode ser feito em uma unica chamada exemplo abaixo:
//		mockMvc.perform(get("/products")).andExpect(status().isOk());
		
//		Exemplo atribuindo para um ResultActions
		ResultActions result =
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {

		ResultActions result =
				mockMvc.perform(get("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoudWhenIdDoesNotExist() throws Exception {
		
		ResultActions result =
				mockMvc.perform(get("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
				
		result.andExpect(status().isNotFound());

	}
	
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result =
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateByIdShouldReturnNotFoudWhenIdDoesNotExist() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
			
		result.andExpect(status().isNotFound());		
	}
	
	@Test
	public void insertShouldReturnProductDTOAndId() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = 
				mockMvc.perform(post("/products")
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());		
	}
	
	@Test
	public void deleteByIdShouldReturnNoContentWhenIdExistis() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
			
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteByIdShouldReturnNoTFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
			
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteByIdShouldReturnNoTFoundWhenExistsDependentId() throws Exception {
		ResultActions result = 
				mockMvc.perform(delete("/products/{id}", dependentId)
						.accept(MediaType.APPLICATION_JSON));
			
		result.andExpect(status().isBadRequest());
	}

}
