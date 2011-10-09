package uk.co.unclealex.callerid.client.factories;

import uk.co.unclealex.callerid.client.presenters.ContactSelectionPopupPresenter;

public interface ContactSelectionPopupDisplayFactory {

	public ContactSelectionPopupPresenter.Display createContactSelectionPopupDisplay(String[] suggestions);
}
