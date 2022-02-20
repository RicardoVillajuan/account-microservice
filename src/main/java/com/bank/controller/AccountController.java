package com.bank.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Account;
import com.bank.model.Authorities;
import com.bank.model.Cards;

import com.bank.model.Customer;
import com.bank.model.Product;
import com.bank.model.Signatories;
import com.bank.service.IAccountService;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	
	private static final Logger log=LoggerFactory.getLogger(SpringBootApplication.class);

	private final IAccountService accountService;
	
	
	
	@PostMapping("/{idcustomer}/{idproduct}")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Account> saveCustomer(@PathVariable String idcustomer,@PathVariable String idproduct,@RequestBody Authorities authorities){
		
		return accountService.create(idcustomer, idproduct,authorities);
	}
	
	@PutMapping("/{id}")
	public Mono<Account> update(@PathVariable String id,@RequestBody Account account){
		
		return accountService.update(id, account);
	}
	
	@GetMapping
	public Flux<Account> findAll(){
		
		return accountService.findByAll(); 
	}
	
	@GetMapping("/{id}")
	public Mono<Account> findByIdClient(@PathVariable String id){
		
		return accountService.findByIdClient(id);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable String id){
		
		return accountService.deleteById(id);
	}
	
}
