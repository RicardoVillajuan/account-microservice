package com.bank.service;

import java.util.Date;
import com.bank.entity.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {
	
	
	
	Mono<Account> findById(String idaccount);
	
	Flux<Account> findByAll();
	
	Mono<Void> deleteById(String id);
	
	Mono<Account> createAccount(Account account);
	
	Mono<Account> updateById(String id, Account account);
	
	Mono<Account> updateByAccountNumber(String accountnumber, Account account);
	
	Mono<Account> findByAccountNumber(String accountnumber);
	
	Flux<Account> findByIdClientAll(String idclient);
	
	Flux<Account> findByDate(Date startdate, Date enddate,String idproduct);
}
