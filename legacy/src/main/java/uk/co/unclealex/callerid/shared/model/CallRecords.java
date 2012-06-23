package uk.co.unclealex.callerid.shared.model;

import java.io.Serializable;
import java.util.List;

public class CallRecords implements Serializable {

	private List<CallRecord> i_callRecords;
	private int i_pageCount;
	private int i_callRecordCount;

	protected CallRecords() {
		super();
	}

	public CallRecords(List<CallRecord> callRecords, int pageCount, int callRecordCount) {
		super();
		i_callRecords = callRecords;
		i_pageCount = pageCount;
		i_callRecordCount = callRecordCount;
	}

	public List<CallRecord> getCallRecords() {
		return i_callRecords;
	}

	public int getPageCount() {
		return i_pageCount;
	}

	public int getCallRecordCount() {
		return i_callRecordCount;
	}
	
	
}
