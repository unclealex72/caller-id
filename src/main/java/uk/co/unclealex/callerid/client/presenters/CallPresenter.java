package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.presenters.CallPresenter.Display;
import uk.co.unclealex.callerid.shared.model.CallRecord;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;

import com.google.common.base.Joiner;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

public class CallPresenter implements HasDisplay<Display> {

	public static interface Display extends IsWidget {
		HasText getPhoneNumberLabel();
	}
	
	private final Display i_display;
	private final CallRecord i_callRecord;
	
	@Inject
	public CallPresenter(Display display, @Assisted CallRecord callRecord) {
		super();
		i_display = display;
		i_callRecord = callRecord;
	}

	public Display getDisplay() {
		return i_display;
	}

	public CallRecord getCallRecord() {
		return i_callRecord;
	}

	public void show(HasWidgets callRecordPanel) {
		CallRecord callRecord = getCallRecord();
		PhoneNumber phoneNumber = callRecord.getPhoneNumber();
		String text =
			Joiner.on(" - ").join(callRecord.getCallTime(),
			phoneNumber==null?"Withheld":phoneNumber.toString(),
			Joiner.on(", ").join(callRecord.getContacts()));
		Display display = getDisplay();
		display.getPhoneNumberLabel().setText(text);
		callRecordPanel.add(display.asWidget());
	}
	
	
}
