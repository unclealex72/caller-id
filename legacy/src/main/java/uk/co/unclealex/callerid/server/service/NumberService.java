package uk.co.unclealex.callerid.server.service;

public interface NumberService {

	public void onNumber(String number) throws Exception;
	public void onRing() throws Exception;

}
