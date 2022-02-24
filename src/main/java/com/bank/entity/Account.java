package com.bank.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.bank.model.Authorities;
import com.bank.model.Cards;
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
	private String idproduct;
	private String nameproduct;
	private String accountnumber;
	private int maxmovements;
	private double maintenancecommission;
	private long ammount;
	private Authorities authorities;
	
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	
	
	
	
	
	
	
}
