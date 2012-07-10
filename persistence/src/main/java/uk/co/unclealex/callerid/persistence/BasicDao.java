package uk.co.unclealex.callerid.persistence;

import java.util.List;

/**
 * A interface for simple DAOs that can create, update and delete an object and
 * also return a list of all objects.
 * 
 * @author alex
 * 
 */
public interface BasicDao<E> {

  /**
   * Create or update an entity.
   * @param entity The entity to create or update.
   * @return The entity with any added persistent information.
   */
  public E storeOrUpdate(E entity);
  
  /**
   * Remove an entity.
   * @param entity The entity to remove.
   */
  public void delete(E entity);
  
  /**
   * Get all persisted entities.
   * @return All currently persisted entities.
   */
  public List<E> getAll();
}
