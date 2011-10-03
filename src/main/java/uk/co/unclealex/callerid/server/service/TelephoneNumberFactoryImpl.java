package uk.co.unclealex.callerid.server.service;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.dao.TelephoneNumberDao;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;

@Transactional
public class TelephoneNumberFactoryImpl implements TelephoneNumberFactory {

	private NumberLocationService i_numberLocationService;
	private TelephoneNumberDao i_telephoneNumberDao;
	
	@Override
	public TelephoneNumber findOrCreateTelephoneNumber(String number) {
		number = getNumberLocationService().normaliseNumber(number);
		TelephoneNumberDao telephoneNumberDao = getTelephoneNumberDao();
		TelephoneNumber telephoneNumber = telephoneNumberDao.findByNumber(number);
		if (telephoneNumber == null) {
			telephoneNumber = new TelephoneNumber(number, false);
			telephoneNumberDao.store(telephoneNumber);
		}
		return telephoneNumber;
	}

	public NumberLocationService getNumberLocationService() {
		return i_numberLocationService;
	}

	public void setNumberLocationService(NumberLocationService numberLocationService) {
		i_numberLocationService = numberLocationService;
	}

	public TelephoneNumberDao getTelephoneNumberDao() {
		return i_telephoneNumberDao;
	}

	public void setTelephoneNumberDao(TelephoneNumberDao telephoneNumberDao) {
		i_telephoneNumberDao = telephoneNumberDao;
	}

}
