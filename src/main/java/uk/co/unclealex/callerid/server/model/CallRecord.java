package uk.co.unclealex.callerid.server.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import uk.co.unclealex.hibernate.model.KeyedBean;

@Entity
public class CallRecord extends KeyedBean<CallRecord> {

	public static CallRecord example() {
		return new CallRecord();
	}

	private Date i_callDate;
	private TelephoneNumber i_telephoneNumber;
	
	
	protected CallRecord() {
		super();
	}

	public CallRecord(Date callDate, TelephoneNumber telephoneNumber) {
		super();
		i_callDate = callDate;
		i_telephoneNumber = telephoneNumber;
	}

	@Override
	@Id @GeneratedValue
	public Integer getId() {
		return super.getId();
	}

	@Override
	public String toString() {
		return getTelephoneNumber() + " @ " + getCallDate();
	}
	
	@Override
	public int compareTo(CallRecord o) {
		return o.getCallDate().compareTo(getCallDate());
	}
	
	public Date getCallDate() {
		return i_callDate;
	}

	protected void setCallDate(Date callDate) {
		i_callDate = callDate;
	}

	@ManyToOne
	public TelephoneNumber getTelephoneNumber() {
		return i_telephoneNumber;
	}

	protected void setTelephoneNumber(TelephoneNumber telephoneNumber) {
		i_telephoneNumber = telephoneNumber;
	}
}
