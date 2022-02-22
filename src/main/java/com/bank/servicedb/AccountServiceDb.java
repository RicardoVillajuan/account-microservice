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
	public Mono<Account> update(String id, Account account) {
		
		return repoAccount.findById(id).flatMap(e -> {
			return repoAccount.save(account);
		});
	}

	@Override
	public Mono<Account> createAccountAhorro(Account account) {
		

		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());

		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setMaxmovements(3);
		account.setDate(new Date());

		return objClient.doOnNext(e -> {
			
			if (e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL")) {
				throw new RuntimeException("Usted es un cliente empresarial, no puede tener una cuenta de ahorro");
			}
		}).flatMap(e -> {
			//regrese si este cliente tiene un producto con ese idproducto
			Mono<Account> objAccount = repoAccount.findByIdclientAndIdproduct(account.getIdclient(),
					account.getIdproduct());
			
			return objAccount.doOnNext(a -> {
				if (e.getTypecustomer().equalsIgnoreCase("PERSONAL"))
					throw new RuntimeException("Usted ya tiene una cuenta de Ahorro no puede tener mas");
				throw new RuntimeException("Usted ya tiene una cuenta Corriente");
			}).switchIfEmpty(save(account));
		});
	}

	@Override
	public Mono<Account> createAccountCorriente(Account account) {
		
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(1.5);
		account.setMaxmovements(3);
		account.setDate(new Date());

		return objClient.flatMap(e -> {
			Mono<Product> objProduct = repoWeb.getProduct(account.getIdproduct());
			if (e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL"))
				return repoAccount.save(account);

			return objProduct.flatMap(o -> {
				if (!o.getName().equalsIgnoreCase(account.getNameproduct()))
					throw new RuntimeException("Su id de cuenta no hace referencia al nombre del producto");
				
				Mono<Account> objAccount = repoAccount.findByIdclientAndIdproduct(account.getIdclient(),account.getIdproduct());
				return objAccount.doOnNext(a -> {
					throw new RuntimeException("Usted ya no puede tener mas cuentas corrientes");
				});
			}).switchIfEmpty(save(account));
		});
	}

	@Override
	public Mono<Account> createAccountPlazoFijo(Account account) {
		
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setMaxmovements(3);
		account.setDate(new Date());

		return objClient.doOnNext(e -> {
			if (e.getTypecustomer().equalsIgnoreCase("EMPRESARIAL")) {
				throw new RuntimeException("Usted es un cliente empresarial, no puede tener una Cuenta a Plazo Fijo");
			}

		}).flatMap(e -> {
			Mono<Product> objProduct = repoWeb.getProduct(account.getIdproduct());
			return objProduct.doOnNext(o -> {

				if (!o.getName().equalsIgnoreCase(account.getNameproduct()))
					throw new RuntimeException("Su id de cuenta no hace referencia al nombre del producto");
			}).flatMap(o -> {
				return repoAccount.save(account);
			});
		});
	}

	private Mono<Account> save(Account account) {
		return repoAccount.save(account);
	}
	
	@Override
	public Mono<Account> createAccountCredito(Account account) {
		
		Mono<Customer> objClient = repoWeb.getCustomer(account.getIdclient());
		Mono<Product> objProduct = repoWeb.getProduct(account.getIdproduct());
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));
		account.setAmmount(account.getAmmount());
		account.setNameproduct(account.getNameproduct());
		account.setMaintenancecommission(0);
		account.setDate(new Date());

		return objProduct.doOnNext(p -> {
			if (!p.getName().equalsIgnoreCase(account.getNameproduct()))
				throw new RuntimeException("Su id de cuenta no hace referencia al nombre del producto");
			
		}).flatMap(p->{
			
			return objClient.flatMap(c->{
				if (c.getTypecustomer().equalsIgnoreCase("EMPRESARIAL"))
					return repoAccount.save(account);
				
				Mono<Account> objAccount = repoAccount.findByIdclientAndIdproduct(account.getIdclient(),account.getIdproduct());
				return objAccount.doOnNext(a -> {
					throw new RuntimeException("Usted ya tiene un producto de credito");				
				}).switchIfEmpty(savecredit(c, account));
			});
		});
	}

	private Mono<Account> savecredit(Customer c, Account account) {
		
		if (c.getTypecustomer().equalsIgnoreCase("PERSONAL")
				&& account.getNameproduct().equalsIgnoreCase("EMPRESARIAL"))
			throw new RuntimeException("Usted es un tipo personal, no puede tener un producto de tipo empresarial");
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

}
