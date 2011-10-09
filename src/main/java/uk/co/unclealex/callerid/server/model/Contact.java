package uk.co.unclealex.callerid.server.model;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

@Entity
public class Contact extends BusinessKeyedBean<Contact, String> {

	public static Contact example() {
		return new Contact();
	}	
	
	private String i_name;
	private SortedSet<TelephoneNumber> i_telephoneNumbers;
	private SortedSet<User> i_users;
	
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
	@Sort(type=SortType.NATURAL)
	public SortedSet<TelephoneNumber> getTelephoneNumbers() {
		return i_telephoneNumbers;
	}

	public void setTelephoneNumbers(SortedSet<TelephoneNumber> telephoneNumbers) {
		i_telephoneNumbers = telephoneNumbers;
	}

	@ManyToMany(mappedBy="contacts")
	@Sort(type=SortType.NATURAL)
	public SortedSet<User> getUsers() {
		return i_users;
	}

	public void setUsers(SortedSet<User> users) {
		i_users = users;
	}

	
}
