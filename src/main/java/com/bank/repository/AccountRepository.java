package com.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String>{
	
	
	
	Mono<Account> findByIdclientAndIdproduct(String idclient, String idproduct);

	Mono<Account> findByAccountnumber(String accountnumber);
	
	Flux<Account> findByIdclient(String idclient);
}
