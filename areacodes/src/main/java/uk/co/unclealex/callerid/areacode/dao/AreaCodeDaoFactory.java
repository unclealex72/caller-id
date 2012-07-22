/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author alex
 *
 */

package uk.co.unclealex.callerid.areacode.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.springframework.beans.factory.FactoryBean;

import uk.co.unclealex.callerid.areacode.model.AreaCode;
import uk.co.unclealex.persistence.paging.PagingService;

/**
 * A {@link FactoryBean} that creates an {@link AreaCodeDao} that persists {@link AreaCode}s in an in-memory
 * database and accesses the database using JDO.
 * @author alex
 * 
 */
public class AreaCodeDaoFactory implements FactoryBean<AreaCodeDao> {

  /**
   * The {@link PagingService} the created DAO will use for paging.
   */
  private final PagingService pagingService;
  
  /**
   * The initial {@link AreaCode}s to load.
   */
  private final Iterable<AreaCode> areaCodes;

  /**
   * Instantiates a new area code dao factory.
   * 
   * @param pagingService
   *          the paging service
   * @param areaCodes
   *          the area codes
   */
  @Inject
  public AreaCodeDaoFactory(PagingService pagingService, Iterable<AreaCode> areaCodes) {
    super();
    this.pagingService = pagingService;
    this.areaCodes = areaCodes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AreaCodeDao getObject() throws Exception {
    AreaCodeDao dao = createDao();
    for (AreaCode areaCode : getAreaCodes()) {
      dao.store(areaCode);
    }
    return dao;
  }

  /**
   * Create a new {@link AreaCodeDao}.
   * 
   * @return A new {@link AreaCodeDao}.
   */
  protected AreaCodeDao createDao() {
    Properties jdoProperties = new Properties();

    jdoProperties.put("javax.jdo.option.ConnectionURL", "jdbc:hsqldb:mem:areacodes");
    jdoProperties.put("javax.jdo.option.ConnectionDriverName", org.hsqldb.jdbc.JDBCDriver.class.getName());
    jdoProperties.put("javax.jdo.option.ConnectionUserName", "sa");
    jdoProperties.put("javax.jdo.option.ConnectionPassword", "");
    jdoProperties.put("datanucleus.autoCreateSchema", "true");
    jdoProperties.put("datanucleus.validateTables", "false");
    jdoProperties.put("datanucleus.validateConstraints", "false");

    final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(jdoProperties);
    final JdoAreaCodeDao dao = new JdoAreaCodeDao(pmf, getPagingService());
    InvocationHandler txHandler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Transaction tx = pmf.getPersistenceManager().currentTransaction();
        tx.begin();
        Object returnValue;
        try {
          returnValue = method.invoke(dao, args);
          tx.commit();
          return returnValue;
        }
        catch (Exception e) {
          tx.rollback();
          throw e;
        }
      }
    };
    return (AreaCodeDao) Proxy.newProxyInstance(
        JdoAreaCodeDao.class.getClassLoader(),
        new Class[] { AreaCodeDao.class },
        txHandler);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getObjectType() {
    return AreaCodeDao.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Gets the {@link PagingService} the created DAO will use for paging.
   * 
   * @return the {@link PagingService} the created DAO will use for paging
   */
  public PagingService getPagingService() {
    return pagingService;
  }

  /**
   * Gets the initial {@link AreaCode}s to load.
   * 
   * @return the initial {@link AreaCode}s to load
   */
  public Iterable<AreaCode> getAreaCodes() {
    return areaCodes;
  }

}
