package com.bank.servicedb;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.entity.Account;
import com.bank.model.Enterprise;
import com.bank.model.Natural;
import com.bank.model.Product;
import com.bank.repository.AccountRepository;
import com.bank.service.IAccountService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AccountServiceDb implements IAccountService{

	private final AccountRepository repoAccount;
	
	WebClient objCustomer=WebClient.builder().baseUrl("http://localhost:8094").build();
	WebClient objProduct=WebClient.builder().baseUrl("http://localhost:8093").build();
	
	@Override
	public Mono<Account> save(Account account) {
		// TODO Auto-generated method stub
		return repoAccount.save(account);
	}

	@Override
	public Mono<Account> findByIdClient(String idclient) {
		// TODO Auto-generated method stub
		return repoAccount.findByIdclient(idclient);
	}

	@Override
	public Flux<Account> findByAll() {
		// TODO Auto-generated method stub
		return repoAccount.findAll();
	}
	
	@Override
	public Mono<Account> findById(String idclient) {
		// TODO Auto-generated method stub
		return repoAccount.findById(idclient);
	}

	@Override
	public Mono<Void> deleteById(String id) {
		// TODO Auto-generated method stub
		return repoAccount.deleteById(id);
	}

	
	
	//Metodos que obtienen registros de otras entidades
	@Override
	public Mono<Product> findByUrlIdProduct(String id) {
		// TODO Auto-generated method stub
		Mono<Product> product=objProduct.get().uri("/product/{id}",id)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Product.class);
				
		product.subscribe();
		return product;
	}

	@Override
	public Mono<Enterprise> findByUrlIdEnterprise(String id) {
		// TODO Auto-generated method stub
		Mono<Enterprise> customer=objCustomer.get().uri("/enterprise/{id}",id)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Enterprise.class);
				
		customer.subscribe();
		return customer;
	}

	@Override
	public Mono<Natural> findByUrlIdNatural(String id) {
		// TODO Auto-generated method stub
		Mono<Natural> natural=objCustomer.get().uri("/natural/{id}",id)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Natural.class);
				
		natural.subscribe();
		return natural;
	}

	
	
	

}
