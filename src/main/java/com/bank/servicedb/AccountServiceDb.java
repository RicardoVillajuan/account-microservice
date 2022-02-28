package com.bank.servicedb;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.stereotype.Service;
import com.bank.entity.Account;
import com.bank.model.Customer;
import com.bank.model.Product;
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
	public Flux<Account> findByIdClientAll(String idcustomer) {
		
		return repoAccount.findByIdcustomer(idcustomer);
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
		Mono<Customer> objCustomer = repoWeb.getCustomer(account.getIdcustomer());
		
		return objProduct.flatMap(pro->{
			return objCustomer.flatMap(cus->{
				if((pro.getName().equalsIgnoreCase("ahorro") || pro.getName().equalsIgnoreCase("Plazo Fijo") || pro.getName().equalsIgnoreCase("Personal")) && cus.getTypecustomer().equalsIgnoreCase("Empresarial"))
					throw new RuntimeException("Una cuenta empresarial no puede tener una Cuenta de Ahorro o de Plazo Fijo, o un credito personal");
				if(pro.getName().equalsIgnoreCase("Empresarial") && cus.getTypecustomer().equalsIgnoreCase("Personal"))
					throw new RuntimeException("Una cuenta Personal no puede tener una Cuenta empresarial");
				
				if(pro.getType().equalsIgnoreCase("Pasivos") && cus.getProfile().equalsIgnoreCase("NORMAL")) {
					return saveAccount(pro,cus,account);
				}
				if(pro.getType().equalsIgnoreCase("Pasivos") && cus.getProfile().equalsIgnoreCase("VIP")) {
					return saveAccountVip(pro,cus,account);
				}
				return saveAccountCredit(pro,cus,account);

			});
		});
	}
	
	/*private void checkAccountStatus(Customer cus) {
		// TODO Auto-generated method stub
		Flux<Account> listAccountClient=repoAccount.findByIdcustomer(cus.getId());
		listAccountClient.doOnNext(e->{
			if(e.getStatus()==false) {
				throw new RuntimeException("Usted es un cliente que tiene una deuda, no puede tener mas cuentas");
			}
		});
	}*/

	private Mono<Account> saveAccountVip(Product product,Customer cus, Account account) {
		
		Mono<Account> objAcco = repoAccount.findByIdcustomerAndIdproduct(account.getIdcustomer(),"62067603e1a3926be69f829e");
		
		return objAcco.doOnNext(e->{
			
		}).flatMap(e->{
			//..............................
			Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
			account.setAccountnumber(Long.toString(numero));		
			account.setMaxmovements(3);
			account.setIdproduct(product.getId());
			account.setNameproduct(product.getName());
			account.setCreationdate(LocalDateTime.now());
			account.setMaintenancecommission(0);
			if(account.getAmmount()==null) {
				account.setAmmount(0.0);
				
			}
			return repoAccount.save(account);
		}).switchIfEmpty(Mono.empty());
		
	}
	
	

	private Mono<Account> saveAccount(Product pro, Customer cus,Account account) {
		// TODO Auto-generated method stub
		Mono<Account> objAccount = repoAccount.findByIdcustomerAndIdproduct(account.getIdcustomer(),account.getIdproduct());
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));		
		account.setMaxmovements(3);
		account.setIdproduct(pro.getId());
		account.setNameproduct(pro.getName());
		account.setCreationdate(LocalDateTime.now());
		account.setMaintenancecommission(0);
		if(account.getAmmount()==null) {
			account.setAmmount(0.0);
		}repoAccount.save(account);
		if(pro.getName().equalsIgnoreCase("Plazo Fijo")) {	
			Flux<Account> listAccountClient=repoAccount.findByIdcustomer(cus.getId());
			
			return repoAccount.save(account);
		
		}
		if(pro.getName().equalsIgnoreCase("Cuenta Corriente")) {
			account.setMaintenancecommission(0.75);
		}

		return objAccount.flatMap(e->{
			if(e.getNameproduct().equalsIgnoreCase("Cuenta Corriente") || e.getNameproduct().equalsIgnoreCase("Ahorro")) {
				throw new RuntimeException("No puede tener mas Cuentas de Ahorro o Corriente");
			}

			return repoAccount.save(account);
		}).switchIfEmpty(saveEntityAccount(account));
	}

	private Mono<Account> saveAccountCredit(Product pro, Customer cus, Account account) {
		
		Mono<Account> objAccount = repoAccount.findByIdcustomerAndIdproduct(account.getIdcustomer(),account.getIdproduct());
		
		Long numero = ThreadLocalRandom.current().nextLong(100000000, 1000000000 + 1);
		account.setAccountnumber(Long.toString(numero));		
		account.setMaxmovements(0);
		account.setIdproduct(pro.getId());
		account.setNameproduct(pro.getName());
		account.setCreationdate(LocalDateTime.now());
		account.setMaintenancecommission(0);
		account.setExpirationdate(account.getCreationdate().plusMonths(1).plusDays(20));
		account.setMaxmovements(0);
		account.setMaxammount(20000.0);
		if(account.getAmmount()==null) {
			account.setAmmount(0.0);
		}
		if(pro.getName().equalsIgnoreCase("Empresarial")) {	
			return repoAccount.save(account);
		}
		
		return objAccount.flatMap(e->{
			if(e.getNameproduct().equalsIgnoreCase("Personal")) {
				throw new RuntimeException("No puede tener mas Productos de credito ");
			}
			
			return repoAccount.save(account);
		}).switchIfEmpty(saveEntityAccount(account));
	}

	private Mono<Account> saveEntityAccount(Account account) {
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

	@Override
	public Flux<Account> findByDate(Date startdate, Date enddate,String idproduct) {
		// TODO Auto-generated method stub
		return repoAccount.findByCreationdateBetween(startdate, enddate)
				.filter(e->{
					return e.getIdproduct().equals(idproduct);
				}).map(e->{
					return e;
				});
	}

}
