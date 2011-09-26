package uk.co.unclealex.callerid.server.service.modem;

public interface ModemResponseListener {

	public void onResponse(String line) throws Exception;
}
