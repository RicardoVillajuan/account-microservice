package com.bank.service;

import com.bank.entity.Account;
import com.bank.model.Enterprise;
import com.bank.model.Natural;
import com.bank.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {

	Mono<Account> save(Account account);
	
	Mono<Account> findByIdClient(String idclient);
	
	Flux<Account> findByAll();
	
	
	//Metodos para obtener registros de otras entidades
	
	Mono<Product> findByUrlIdProduct(String idproduct);	
	Mono<Enterprise> findByUrlIdEnterprise(String identerprise);
	Mono<Natural> findByUrlIdNatural(String idnatural);
}
