package uk.co.unclealex.callerid.server.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.hibernate.dao.HibernateKeyedDao;

@Transactional
public class HibernateCallRecordDao extends HibernateKeyedDao<CallRecord> implements CallRecordDao {

	@Override
	public List<CallRecord> getCallRecords(int page, int pageSize) {
		Criteria c = createCriteria(createExampleBean());
		c.addOrder(Order.desc("callDate"));
		c.setFirstResult(page * pageSize);
		c.setMaxResults(pageSize);
		return asList(c);
	}
	
	@Override
	public CallRecord createExampleBean() {
		return CallRecord.example();
	}

}
