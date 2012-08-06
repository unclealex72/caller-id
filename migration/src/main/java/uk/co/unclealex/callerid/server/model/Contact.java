package uk.co.unclealex.callerid.server.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
public class Contact extends BusinessKeyedBean<Contact, String> {

	public static Contact example() {
		return new Contact();
	}	
	
	private String i_name;
	private List<TelephoneNumber> i_telephoneNumbers;
	private List<User> i_users;
	
	protected Contact() {
		super();
	}

	public Contact(String name) {
		super();
		i_name = name;
	}

	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Override
	@Transient
	public String getBusinessKey() {
		return getName();
	}
	
	@Column(nullable=false, unique=true)
	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}

	@ManyToMany(mappedBy="contacts")
	public List<TelephoneNumber> getTelephoneNumbers() {
		return i_telephoneNumbers;
	}

	public void setTelephoneNumbers(List<TelephoneNumber> telephoneNumbers) {
		i_telephoneNumbers = telephoneNumbers;
	}

	@ManyToMany(mappedBy="contacts")
	public List<User> getUsers() {
		return i_users;
	}

	public void setUsers(List<User> users) {
		i_users = users;
	}

	
}
