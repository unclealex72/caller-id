package uk.co.unclealex.callerid.server.service;

import uk.co.unclealex.callerid.shared.model.PhoneNumber;

public interface NumberLocationService {

	public String BASINGSTOKE = "1256";
	public String UK = "44";

	public PhoneNumber decomposeNumber(String number);
	public String normaliseNumber(String number);

}
