package com.bank.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String>{
	
	
	
	Mono<Account> findByIdcustomerAndIdproduct(String idcustomer, String idproduct);
	
	Mono<Account> findByIdcustomerAndNameproduct(String idcustomer, String nameproduct);

	Mono<Account> findByAccountnumber(String accountnumber);
	
	Flux<Account> findByIdcustomer(String idcustomer);
	
	Flux<Account> findByCreationdateBetween(Date startdate, Date enddate);
}
