package uk.co.unclealex.callerid.remote.dao;

import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.remote.model.CallRecord;
import uk.co.unclealex.callerid.remote.model.QCallRecord;
import uk.co.unclealex.persistence.jdo.JdoBasicDao;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * The JDO implementation of {@link CallRecordDao}.
 */
public class JdoCallRecordDao extends JdoBasicDao<CallRecord, QCallRecord> implements CallRecordDao {

  public JdoCallRecordDao(final PersistenceManagerFactory persistenceManagerFactory, final PagingService pagingService) {
    super(CallRecord.class, persistenceManagerFactory, pagingService);
  }

  @Override
  public QCallRecord candidate() {
    return QCallRecord.candidate();
  }

}