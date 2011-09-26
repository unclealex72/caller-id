package uk.co.unclealex.callerid.client.presenters;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter<D extends IsWidget> extends HasDisplay<D> {

	void show(AcceptsOneWidget container);
}
