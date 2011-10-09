package uk.co.unclealex.callerid.client.factories;

import java.util.Date;

import uk.co.unclealex.callerid.client.presenters.ContactSelectionPopupPresenter;

public interface ContactSelectionPopupPresenterFactory {

	public ContactSelectionPopupPresenter createContactSelectionPopupPresenter(String[] contactNames, Date callRecordTime);
}
