package com.bank.servicedb;

import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.entity.Account;
import com.bank.model.Authorities;
import com.bank.model.Cards;
import com.bank.model.Customer;
import com.bank.model.Product;
import com.bank.model.Signatories;
import com.bank.repository.AccountRepository;
import com.bank.service.IAccountService;
import com.bank.webclient.repoWebClient;

import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AccountServiceDb implements IAccountService {

	private final AccountRepository repoAccount;

	private repoWebClient repoWeb = new repoWebClient();

	@Override
	public Flux<Account> findByAll() {
		// TODO Auto-generated method stub
		return repoAccount.findAll();
	}

	@Override
	public Mono<Void> deleteById(String id) {
		// TODO Auto-generated method stub
		return repoAccount.deleteById(id);
	}

	


	@Override
	public Mono<Account> findByIdClient(String idclient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Account> update(String id, Account account) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Account> createAccountAhorro(Account account) {
		// TODO Auto-generated method stub
		Mono<Account> objAccount = repoAccount.findByIdclientAndIdproductAndNameproduct(account.getIdclient(),account.getIdproduct(),account.getNameproduct());
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setMaxmovements(3);
		
		account.setDate(new Date());
		
		return objClient.doOnNext(e->{
			if(e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL")) {
				throw new RuntimeException("Usted es un cliente empresarial, no puede tener una cuenta de ahorro");
			}
			
		}).flatMap(e->{
			return objAccount.doOnNext(a->{
				if(e.getTypecustomer().equalsIgnoreCase("PERSONAL"))
					throw new RuntimeException("Usted ya tiene una cuenta de Ahorro no puede tener mas");
				
			}).flatMap(c->{
				
				return repoAccount.save(account);
			})
			.switchIfEmpty(save(account));
		});
	}
	
	@Override
	public Mono<Account> createAccountCorriente(Account account) {
		// TODO Auto-generated method stub
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
				
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(1.5);
		account.setMaxmovements(3);
		account.setDate(new Date());
		
		return objClient.flatMap(e->{
			Mono<Account> objAccount = repoAccount.findByIdclientAndIdproductAndNameproduct(account.getIdclient(),account.getIdproduct(),account.getNameproduct());
			if(e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL"))
				return repoAccount.save(account);
			
			return objAccount.doOnNext(a->{
				throw new RuntimeException("Usted solo puede tener una cuenta Corriente");
				
			}).switchIfEmpty(save(account));
		});
	}

	@Override
	public Mono<Account> createAccountPlazoFijo(Account account) {
		// TODO Auto-generated method stub
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		
		
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setMaxmovements(3);
		account.setDate(new Date());
		
		return objClient.doOnNext(e->{
			if(e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL")) {
				throw new RuntimeException("Usted es un cliente empresarial, no puede tener una Cuenta a Plazo Fijo");
			}
			
		}).flatMap(e->{
			return repoAccount.save(account);
		});
	}

	private Mono<Account> save(Account account) {
		// TODO Auto-generated method stub
		
		return repoAccount.save(account);
	}

	@Override
	public Mono<Account> createAccountCredito(Account account) {
		// TODO Auto-generated method stub
		Mono<Account> objAccount = repoAccount.findByIdclientAndIdproductAndNameproduct(account.getIdclient(),account.getIdproduct(),account.getNameproduct());
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setDate(new Date());
		
		return objClient.flatMap(e->{
			if(e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL"))
				return repoAccount.save(account);
		
			return objAccount.doOnNext(a->{
					throw new RuntimeException("Usted ya tiene un producto de credito");
			}).switchIfEmpty(savecredit(e,account));
		});
	}

	private  Mono<Account> savecredit(Customer c, Account account) {
		// TODO Auto-generated method stub
		if(c.getTypecustomer().equalsIgnoreCase("PERSONAL") && account.getNameproduct().equalsIgnoreCase("EMPRESARIAL")) 
			throw new RuntimeException("Usted es un tipo personal, no puede tener un producto de tipo empresarial");
		return repoAccount.save(account);
	}

}
