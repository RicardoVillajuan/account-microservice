package com.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
	
	private String id;
	private String typecustomer;
	private String fullname;
	private String email;
    private String profile;
    private String typedocument;
    private String documentnumber;
}
