package uk.co.unclealex.callerid.client.factory;

import uk.co.unclealex.callerid.client.presenters.CallPresenter;
import uk.co.unclealex.callerid.shared.model.CallRecord;

public interface CallPresenterFactory {

	CallPresenter createCallPresenter(CallRecord callRecord);

}
