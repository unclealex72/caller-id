package uk.co.unclealex.callerid.persitence.test.dao;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import uk.co.unclealex.callerid.persistence.JdoBasicDao;
import uk.co.unclealex.callerid.persitence.test.model.Persistable;
import uk.co.unclealex.callerid.persitence.test.model.QPersistable;

import com.mysema.query.jdo.JDOQLQuery;

/**
 * An extension of {@link JdoBasicDao} for testing.
 * @author alex
 *
 */
public class JdoPersistableDao extends JdoBasicDao<Persistable, QPersistable> implements PersistableDao {

  public JdoPersistableDao(PersistenceManagerFactory persistenceManagerFactory) {
    super(persistenceManagerFactory);
  }

  @Override
  public List<Persistable> getByValue(final String value) {
    QueryCallback<List<Persistable>> callback = new QueryCallback<List<Persistable>>() {
      @Override
      public List<Persistable> doInQuery(JDOQLQuery query) {
        QPersistable candidate = candidate();
        return query.from(candidate).where(candidate.value.eq(value)).list(candidate);
      }
    };
    return execute(callback);
  }
  
  @Override
  public QPersistable candidate() {
    return QPersistable.persistable;
  }

}
