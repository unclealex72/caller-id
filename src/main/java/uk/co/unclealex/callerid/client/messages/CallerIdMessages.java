package uk.co.unclealex.callerid.client.messages;

import java.util.Date;

import com.google.gwt.i18n.client.Messages;

public interface CallerIdMessages extends Messages {

	@DefaultMessage("{0,date,EEEE, dd MMMM yyyy}")
	String callDate(Date callTime);

	@DefaultMessage("{0,date,HH:mm}")
	String callTime(Date callTime);

	@DefaultMessage("Withheld")
	String unknownNumber();

	@DefaultMessage("{0}, {1}")
	String area(String area, String country);

	@DefaultMessage("Unknown")
	String unknownLocation();

	@DefaultMessage("Call {0} of {1}")
	String record(int firstRecord, int callRecordCount);

	@DefaultMessage("Calls {0} to {1} of {2}")
	String records(int firstRecord, int lastRecord, int callRecordCount);
}
