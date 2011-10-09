package uk.co.unclealex.callerid.server.model;

public class GoogleContact {

	private final String i_name;
	private final String i_telephoneNumber;
	
	public GoogleContact(String name, String telephoneNumber) {
		super();
		i_name = name;
		i_telephoneNumber = telephoneNumber;
	}

	public String getName() {
		return i_name;
	}

	public String getTelephoneNumber() {
		return i_telephoneNumber;
	}
	
	
}
