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
		
		return repoAccount.findAll();
	}

	@Override
	public Mono<Void> deleteById(String id) {
		
		return repoAccount.deleteById(id);
	}

	@Override
	public Flux<Account> findByIdClientAll(String idclient) {
		
		return repoAccount.findByIdclient(idclient);
	}

	@Override
	public Mono<Account> updateByAccountNumber(String accountnumber, Account account) {
		
		return repoAccount.findByAccountnumber(accountnumber).flatMap(e -> {
			account.setId(e.getId());
			return repoAccount.save(account);
		});
	}

	@Override
	public Mono<Account> createAccount(Account account) {
		Mono<Product> objProduct = repoWeb.getProduct(account.getIdproduct());
		Mono<Customer> objCustomer = repoWeb.getCustomer(account.getIdclient());
		Mono<Account> objAccount = repoAccount.findByIdclientAndIdproduct(account.getIdclient(),account.getIdproduct());
		
		return objProduct.flatMap(pro->{
			return objCustomer.flatMap(cus->{
				if((pro.getName().equalsIgnoreCase("ahorro") || pro.getName().equalsIgnoreCase("Cuenta Corriente")) && cus.getTypecustomer().equalsIgnoreCase("Empresarial"))
					throw new RuntimeException("Una cuenta empresarial no puede tener una Cuenta de Ahorro o de Plazo Fijo");
				if(pro.getName().equalsIgnoreCase("Empresarial") && cus.getTypecustomer().equalsIgnoreCase("Personal"))
					throw new RuntimeException("Una cuenta Personal no puede tener una Cuenta empresarial");
				
				return objAccount.flatMap(ac->{
					if((ac.getNameproduct().equalsIgnoreCase("Ahorro") || ac.getNameproduct().equalsIgnoreCase("Cuenta Corriente") || ac.getNameproduct().equalsIgnoreCase("Personal")) && cus.getTypecustomer().equalsIgnoreCase("personal"))
						throw new RuntimeException("Esta cuenta solo puede tener una cuenta Corriente o de Ahorro o Personal");
					
					Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
					Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
					account.setAccountnumber(Long.toString(numero));
					account.setMaxmovements(3);
					account.setIdproduct(pro.getId());
					account.setDate(new Date());
					account.setNameproduct(pro.getName());
					return repoAccount.save(account);
				}).switchIfEmpty(save(account,pro,cus));
			});
		});
	}

	

	private Mono<Account> save(Account account,Product pro,Customer cus) {
		if((cus.getProfile().equalsIgnoreCase("VIP") && pro.getName().equalsIgnoreCase("Ahorro")) || (cus.getProfile().equals("PYME") && pro.getName().equalsIgnoreCase("Cuenta Corriente")))
			throw new RuntimeException("Los clientes VIP no pueden crearse estas cuentas, primero deben tener una cuenta en el banco");
		
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setMaxmovements(3);
		account.setMaxmovements(3);
		account.setIdproduct(pro.getId());
		account.setDate(new Date());
		account.setNameproduct(pro.getName());
		return repoAccount.save(account);
	}
	
	@Override
	public Mono<Account> findByAccountNumber(String accountnumber) {
		
		return repoAccount.findByAccountnumber(accountnumber);
	}

	@Override
	public Mono<Account> findById(String idaccount) {
		
		return repoAccount.findById(idaccount);
	}

	@Override
	public Mono<Account> updateById(String id, Account account) {
		// TODO Auto-generated method stub
		return repoAccount.findById(id).flatMap(e -> {
			account.setId(id);
			return repoAccount.save(account);
		});
	}

}
