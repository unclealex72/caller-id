package uk.co.unclealex.callerid.server.dao;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateCallRecordDao extends HibernateKeyedDao<CallRecord> implements CallRecordDao {

	@Override
	public CallRecord createExampleBean() {
		return CallRecord.example();
	}

}
