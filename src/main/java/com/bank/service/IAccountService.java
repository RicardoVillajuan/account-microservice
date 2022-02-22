package com.bank.service;

import com.bank.entity.Account;
import com.bank.model.Authorities;
import com.bank.model.Customer;
import com.bank.model.Product;
import com.bank.model.Signatories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {
	
	
	
	Mono<Account> findById(String idaccount);
	
	Flux<Account> findByAll();
	
	Mono<Void> deleteById(String id);
	
	Mono<Account> createAccount(Account account);
	
	
	Mono<Account> update(String id, Account account);
	
	Mono<Account> findByAccountNumber(String accountnumber);
	
	Flux<Account> findByIdClientAll(String idclient);
	
}
