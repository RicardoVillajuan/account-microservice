package com.bank.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bank.controller.AccountController;
import com.bank.model.Cards;
import com.bank.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class Account {

	@Id
	private String id;
	private String idclient;
	private List<Cards> cards;
	
	public Account(String idclient, List<Cards> cards) {
		
		this.idclient = idclient;
		this.cards = cards;
	}
	
	
	
	
	
	
}
