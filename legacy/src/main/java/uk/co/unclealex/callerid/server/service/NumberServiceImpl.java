package uk.co.unclealex.callerid.server.service;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.phonenumber.model.PhoneNumber;
import uk.co.unclealex.callerid.phonenumber.service.PhoneNumberFactory;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.service.listener.NumberListener;

import com.google.common.collect.Lists;

@Transactional
public class NumberServiceImpl implements NumberService {

	private final static Logger log = LoggerFactory.getLogger(NumberServiceImpl.class);
	
	private List<NumberListener> i_numberListeners = Lists.newArrayList();
	private TelephoneNumberFactory i_telephoneNumberFactory;
	private PhoneNumberFactory i_numberLocationService;
	
	@Override
	public void onNumber(final String number) throws Exception {
		log.info("A call has been received from " + number==null?"UNKNOWN":number);
		final PhoneNumber phoneNumber;
		final TelephoneNumber persistedTelephoneNumber;
		if (number == null) {
			phoneNumber = null;
			persistedTelephoneNumber = null;
		}
		else {
			phoneNumber = getNumberLocationService().decomposeNumber(number);
			persistedTelephoneNumber = getTelephoneNumberFactory().findOrCreateTelephoneNumber(number);			
		}
		NumberCallback numberCallback = new NumberCallback() {
			@Override
			public boolean onEvent(NumberListener numberListener) throws Exception {
				return numberListener.onNumber(number, persistedTelephoneNumber, phoneNumber);
			}
		};
		execute(numberCallback);
	}

	@Override
	public void onRing() throws Exception {
		NumberCallback ringCallback = new NumberCallback() {
			@Override
			public boolean onEvent(NumberListener numberListener) throws Exception {
				return numberListener.onRing();
			}
		};
		execute(ringCallback);
	}

	public void execute(NumberCallback numberCallback) {
		boolean keepGoing = true;
		for (Iterator<NumberListener> iter = getNumberListeners().iterator(); keepGoing && iter.hasNext(); ) {
			try {
				keepGoing = numberCallback.onEvent(iter.next());
			}
			catch (Throwable t) {
				log.error("There was a failure during a number listener.", t);
			}
		}
	}
	
	public interface NumberCallback {
		public boolean onEvent(NumberListener numberListener) throws Exception;
	}
	
	public List<NumberListener> getNumberListeners() {
		return i_numberListeners;
	}

	public void setNumberListeners(List<NumberListener> numberListeners) {
		i_numberListeners = numberListeners;
	}

	public TelephoneNumberFactory getTelephoneNumberFactory() {
		return i_telephoneNumberFactory;
	}

	public void setTelephoneNumberFactory(TelephoneNumberFactory telephoneNumberFactory) {
		i_telephoneNumberFactory = telephoneNumberFactory;
	}

	public PhoneNumberFactory getNumberLocationService() {
		return i_numberLocationService;
	}

	public void setNumberLocationService(PhoneNumberFactory numberLocationService) {
		i_numberLocationService = numberLocationService;
	}
}
