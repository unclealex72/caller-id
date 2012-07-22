package uk.co.unclealex.callerid.server.service.listener;

import java.util.Date;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.server.dao.CallRecordDao;
import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;

public class CallLoggingNumberListener implements NumberListener {

	private CallRecordDao i_callRecordDao;
	
	@Override
	public boolean onRing() throws Exception {
		// Do nothing
		return true;
	}

	@Override
	public boolean onNumber(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws Exception {
		CallRecord callRecord = new CallRecord(new Date(), telephoneNumber);
		getCallRecordDao().store(callRecord);
		return true;
	}

	public CallRecordDao getCallRecordDao() {
		return i_callRecordDao;
	}

	public void setCallRecordDao(CallRecordDao callRecordDao) {
		i_callRecordDao = callRecordDao;
	}

}
