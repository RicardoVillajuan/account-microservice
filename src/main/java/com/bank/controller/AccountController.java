package com.bank.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bank.entity.Account;
import com.bank.model.Authorities;
import com.bank.model.Cards;
import com.bank.model.Enterprise;
import com.bank.model.Natural;
import com.bank.model.Product;
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
	
	@PostMapping("/enterprise/{idclient}/{idproduct}")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Account> saveEnterpr(@PathVariable String idclient
			, @PathVariable String idproduct,@RequestBody Authorities authorities){
		
		Mono<Account> account= accountService.findByIdClient(idclient);
		Mono<Enterprise> enterprise=accountService.findByUrlIdEnterprise(idclient);
		Mono<Product> product=accountService.findByUrlIdProduct(idproduct);

		return account.flatMap(cp->{			
			return enterprise.flatMap(c->{
			 	return product.flatMap(p->{
			 		System.out.println(p);
					if(c.getType().equalsIgnoreCase("Empresarial")==true && p.getName().equalsIgnoreCase("Plazo fijo")==true) {
						
						List<Cards> listCards= cp.getCards();
							Cards cards=new Cards();
							cards.setIdproduct(p.getId());
							cards.setNameproduct(p.getName());
							cards.setAccountnumber("37364532162737");
							cards.setMaxmovements(0);
							cards.setMaintenancecommission(1.5);
							cards.setAmmount(1000);
							cards.setAuthorities(authorities);
							cards.setDate(new Date());
							
							listCards.add(cards);
							cp.setCards(listCards);
					}else {
						throw new RuntimeException("No se puede agregar cuentas Ahorro o corriente");
					}
					return accountService.save(cp);		
				});
		});
	}).switchIfEmpty(saveAccountEnterprise(idclient,product,authorities));
	}
	
	
	@PostMapping("/natural/{idclient}/{idproduct}")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Account> savePersonal(@PathVariable String idclient
			, @PathVariable String idproduct){
		
		Mono<Account> account= accountService.findByIdClient(idclient);
		Mono<Natural> natural=accountService.findByUrlIdNatural(idclient);
		Mono<Product> product=accountService.findByUrlIdProduct(idproduct);

		return account.flatMap(cp->{			
			return natural.flatMap(c->{
			 	return product.flatMap(p->{
			 		if(c.getType().equalsIgnoreCase("Personal")==true) {
			 			
			 			Boolean boo=false;
			 			List<Cards> listCards= cp.getCards();
			 			
			 			for (Cards cards : listCards) {
			 				if(cards.getNameproduct().equalsIgnoreCase("Ahorro") && p.getName().equalsIgnoreCase("Ahorro") || cards.getNameproduct().equalsIgnoreCase("Cuenta Corriente") && p.getName().equalsIgnoreCase("Cuenta Corriente")) {
								boo=true;
								break;
							}
						}
			 			
			 			if(boo!=true) {
								Cards cards=new Cards();
								cards.setIdproduct(p.getId());
								cards.setNameproduct(p.getName());
								cards.setAccountnumber("37364532162737");
								cards.setMaxmovements(4);
								cards.setMaintenancecommission(1.5);
								cards.setAmmount(0);
								cards.setAuthorities(null);
								cards.setDate(new Date());
								
								listCards.add(cards);
								cp.setCards(listCards);
						}else {
							throw new RuntimeException("No se puede agregar cuentas Ahorro o corriente");
						}
			 			
			 		}else {
			 			throw new RuntimeException("No es una persona Natural");
			 		}
					
					return accountService.save(cp);		
				});
		});
	
	}).switchIfEmpty(saveAccountNatural(idclient,product));
	}
	
	public Mono<Account> saveAccountEnterprise(String idclient, Mono<Product> produ,Authorities authorities) {
		// TODO Auto-generated method stub
		Account s=new Account();
		List<Cards> listCards=new ArrayList<>();
		Cards cards=new Cards();
		
		return produ.flatMap(e->{
			if(e.getName().equalsIgnoreCase("Plazo Fijo")) {
				cards.setIdproduct(e.getId());
				cards.setNameproduct(e.getName());
				cards.setAccountnumber("32364534162737");
				cards.setMaxmovements(4);
				cards.setMaintenancecommission(1.5);
				cards.setAmmount(100);
				cards.setAuthorities(authorities);
				cards.setDate(new Date());
				
				listCards.add(cards);
				s.setIdclient(idclient);			
				s.setCards(listCards);

				return accountService.save(s);
			}else {
				throw new RuntimeException("No se puede agregar cuentas que no sean de Plazo Fijo");
			}
			
		});
	}
	
	public Mono<Account> saveAccountNatural(String idclient, Mono<Product> produ) {
		// TODO Auto-generated method stub
		Account s=new Account();
		List<Cards> listCards=new ArrayList<>();
		Cards cards=new Cards();
		
		return produ.flatMap(e->{
			
				cards.setIdproduct(e.getId());
				cards.setNameproduct(e.getName());
				cards.setAccountnumber("32364534162737");
				cards.setMaxmovements(4);
				cards.setMaintenancecommission(1.5);
				cards.setAmmount(100);
				
				cards.setDate(new Date());
				
				listCards.add(cards);
				s.setIdclient(idclient);			
				s.setCards(listCards);

				return accountService.save(s);		
		});
	}
	
	@GetMapping
	public Flux<Account> findAll(){
		
		return accountService.findByAll();
	}
	
}
