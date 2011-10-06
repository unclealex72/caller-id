package uk.co.unclealex.callerid.server.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import uk.co.unclealex.hibernate.model.KeyedBean;

@Entity
public class Contact extends KeyedBean<Contact> {

	public static Contact example() {
		return new Contact();
	}	
	
	private String i_name;
	private SortedSet<TelephoneNumber> i_telephoneNumbers;
	
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
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Contact) && compareTo((Contact) obj) == 0;
	}
	
	@Override
	public int compareTo(Contact o) {
		return getName().compareTo(o.getName());
	}
	
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
}
