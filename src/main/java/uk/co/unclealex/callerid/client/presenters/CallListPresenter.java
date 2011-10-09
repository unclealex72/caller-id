package uk.co.unclealex.callerid.client.presenters;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.factories.ContactSelectionPopupPresenterFactory;
import uk.co.unclealex.callerid.client.messages.CallerIdMessages;
import uk.co.unclealex.callerid.client.places.CallListPlace;
import uk.co.unclealex.callerid.client.presenters.CallListPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.ClickHandlerAndFailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.callerid.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.model.CallRecord;
import uk.co.unclealex.callerid.shared.model.CallRecordContact;
import uk.co.unclealex.callerid.shared.model.CallRecords;
import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;
import uk.co.unclealex.callerid.shared.service.PhoneNumberFormatter;
import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;

public class CallListPresenter extends AbstractActivity implements HasDisplay<Display>, PhoneNumberVisitor<Widget> {

	public static interface Display extends IsWidget {
		String HEADER_CLASS = "callrecordheader";
		FlexTable getCallRecordTable();
		HasWidgets getPagesPanel();
		HasText getResultCountLabel();
	}
	
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final int i_page;
	private final int i_callsPerPage;
	private final CallerIdMessages i_callerIdMessages;
	private final PhoneNumberFormatter i_phoneNumberFormatter;
	private final PlaceController i_placeController;
	private final ContactSelectionPopupPresenterFactory i_contactSelectionPopupPresenterFactory;

	@Inject
	public CallListPresenter(
		Display display, AsyncCallbackExecutor asyncCallbackExecutor,
		CallerIdMessages callerIdMessages, PhoneNumberFormatter phoneNumberFormatter,
		PlaceController placeController, ContactSelectionPopupPresenterFactory contactSelectionPopupPresenterFactory,
		@Assisted("page") int page, @Assisted("callsPerPage") int callsPerPage) {
		super();
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_page = page;
		i_callsPerPage = callsPerPage;
		i_callerIdMessages = callerIdMessages;
		i_phoneNumberFormatter = phoneNumberFormatter;
		i_placeController = placeController;
		i_contactSelectionPopupPresenterFactory = contactSelectionPopupPresenterFactory;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		ExecutableAsyncCallback<CallRecords> callback = new FailureAsPopupExecutableAsyncCallback<CallRecords>() {
			@Override
			public void onSuccess(CallRecords callRecords) {
				start(panel, callRecords);
			}
			@Override
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<CallRecords> callback) {
				callerIdService.getAllCallRecords(getPage(), getCallsPerPage(), callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	protected void start(AcceptsOneWidget panel, CallRecords callRecords) {
		Display display = getDisplay();
		populateTable(callRecords, display);
		populateTitle(callRecords, display);
		populatePageLinks(callRecords, display);
		panel.setWidget(display);
	}

	protected void populatePageLinks(CallRecords callRecords, Display display) {
		HasWidgets pagesPanel = display.getPagesPanel();
		int pageCount = callRecords.getPageCount();
		int currentPage = getPage();
		for (int page = 0; page < pageCount; page++) {
			Widget pageWidget;
			String pageDisplayText = Integer.toString(page + 1);
			if (page == currentPage) {
				pageWidget = new InlineLabel(pageDisplayText);
			}
			else {
				Anchor pageLink = new Anchor(pageDisplayText);
				final int newPage = page;
				ClickHandler handler = new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						getPlaceController().goTo(new CallListPlace(newPage, getCallsPerPage()));
					}
				};
				pageLink.addClickHandler(handler);
				pageWidget = pageLink;
			}
			pagesPanel.add(pageWidget);
		}
	}

	protected void populateTitle(CallRecords callRecords, Display display) {
		int page = getPage();
		int callsPerPage = getCallsPerPage();
		int firstRecord = page * callsPerPage + 1;
		int callRecordCount = callRecords.getCallRecordCount();
		int lastRecord = Math.min((page + 1) * callsPerPage, callRecordCount);
		String title = firstRecord == lastRecord?
				getCallerIdMessages().record(firstRecord, callRecordCount):
				getCallerIdMessages().records(firstRecord, lastRecord, callRecordCount);
		display.getResultCountLabel().setText(title);
	}

	protected void populateTable(CallRecords callRecords, Display display) {
		FlexTable callRecordTable = display.getCallRecordTable();
		Date previousCallTime = null;
		final PhoneNumberFormatter phoneNumberFormatter = getPhoneNumberFormatter();
		CallerIdMessages callerIdMessages = getCallerIdMessages();
		String unknownNumber = callerIdMessages.unknownNumber();
		int row = 0;
		FlexCellFormatter flexCellFormatter = callRecordTable.getFlexCellFormatter();
		for (final CallRecord callRecord : callRecords.getCallRecords()) {
			Date callTime = callRecord.getCallTime();
			if (notSameDay(previousCallTime, callTime)) {
				callRecordTable.setText(row, 0, callerIdMessages.callDate(callTime));
				flexCellFormatter.setColSpan(row, 0, 3);
				flexCellFormatter.addStyleName(row, 0, Display.HEADER_CLASS);
				row++;
			}
			previousCallTime = callTime;
			callRecordTable.setText(row, 0, callerIdMessages.callTime(callTime));
			final PhoneNumber phoneNumber = callRecord.getPhoneNumber();
			if (phoneNumber != null) {
				SortedSet<CallRecordContact> contacts = callRecord.getContacts();
				final String prettyPrintedNumber = phoneNumberFormatter.prettyPrintNumber(phoneNumber);
				if (contacts.isEmpty()) {
					final Anchor phoneSearchLink = new Anchor(prettyPrintedNumber);
					ClickHandler searchClickHandler = 
						new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<String[]>(
								getAsyncCallbackExecutor(), "Finding all contact names.") {
						@Override
						public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<String[]> callback) {
							callerIdService.getAllContactNames(callback);
						}
						@Override
						public void onSuccess(String[] contactNames) {
							ContactSelectionPopupPresenter contactSelectionPopupPresenter = 
									getContactSelectionPopupPresenterFactory().createContactSelectionPopupPresenter(
											contactNames, callRecord.getCallTime());
							contactSelectionPopupPresenter.showRelativeTo(phoneSearchLink);
						}
					};
					phoneSearchLink.setHref("http://www.google.com/search?q=" + phoneNumberFormatter.formatForSearch(phoneNumber));
					phoneSearchLink.setTarget("_blank");
					phoneSearchLink.addClickHandler(searchClickHandler);
					callRecordTable.setWidget(row, 1, phoneSearchLink);
					callRecordTable.setWidget(row, 2, phoneNumber.accept(this));
				}
				else {
					callRecordTable.setText(row, 1, prettyPrintedNumber);
					callRecordTable.setWidget(row, 2, listContacts(contacts));
				}
			}
			else {
				callRecordTable.setText(row, 1, unknownNumber);
			}
			row++;
		}
	}

