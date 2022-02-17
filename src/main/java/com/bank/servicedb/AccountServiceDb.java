package com.bank.servicedb;

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

import io.netty.util.internal.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AccountServiceDb implements IAccountService{

	private final AccountRepository repoAccount;
	
	WebClient objCustomer=WebClient.builder().baseUrl("http://localhost:8094").build();
	WebClient objProduct=WebClient.builder().baseUrl("http://localhost:8093").build();
	
	

	@Override
	public Flux<Account> findByAll() {
		// TODO Auto-generated method stub
		return repoAccount.findAll();
	}
	
	@Override
	public Mono<Account> findById(String idclient) {
		// TODO Auto-generated method stub
		return repoAccount.findById(idclient);
	}

	@Override
	public Mono<Void> deleteById(String id) {
		// TODO Auto-generated method stub
		return repoAccount.deleteById(id);
	}

	
	
	//Metodos que obtienen registros de otras entidades
	@Override
	public Mono<Product> findByUrlIdProduct(String id) {
		// TODO Auto-generated method stub
		Mono<Product> product=objProduct.get().uri("/product/{id}",id)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Product.class);
				
		product.subscribe();
		return product;
	}

	@Override
	public Mono<Customer> findByUrlIdCustomer(String id) {
		// TODO Auto-generated method stub
		Mono<Customer> customer=objCustomer.get().uri("/customer/{id}",id)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(Customer.class);
				
		customer.subscribe();
		return customer;
	}

	@Override
	public Mono<Account> create(String idcustomer,String idproduct,Authorities authorities) {
		// TODO Auto-generated method stub
		Mono<Customer> customer=objCustomer.get().uri("/customer/{id}",idcustomer)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToMono(Customer.class);
		
		Mono<Product> product=objProduct.get().uri("/product/{id}",idproduct)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToMono(Product.class);
		
		Mono<Account> objAccount=repoAccount.findByIdclient(idcustomer);
		
		return objAccount.flatMap(a->{
			
			return customer.flatMap(c->{
				return product.flatMap(p->{
					Boolean boo=false;
					switch (c.getTypecustomer()) {

					case "PERSONAL":
							List<Cards> listCards=new ArrayList<>();
							Cards cards=new Cards();
							listCards=a.getCards();

							for (Cards card : listCards) {
								if(card.getNameproduct().equalsIgnoreCase("ahorro") && p.getName().equalsIgnoreCase("ahorro") || 
										card.getNameproduct().equalsIgnoreCase("Cuenta Corriente") && 
											p.getName().equalsIgnoreCase("Cuenta Corriente") || card.getNameproduct().equalsIgnoreCase("Personal") && 
												p.getName().equalsIgnoreCase("Personal")) {
									boo=true;
									return Mono.error(new Exception("No se puede agregar mas cuentas de ahorro o corriente o credito"));
								}
							}
							if(boo!=true) {
								// ya existe el cliente
								Long numero= ThreadLocalRandom.current().nextLong(100000000,1000000000+1);
								cards.setIdproduct(idproduct);
								if(p.getType().equalsIgnoreCase("Pasivos")) {
									cards.setAccountnumber(Long.toString(numero));
								}
								cards.setAmmount(0);
								cards.setDate(new Date());
								if(p.getName().equalsIgnoreCase("ahorro")) {
									cards.setMaintenancecommission(0);
									cards.setMaxmovements(2);
								}
								if(p.getName().equalsIgnoreCase("Cuenta Corriente")) {
									cards.setMaintenancecommission(1.5);
									cards.setMaxmovements(0);
								}
								if(p.getName().equalsIgnoreCase("Plazo Fijo")) {
									cards.setMaintenancecommission(0);
									cards.setMaxmovements(1);
								}
								
								cards.setNameproduct(p.getName());
								listCards.add(cards);
								a.setCards(listCards);
								
								return repoAccount.save(a);
							}
							
						break;
						
					case "EMPRESARIAL":
						List<Cards> listCard=new ArrayList<>();
						Cards card=new Cards();
						listCards=a.getCards();

						
						if(p.getName().equalsIgnoreCase("Plazo Fijo") || p.getName().equalsIgnoreCase("EMPRESARIAL")) {
							// ya existe el cliente
							Long numero= ThreadLocalRandom.current().nextLong(100000000,1000000000+1);
							card.setIdproduct(idproduct);
							if(p.getType().equalsIgnoreCase("Pasivos")) {
								card.setAccountnumber(Long.toString(numero));
							}
							card.setAmmount(0);
							card.setDate(new Date());
							if(p.getName().equalsIgnoreCase("ahorro")) {
								card.setMaintenancecommission(0);
								card.setMaxmovements(2);
							}
							if(p.getName().equalsIgnoreCase("Cuenta Corriente")) {
								card.setMaintenancecommission(1.5);
								card.setMaxmovements(0);
							}
							if(p.getName().equalsIgnoreCase("Plazo Fijo")) {
								card.setMaintenancecommission(0);
								card.setMaxmovements(1);
							}
							card.setNameproduct(p.getName());
							card.setAuthorities(authorities);
							/*SimpleDateFormat strDate=new SimpleDateFormat("dd/MM/yyyy");
							Date date=new Date();
							card.setDate();*/
							listCards.add(card);
							a.setCards(listCards);
							
							return repoAccount.save(a);
						}
						
						break;

					default:
						return Mono.error(new Exception("Su tipo de Cuenta no se encuentra en el patron "));
					}
					return null;
				});
				
			});
		}).switchIfEmpty(createAccount(customer,product,authorities));
	}

	
	
	private Mono<Account> createAccount(Mono<Customer> custome,Mono<Product> prod,Authorities autoriti) {
		// TODO Auto-generated method stub
		
		return custome.flatMap(cus->{
			return prod.flatMap(pro->{
				Account acco=new Account();
				List<Cards> listCards=new ArrayList<>();
				Cards cards=new Cards();
				
				Long numero= ThreadLocalRandom.current().nextLong(100000000,1000000000+1);
				cards.setIdproduct(pro.getId());
				acco.setIdclient(cus.getId());
				if(pro.getType().equalsIgnoreCase("Pasivos")) {
					cards.setAccountnumber(Long.toString(numero));
				}
				cards.setAmmount(0);
				cards.setDate(new Date());
				if(pro.getName().equalsIgnoreCase("ahorro")) {
					cards.setMaintenancecommission(0);
					cards.setMaxmovements(2);
				}
				if(pro.getName().equalsIgnoreCase("Cuenta Corriente")) {
					cards.setMaintenancecommission(1.5);
					cards.setMaxmovements(0);
				}
				if(pro.getName().equalsIgnoreCase("Plazo Fijo")) {
					cards.setMaintenancecommission(0);
					cards.setMaxmovements(1);
				}
				cards.setNameproduct(pro.getName());
				if(cus.getTypecustomer().equalsIgnoreCase("EMPRESARIAL")) {
					cards.setAuthorities(autoriti);
				}
				
				listCards.add(cards);
				acco.setCards(listCards);
				
				return repoAccount.save(acco);
			});
			
		});
	}

	@Override
	public Mono<Account> findByIdClient(String idclient) {
		// TODO Auto-generated method stub
		return repoAccount.findByIdclient(idclient);
	}

	
	

}
