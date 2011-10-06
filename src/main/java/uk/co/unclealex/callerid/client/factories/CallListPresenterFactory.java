package uk.co.unclealex.callerid.client.factories;

import com.google.inject.assistedinject.Assisted;

import uk.co.unclealex.callerid.client.presenters.CallListPresenter;

public interface CallListPresenterFactory {

	public CallListPresenter createCallListPresenter(@Assisted("page") int page, @Assisted("callsPerPage") int callsPerPage);
}