	protected Widget listContacts(SortedSet<CallRecordContact> googleContacts) {
		CallRecordContact lastContact = googleContacts.last();
		SortedSet<CallRecordContact> allButLastContact = googleContacts.headSet(lastContact);
		Widget lastContactWidget = asContactWidget(lastContact);
		if (allButLastContact.isEmpty()) {
			return lastContactWidget;
		}
		Panel contactsPanel = new FlowPanel();
		boolean isNotFirstElement = false;
		CallerIdMessages callerIdMessages = getCallerIdMessages();
		for (CallRecordContact contact : allButLastContact) {
			if (isNotFirstElement) {
				contactsPanel.add(new InlineLabel(callerIdMessages.separator()));
			}
			isNotFirstElement = true;
			contactsPanel.add(asContactWidget(contact));
		}
		contactsPanel.add(new InlineLabel(callerIdMessages.finalSeparator()));
		contactsPanel.add(lastContactWidget);
		return contactsPanel;
	}
	
	protected Widget asContactWidget(CallRecordContact contact) {
		final String name = contact.getName();
		if (contact.isGoogleContact()) {
			return new InlineLabel(name);
		}
		else {
			Anchor contactLink = new Anchor(name);
			ClickHandler removeContactClickHandler =
					new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<Void>(
							getAsyncCallbackExecutor(), "Removing contact " + name) {
				public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<Void> callback) {
					if (Window.confirm(getCallerIdMessages().removeContactConfirm(name))) {
						callerIdService.removeContact(name, callback);
					}
				}
				public void onSuccess(Void result) {
					Window.Location.reload();
				}
			};
			contactLink.addClickHandler(removeContactClickHandler);
			return contactLink;
		}
	}

	protected String or(Iterable<String> iterable) {
		List<String> elements = Lists.newArrayList(iterable);
		int lastIndex = elements.size() - 1;
		String lastElement = elements.get(lastIndex);
		if (lastIndex == 0) {
			return lastElement;
		}
		else {
			List<String> allButLastElement = elements.subList(0, lastIndex);
			CallerIdMessages callerIdMessages = getCallerIdMessages();
			return Joiner.on(callerIdMessages.finalSeparator()).join(
					Joiner.on(callerIdMessages.separator()).join(allButLastElement), lastElement);
		}
	}

	@Override
	public Widget visit(PhoneNumber phoneNumber) {
		return null;
	}
	
	@Override
	public Widget visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
		List<String> countries = countriesOnlyPhoneNumber.getCountries();
		Widget label = new InlineLabel(countries.get(0));
		if (countries.size() > 1) {
			label.setTitle(or(countries));
		}
		return label;
	}
	
	@Override
	public Widget visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
		String text = getCallerIdMessages().area(countryAndAreaPhoneNumber.getArea(), countryAndAreaPhoneNumber.getCountry());
		return new InlineLabel(text);
	}
	
	@Override
	public Widget visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
		return new InlineLabel(getCallerIdMessages().unknownLocation());
	}
	
	@SuppressWarnings("deprecation")
	protected boolean notSameDay(Date previousCallTime, Date callTime) {
		return previousCallTime == null || 
		callTime.getDate() != previousCallTime.getDate() || 
		callTime.getMonth() != previousCallTime.getMonth() || 
		callTime.getYear() != previousCallTime.getYear();
	}

	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public int getPage() {
		return i_page;
	}

	public int getCallsPerPage() {
		return i_callsPerPage;
	}

	public CallerIdMessages getCallerIdMessages() {
		return i_callerIdMessages;
	}

	public PhoneNumberFormatter getPhoneNumberFormatter() {
		return i_phoneNumberFormatter;
	}

	public PlaceController getPlaceController() {
		return i_placeController;
	}

	public ContactSelectionPopupPresenterFactory getContactSelectionPopupPresenterFactory() {
		return i_contactSelectionPopupPresenterFactory;
	}
}
