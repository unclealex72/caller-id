package uk.co.unclealex.callerid.server.model;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import uk.co.unclealex.hibernate.model.KeyedBean;

import com.google.common.collect.Sets;

@Entity
public class TelephoneNumber extends KeyedBean<TelephoneNumber> {

	public static TelephoneNumber example() {
		return new TelephoneNumber();
	}

	private String i_number;
	private SortedSet<CallRecord> i_callRecords;
	private SortedSet<Contact> i_contacts;
	private Boolean i_blocked;
	
	protected TelephoneNumber() {
		super();
	}

	public TelephoneNumber(String number, Boolean blocked) {
		super();
		i_callRecords = Sets.newTreeSet();
		i_contacts = Sets.newTreeSet();
		i_number = number;
		i_blocked = blocked;
	}

	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
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
	@Sort(type=SortType.NATURAL)
	public SortedSet<CallRecord> getCallRecords() {
		return i_callRecords;
	}

	protected void setCallRecords(SortedSet<CallRecord> callRecords) {
		i_callRecords = callRecords;
	}

	public Boolean isBlocked() {
		return i_blocked;
	}

	protected void setBlocked(Boolean blocked) {
		i_blocked = blocked;
	}

	@ManyToMany(cascade={CascadeType.REMOVE, CascadeType.DETACH})
	@Sort(type=SortType.NATURAL)
	public SortedSet<Contact> getContacts() {
		return i_contacts;
	}

	public void setContacts(SortedSet<Contact> contacts) {
		i_contacts = contacts;
	}
}
