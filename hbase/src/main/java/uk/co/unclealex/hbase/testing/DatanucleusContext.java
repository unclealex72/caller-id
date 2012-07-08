package uk.co.unclealex.hbase.testing;

import java.util.Map;
import java.util.Properties;

import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jdo.LocalPersistenceManagerFactoryBean;

import uk.co.unclealex.hbase.testing.HBaseTestContainer.Port;

/**
 * A Spring {@link Configuration} bean that allows Datanucleus to be set up
 * programatically and reference a {@link HBaseTestContainer}.
 * 
 * @author alex
 * 
 */
public abstract class DatanucleusContext {

  @Bean
  public FactoryBean<PersistenceManagerFactory> rawPersistenceManagerFactory() {
    Properties properties = new Properties();
    properties.put("javax.jdo.PersistenceManagerFactoryClass", JDOPersistenceManagerFactory.class.getName());
    Map<Port, Integer> ports = getContainer().getPorts();
    properties.put("javax.jdo.option.ConnectionURL", "hbase:localhost:" + ports.get(Port.MASTER));
    properties.put("datanucleus.autoCreateSchema", "true");
    properties.put("datanucleus.validateTables", "false");
    properties.put("datanucleus.validateConstraints", "false");
    properties.put("datanucleus.hbase.wotnot.validateConstraints", "false");
    LocalPersistenceManagerFactoryBean factoryBean = new LocalPersistenceManagerFactoryBean();
    factoryBean.setJdoProperties(properties);
    return factoryBean;
  }

  /**
   * Get the static instance of the {@link HBaseTestContainer}.
   * 
   * @return The static instance of the {@link HBaseTestContainer}.
   */
  public abstract HBaseTestContainer getContainer();

}
