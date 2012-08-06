package uk.co.unclealex.callerid.server.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class TelephoneNumber extends BusinessKeyedBean<TelephoneNumber, String> {

	public static TelephoneNumber example() {
		return new TelephoneNumber();
	}

	private String i_number;
	private List<CallRecord> i_callRecords;
	private List<Contact> i_contacts;
	private Boolean i_blocked;
	
	protected TelephoneNumber() {
		super();
	}

	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Override
	@Transient
	public String getBusinessKey() {
		return getNumber();
	}
	
	@Column(nullable=false, unique=true)
	public String getNumber() {
		return i_number;
	}

	protected void setNumber(String number) {
		i_number = number;
	}
	
	@OneToMany
	public List<CallRecord> getCallRecords() {
		return i_callRecords;
	}

	protected void setCallRecords(List<CallRecord> callRecords) {
		i_callRecords = callRecords;
	}

	public Boolean isBlocked() {
		return i_blocked;
	}

	protected void setBlocked(Boolean blocked) {
		i_blocked = blocked;
	}

	@ManyToMany
	public List<Contact> getContacts() {
		return i_contacts;
	}

	public void setContacts(List<Contact> contacts) {
		i_contacts = contacts;
	}
}
