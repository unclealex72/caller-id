package uk.co.unclealex.callerid.server.dao;

import java.util.Date;
import java.util.List;

import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.hibernate.dao.KeyedDao;

public interface CallRecordDao extends KeyedDao<CallRecord> {

	public List<CallRecord> getCallRecords(int page, int pageSize);

	public CallRecord findByTime(Date callRecordTime);
}