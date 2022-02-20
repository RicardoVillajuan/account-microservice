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
	public Mono<Account> create(String idcustomer, String idproduct, Authorities authorities) {
		// TODO Auto-generated method stub
		Mono<Customer> customer = repoWeb.getCustomer(idcustomer);
		Mono<Product> product = repoWeb.getProduct(idproduct);
		Mono<Account> objAccount = repoAccount.findByIdclient(idcustomer);

		return objAccount.flatMap(a -> {
			return customer.flatMap(c -> {
				return product.flatMap(p -> {

					for (Cards card : a.getCards()) {
						if (card.getNameproduct().equalsIgnoreCase("ahorro") && p.getName().equalsIgnoreCase("ahorro")
								|| card.getNameproduct().equalsIgnoreCase("Cuenta Corriente")
										&& p.getName().equalsIgnoreCase("Cuenta Corriente")
								|| card.getNameproduct().equalsIgnoreCase("Personal")
										&& p.getName().equalsIgnoreCase("Personal")
								|| card.getNameproduct().equalsIgnoreCase("Personal")
										&& p.getName().equalsIgnoreCase("Personal")) {
							return Mono.error(new Exception(
									"Alcanzo el limite maximo para adquirir una cuenta con ese producto"));
						}
					}
					
					Cards cards = new Cards();
					Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
					cards.setAccountnumber(Long.toString(numero));
					cards.setAmmount(23);
					cards.setDate(new Date());
					cards.setIdproduct(p.getId());
					cards.setNameproduct(p.getName());
					
					switch (p.getType().toUpperCase()) {

					case "PASIVOS":
						
						cards.setMaxmovements(0);
						if (p.getName().equalsIgnoreCase("AHORRO")) {
							cards.setMaxmovements(3);
						}
						cards.setMaintenancecommission(2);
						if (p.getName().equalsIgnoreCase("AHORRO") || p.getName().equalsIgnoreCase("PLAZO FIJO")) {
							cards.setMaintenancecommission(0);
						}
						a.getCards().add(cards);
						a.setCards(a.getCards());
						return repoAccount.save(a);

					case "ACTIVOS":
						
						cards.setMaxmovements(0);
						cards.setMaintenancecommission(0);
						a.getCards().add(cards);
						a.setCards(a.getCards());
						return repoAccount.save(a);

					default:
						return Mono.error(new Exception("Su tipo de Cuenta no se encuentra en el patron "));
					}
					
				});

			});
		}).switchIfEmpty(createAccount(customer, product, authorities));

	}

	private Mono<Account> createAccount(Mono<Customer> custome, Mono<Product> prod, Authorities autoriti) {
		// TODO Auto-generated method stub

		return custome.flatMap(cus -> {
			return prod.flatMap(pro -> {
				Account acco = new Account();
				List<Cards> listCards = new ArrayList<>();
				Cards cards = new Cards();

				Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
				cards.setIdproduct(pro.getId());
				acco.setIdclient(cus.getId());
				cards.setAccountnumber(Long.toString(numero));
				cards.setAmmount(0);
				cards.setDate(new Date());
				cards.setMaxmovements(0);
				if (pro.getName().equalsIgnoreCase("AHORRO")) {
					cards.setMaxmovements(3);
				}
				cards.setMaintenancecommission(2);
				if (pro.getName().equalsIgnoreCase("AHORRO") || pro.getName().equalsIgnoreCase("PLAZO FIJO")) {
					cards.setMaintenancecommission(0);
				}
				cards.setNameproduct(pro.getName());
				cards.setAuthorities(autoriti);
				listCards.add(cards);
				acco.setCards(listCards);

				return repoAccount.save(acco);
			});

		});
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

}
