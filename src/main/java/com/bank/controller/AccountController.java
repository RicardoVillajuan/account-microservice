package com.bank.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.bank.entity.Account;
import com.bank.service.IAccountService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	
	private static final Logger log=LoggerFactory.getLogger(SpringBootApplication.class);

	private final IAccountService accountService;

	@GetMapping("/findbydate")
	public Flux<Account> findByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startdate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date enddate,@RequestParam String idproduct){
		 
		return accountService.findByDate(startdate,enddate,idproduct);
	}
	
	@GetMapping("/accountnumber/{accountnumber}")
	public Mono<Account> findByAccountNumber(@PathVariable String accountnumber){
		
		return accountService.findByAccountNumber(accountnumber); 
	}
	
	@GetMapping
	public Flux<Account> findAll(){
		
		return accountService.findByAll(); 
	}
	
	@GetMapping("/findById/{id}")
	public Mono<Account> findById(@PathVariable String id){
		
		return accountService.findById(id);
	}
	
	@GetMapping("/{id}")
	public Flux<Account> findByIdClient(@PathVariable String id){
		
		return accountService.findByIdClientAll(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Account> saveAccountAhorro(@RequestBody Account account){
		
		return accountService.createAccount(account);
					
	}
	
	@PutMapping("/{id}")
	public Mono<Account> update(@PathVariable String id,@RequestBody Account account){
		
		return accountService.updateById(id, account);
	}
	
	@PutMapping("/accountnumber/{accountnumber}")
	public Mono<Account> updateByAccountNumber(@PathVariable String accountnumber,@RequestBody Account account){
		
		return accountService.updateByAccountNumber(accountnumber, account);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable String id){
		
		return accountService.deleteById(id);
	}
	
}
