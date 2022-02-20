package com.bank.service;

import com.bank.entity.Account;
import com.bank.model.Authorities;
import com.bank.model.Customer;
import com.bank.model.Product;
import com.bank.model.Signatories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {
	
	Mono<Account> findByIdClient(String idclient);
	
	Flux<Account> findByAll();
	
	Mono<Void> deleteById(String id);
	
	Mono<Account> createAccountAhorro(Account account);
	
	Mono<Account> createAccountCorriente(Account account);
	
	Mono<Account> createAccountPlazoFijo(Account account);
	
	Mono<Account> createAccountCredito(Account account);
	
	Mono<Account> update(String id, Account account);
	
}
