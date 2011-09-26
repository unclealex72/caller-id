package uk.co.unclealex.callerid.server.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
	
	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return i_name;
	}

	public void setName(String name) {
		i_name = name;
	}

	@OneToMany
	@Sort(type=SortType.NATURAL)
	public SortedSet<TelephoneNumber> getTelephoneNumbers() {
		return i_telephoneNumbers;
	}

	protected void setTelephoneNumbers(SortedSet<TelephoneNumber> telephoneNumbers) {
		i_telephoneNumbers = telephoneNumbers;
	}
}
