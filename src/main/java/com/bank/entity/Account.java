package com.bank.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import com.bank.model.Authorities;
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
	private String idcustomer;
	private String idproduct;
	private String nameproduct;
	private String accountnumber;
	private int maxmovements;
	private double maintenancecommission;
	private Double ammount;
	private Double maxammount;
	private Authorities authorities;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime expirationdate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime creationdate;
	
	private Boolean status=true;
	
}
