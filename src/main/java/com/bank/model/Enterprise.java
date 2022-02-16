package com.bank.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Enterprise {
	
	private String id;
	private String name;
	private String ruc;
	private String type;
	private String profile;

}