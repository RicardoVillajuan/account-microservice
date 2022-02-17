package com.bank.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Authorities {

	List<Holders> holders;//titulares
	List<Signatories> signatories;// firmantes
}
