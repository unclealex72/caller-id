package uk.co.unclealex.callerid.shared.model;

import java.io.Serializable;

public class CountryAndArea implements Serializable {

	private String i_country;
	private String i_area;
	
	
	protected CountryAndArea() {
		// Constructor for serialisation
	}

	public CountryAndArea(String country, String area) {
		super();
		i_country = country;
		i_area = area;
	}

	public String getCountry() {
		return i_country;
	}

	public String getArea() {
		return i_area;
	}	
}
