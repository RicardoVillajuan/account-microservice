package com.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Natural {
	
	private String id;
    private String dni;
    private String firstName;
    private String lastName;
    private String email;
    private String type;
    private String profile;
}
