package uk.co.unclealex.callerid.server.service.listener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.SortedSet;

import org.springframework.web.util.UriUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.gdata.util.common.base.Joiner;

import uk.co.unclealex.callerid.server.dao.ContactDao;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.service.NumberLocationService;
import uk.co.unclealex.callerid.server.service.squeezebox.SqueezeboxConnection;
import uk.co.unclealex.callerid.server.service.squeezebox.SqueezeboxConnectionFactory;
import uk.co.unclealex.callerid.shared.model.CountriesOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.CountryAndAreaPhoneNumber;
import uk.co.unclealex.callerid.shared.model.NumberOnlyPhoneNumber;
import uk.co.unclealex.callerid.shared.model.PhoneNumber;
import uk.co.unclealex.callerid.shared.visitor.PhoneNumberVisitor;

public class SqueezeboxDisplayingNumberListener extends AbstractOnRingNumberListener implements PhoneNumberVisitor<String> {
	
	private String i_messageToDisplay;
	private SqueezeboxConnectionFactory i_squeezeboxConnectionFactory;
	private ContactDao i_contactDao;
	
	@Override
	protected boolean beforeFirstRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber)
			throws Exception {
		String contactsMessage;
		String phoneNumberMessage;
		SortedSet<Contact> contacts = telephoneNumber.getContacts();
		if (!contacts.isEmpty()) {
			Function<Contact, String> contactNumberFunction = new Function<Contact, String>() {
				@Override
				public String apply(Contact contact) {
					return contact.getName();
				}
			};
			contactsMessage = Joiner.on(", ").join(Iterables.transform(contacts, contactNumberFunction));
		}
		else {
			contactsMessage = null;
		}
		if (phoneNumber == null) {
			phoneNumberMessage = "Unknown caller";
		}
		else {
			phoneNumberMessage = phoneNumber.accept(this);
		}
		Iterable<String> messageComponents = 
				Iterables.filter(Arrays.asList(contactsMessage, phoneNumberMessage), Predicates.notNull());
		String message = Joiner.on(": ").join(messageComponents);
		setMessageToDisplay(encode(message));
		return true;
	}
	
	@Override
	public String visit(CountriesOnlyPhoneNumber countriesOnlyPhoneNumber) {
		String countryCode = countriesOnlyPhoneNumber.getCountryCode();
		String number = countriesOnlyPhoneNumber.getNumber();
		if (NumberLocationService.UK.equals(countryCode)) {
			return "0" + number;
		}
		else {
			return String.format(
				"+%s %s (%s)", countryCode, number, Joiner.on(", ").join(countriesOnlyPhoneNumber.getCountries()));
		}
	}
	
	@Override
	public String visit(CountryAndAreaPhoneNumber countryAndAreaPhoneNumber) {
		String countryCode = countryAndAreaPhoneNumber.getCountryCode();
		String areaCode = countryAndAreaPhoneNumber.getAreaCode();
		String area = countryAndAreaPhoneNumber.getArea();
		String number = countryAndAreaPhoneNumber.getNumber();
		if (NumberLocationService.UK.equals(countryCode)) {
			if (NumberLocationService.BASINGSTOKE.equals(areaCode)) {
				return number;
			}
			else {
				return String.format(
					"0%s %s (%s)", areaCode, number, area);
			}
		}
		else {
			return String.format("+%s %s%s (%s, %s)", countryCode, areaCode, number, area, countryAndAreaPhoneNumber.getCountry());
		}
	}
	
	@Override
	public String visit(NumberOnlyPhoneNumber numberOnlyPhoneNumber) {
		return numberOnlyPhoneNumber.getNumber();
	}
	
	@Override
	public String visit(PhoneNumber phoneNumber) {
		return phoneNumber.toString();
	}
	
	@Override
	protected boolean onRing(String number, TelephoneNumber telephoneNumber, PhoneNumber phoneNumber) throws IOException {
		SqueezeboxConnection conn = getSqueezeboxConnectionFactory().createSqueezeboxConnection();
		int playerCount = Integer.parseInt(conn.execute("player count ?"));
		for (int idx = 0; idx < playerCount; idx++) {
			String playerId = conn.execute(String.format("player id %d ?", idx));
			String command = String.format("%s display %s %s %d", playerId, encode("Incoming call"), getMessageToDisplay(), 15);
			conn.execute(command);
		}
		conn.close();
		return true;
	}

	protected String encode(String parameter) throws UnsupportedEncodingException {
		return UriUtils.encodeFragment(parameter, "UTF-8");
	}
	
	@Override
	protected void clearState() {
		setMessageToDisplay(null);
	}

	public String getMessageToDisplay() {
		return i_messageToDisplay;
	}

	public void setMessageToDisplay(String messageToDisplay) {
		i_messageToDisplay = messageToDisplay;
	}

	public SqueezeboxConnectionFactory getSqueezeboxConnectionFactory() {
		return i_squeezeboxConnectionFactory;
	}

	public void setSqueezeboxConnectionFactory(SqueezeboxConnectionFactory squeezeboxConnectionFactory) {
		i_squeezeboxConnectionFactory = squeezeboxConnectionFactory;
	}

	public ContactDao getContactDao() {
		return i_contactDao;
	}

	public void setContactDao(ContactDao contactDao) {
		i_contactDao = contactDao;
	}
}
