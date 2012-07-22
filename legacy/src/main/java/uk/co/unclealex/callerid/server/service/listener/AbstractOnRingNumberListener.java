package uk.co.unclealex.callerid.server.service.listener;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.server.dao.TelephoneNumberDao;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public abstract class AbstractOnRingNumberListener implements NumberListener {

	private String i_number;
	private Integer i_telephoneNumberId;
	private PhoneNumber i_phoneNumber;
	private TelephoneNumberDao i_telephoneNumberDao;
	private boolean i_firstRing;
	
	@Override
	public boolean onNumber(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception {
		clearState();
		setNumber(number);
		setTelephoneNumberId(telephoneNumber==null?null:telephoneNumber.getId());
		setPhoneNumber(phoneNumber);
		setFirstRing(true);
		return true;
	}

	@Override
	public boolean onRing() throws Exception {
		String number = getNumber();
		PhoneNumber phoneNumber = getPhoneNumber();
		Integer telephoneNumberId = getTelephoneNumberId();
		TelephoneNumber telephoneNumber = telephoneNumberId==null?null:getTelephoneNumberDao().findById(telephoneNumberId);
		if (isFirstRing()) {
			setFirstRing(false);
			return beforeFirstRing(number, telephoneNumber, phoneNumber) && onFirstRing(number, telephoneNumber, phoneNumber);
		}
		else {
			return onRing(number, telephoneNumber, phoneNumber);
		}
	}
	
	protected void clearState() {
		// Default is to do nothing.
	}
	
	protected boolean beforeFirstRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception {
		return true;
	}

	protected boolean onFirstRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception {
		return onRing(number, telephoneNumber, phoneNumber);
	}

	protected abstract boolean onRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception;

	public String getNumber() {
		return i_number;
	}

	public void setNumber(String number) {
		i_number = number;
	}

	public Integer getTelephoneNumberId() {
		return i_telephoneNumberId;
	}

	public void setTelephoneNumberId(Integer telephoneNumberId) {
		i_telephoneNumberId = telephoneNumberId;
	}

	public PhoneNumber getPhoneNumber() {
		return i_phoneNumber;
	}

	public void setPhoneNumber(PhoneNumber phoneNumber) {
		i_phoneNumber = phoneNumber;
	}

	public TelephoneNumberDao getTelephoneNumberDao() {
		return i_telephoneNumberDao;
	}

	public void setTelephoneNumberDao(TelephoneNumberDao telephoneNumberDao) {
		i_telephoneNumberDao = telephoneNumberDao;
	}

	public boolean isFirstRing() {
		return i_firstRing;
	}

	public void setFirstRing(boolean firstRing) {
		i_firstRing = firstRing;
	}

}
