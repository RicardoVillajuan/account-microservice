package com.bank.webclient;

import org.springframework.web.reactive.function.client.WebClient;
import com.bank.model.Customer;
import com.bank.model.Product;
import reactor.core.publisher.Mono;

public class repoWebClient {

	
	private WebClient.Builder webclient=WebClient.builder();
	
	
	public Mono<Customer> getCustomer(String id){
		return webclient.build()
					.get()
					.uri("http://localhost:8094/customer/{id}",id)
					.retrieve()				
					.bodyToMono(Customer.class);
	}
	
	public Mono<Product> getProduct(String idproduct){
		return webclient.build()
					.get()
					.uri("http://localhost:8093/product/{id}",idproduct)
					.retrieve()
					.bodyToMono(Product.class);
	}
	
}
