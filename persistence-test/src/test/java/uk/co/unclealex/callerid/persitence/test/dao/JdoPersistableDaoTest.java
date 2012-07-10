package uk.co.unclealex.callerid.persitence.test.dao;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import uk.co.unclealex.callerid.persitence.test.dao.JdoPersistableDaoTest.Context;
import uk.co.unclealex.callerid.persitence.test.model.Persistable;
import uk.co.unclealex.hbase.testing.DatanucleusContext;
import uk.co.unclealex.hbase.testing.HBaseTestContainer;
import uk.co.unclealex.hbase.testing.HBaseTestContainer.Port;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Context.class })
@Transactional
@TransactionConfiguration
public class JdoPersistableDaoTest {

  static HBaseTestContainer container = new HBaseTestContainer().withPort(Port.ZOOKEEPER_CLIENT, 2181);

  @Autowired
  PersistableDao persistableDao;
  @Autowired
  PersistenceManagerFactory persistenceManagerFactory;

  @BeforeClass
  public static void setup() throws Exception {
    container.start();
  }

  @AfterClass
  public static void teardown() {
    container.stop();
  }

  @SuppressWarnings("unchecked")
  public int countEntities() {
    return ((List<Persistable>) queryAll().execute()).size();
  }

  @Before
  public void clear() {
    queryAll().deletePersistentAll();
    Assert.assertEquals("The wrong number of persistent entities were found before a test case.", 0, countEntities());
  }

  protected Query queryAll() {
    return persistenceManagerFactory.getPersistenceManager().newQuery(Persistable.class);
  }

  @Test
  public void testStore() {
    Persistable persistable = new Persistable(1, "hello");
    persistableDao.storeOrUpdate(persistable);
    Assert.assertEquals("The wrong number of persistent object were found.", 1, countEntities());
  }

  @Test
  public void testUpdate() {
    Persistable persistable = new Persistable(1, "hello");
    persistableDao.storeOrUpdate(persistable);
    persistable.setValue("goodbye");
    persistableDao.storeOrUpdate(persistable);
    Assert.assertEquals("The wrong number of persistent object were found.", 1, countEntities());
  }

  @Test
  public void testGetAll() {
    Persistable persistableOne = new Persistable(1, "hello");
    Persistable persistableTwo = new Persistable(2, "goodbye");
    persistableDao.storeOrUpdate(persistableOne);
    persistableDao.storeOrUpdate(persistableTwo);
    Assert.assertEquals("The wrong number of persistent objects were found.", 2, countEntities());
    Persistable[] expectedPersistables = new Persistable[] { persistableOne, persistableTwo };
    List<Persistable> actualPersistables = Lists.newArrayList(persistableDao.getAll());
    Assert.assertArrayEquals(
        "The wrong persitable objects were returned.",
        expectedPersistables,
        Iterables.toArray(actualPersistables, Persistable.class));
  }

  @Test
  public void testDelete() {
    Persistable persistableOne = new Persistable(1, "hello");
    Persistable persistableTwo = new Persistable(2, "goodbye");
    persistableDao.storeOrUpdate(persistableOne);
    persistableDao.storeOrUpdate(persistableTwo);
    Assert.assertEquals("The wrong number of persistent objects were found before deletion.", 2, countEntities());
    persistableDao.delete(persistableOne);
    Assert.assertArrayEquals(
        "The wrong persitable objects were returned after deletion.",
        new Persistable[] { persistableTwo },
        Iterables.toArray(persistableDao.getAll(), Persistable.class));
  }

  @Configuration
  @ImportResource({ "classpath:application-context-persistence.xml", "classpath:application-context-persistable.xml" })
  static class Context extends DatanucleusContext {

    @Override
    public HBaseTestContainer getContainer() {
      return container;
    }
  }
}
