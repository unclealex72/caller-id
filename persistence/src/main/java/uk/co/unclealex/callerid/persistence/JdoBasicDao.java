package uk.co.unclealex.callerid.persistence;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.mysema.query.jdo.JDOQLQuery;
import com.mysema.query.jdo.JDOQLQueryImpl;
import com.mysema.query.types.EntityPath;

/**
 * The JDO implementation of {@link BasicDao}.
 * @author alex
 *
 * @param <E> The type of persitent entity 
 * @param <Q> The type of the QueryDSL generated type for the persitent entity.
 */
public abstract class JdoBasicDao<E, Q extends EntityPath<E>> implements BasicDao<E> {

  /**
   * The {@link PersistenceManagerFactory} used to get {@link PersistenceManager}s.
   */
  private final PersistenceManagerFactory persistenceManagerFactory;

  
  /**
   * @param persistenceManagerFactory
   */
  public JdoBasicDao(PersistenceManagerFactory persistenceManagerFactory) {
    super();
    this.persistenceManagerFactory = persistenceManagerFactory;
  }

  @Override
  public E storeOrUpdate(E entity) {
    return getPersistenceManager().makePersistent(entity);
  }
  
  @Override
  public void delete(E entity) {
    getPersistenceManager().deletePersistent(entity);
  }
  
  @Override
  public List<E> getAll() {
    QueryCallback<List<E>> callback = new QueryCallback<List<E>>() {
      @Override
      public List<E> doInQuery(JDOQLQuery query) {
        Q candidate = candidate();
        return query.from(candidate).list(candidate);
      }
    };
    return execute(callback);
  }

  /**
   * An interface for allowing queries to be executed without having to worry about closing them.
   * @author alex
   *
   * @param <V> The value type to return.
   */
  protected static interface QueryCallback<V> {
    public V doInQuery(JDOQLQuery query);
  }

  /**
   * A helper class for query callbacks that return a unique value.
   * @author alex
   *
   */
  protected abstract class UniqueQueryCallback implements QueryCallback<E> {};
  
  /**
   * A helper class for query callbacks that return a list of values.
   * @author alex
   *
   */
  protected abstract class ListQueryCallback implements QueryCallback<List<E>> {};

  /**
   * Execute a {@link QueryCallback} and make sure that all resources are closed.
   * @param callback The callback to execute.
   * @return The result returned in the callback.
   */
  protected <V> V execute(QueryCallback<V> callback) {
    JDOQLQuery query = new JDOQLQueryImpl(getPersistenceManager());
    try {
      return callback.doInQuery(query);
    }
    finally {
      query.close();
    }
  }
  
  /**
   * Get the default QueryDSL generated candidate.
   * @return The default QueryDSL generated candidate.
   */
  public abstract Q candidate();

  public PersistenceManager getPersistenceManager() {
    return getPersistenceManagerFactory().getPersistenceManager();
  }
  
  public PersistenceManagerFactory getPersistenceManagerFactory() {
    return persistenceManagerFactory;
  }
}
