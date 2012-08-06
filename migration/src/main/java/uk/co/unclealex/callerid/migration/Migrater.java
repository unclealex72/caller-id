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

package uk.co.unclealex.callerid.migration;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.postgresql.Driver;

import uk.co.unclealex.callerid.calls.model.Call;
import uk.co.unclealex.callerid.google.model.OAuthTokens;
import uk.co.unclealex.callerid.server.model.CallRecord;
import uk.co.unclealex.callerid.server.model.Contact;
import uk.co.unclealex.callerid.server.model.OauthToken;
import uk.co.unclealex.callerid.server.model.OauthTokenType;
import uk.co.unclealex.callerid.server.model.TelephoneNumber;
import uk.co.unclealex.callerid.server.model.User;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A quick and dirty class to migrate data from the old caller id schema to the new.
 * @author alex
 * 
 */
public class Migrater {

  private static final Logger log = Logger.getLogger(Migrater.class.getName());

  private static final Function<TelephoneNumber, String> NUMBER_FUNCTION = new Function<TelephoneNumber, String>() {
    public String apply(TelephoneNumber telephoneNumber) {
      return telephoneNumber == null ? null : telephoneNumber.getNumber().replace("+", "00");
    }
  };
  
  public static void main(String[] args) {
    Migrater migrater =
        new Migrater(args[0], new Database(args[1], args[2], args[3]), new Database(args[4], args[5], args[6]));
    migrater.migrate();
  }

  /**
   * The host where both databases are located.
   */
  private final String host;

  /**
   * Connection information for the old database.
   */
  private final Database oldDatabase;

  /**
   * Connection information for the new database.
   */
  private final Database newDatabase;

  /**
   * @param host
   * @param oldDatabase
   * @param newDatabase
   */
  public Migrater(String host, Database oldDatabase, Database newDatabase) {
    super();
    this.host = host;
    this.oldDatabase = oldDatabase;
    this.newDatabase = newDatabase;
  }

  public void migrate() {
    PersistenceManager persistenceManager = createPersistenceManager();
    Session session = createSession();
    migrate(persistenceManager, session);
    session.close();
    persistenceManager.close();
  }

  /**
   * @return
   */
  private Session createSession() {
    Database oldDatabase = getOldDatabase();
    Properties properties = new Properties();
    properties.setProperty("hibernate.connection.driver_class", Driver.class.getName());
    properties.setProperty("hibernate.connection.url", "jdbc:postgresql://" + getHost() + ":5432/" + oldDatabase.getName());
    properties.setProperty("hibernate.connection.username", oldDatabase.getUsername());
    properties.setProperty("hibernate.connection.password", oldDatabase.getPassword());
    properties.setProperty("hibernate.dialect", PostgreSQL82Dialect.class.getName());
    properties.setProperty("hibernate.connection.pool_size", "1");
    properties.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.internal.NoCacheProvider");

    Configuration cfg = new Configuration().setProperties(properties);
    cfg.addAnnotatedClass(Contact.class)
        .addAnnotatedClass(User.class)
        .addAnnotatedClass(CallRecord.class)
        .addAnnotatedClass(TelephoneNumber.class)
        .addAnnotatedClass(OauthToken.class);
    SessionFactory sf = cfg.buildSessionFactory();
    return sf.openSession();
  }

  /**
   * @return
   */
  private PersistenceManager createPersistenceManager() {
    Database newDatabase = getNewDatabase();
    Properties properties = new Properties();
    properties.setProperty("javax.jdo.option.ConnectionDriverName", Driver.class.getName());
    properties.setProperty("javax.jdo.option.ConnectionURL", "jdbc:postgresql://" + getHost() + ":5432/" + newDatabase.getName());
    properties.setProperty("javax.jdo.option.ConnectionUserName", newDatabase.getUsername());
    properties.setProperty("javax.jdo.option.ConnectionPassword", newDatabase.getPassword());
    properties.setProperty("datanucleus.identifier.case", "LowerCase");
    properties.setProperty("datanucleus.autoCreateSchema", "true");
    properties.setProperty("datanucleus.validateTables", "false");
    properties.setProperty("datanucleus.validateConstraints", "false");
    PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(properties);
    PersistenceManager pm = pmf.getPersistenceManager();
    return pm;
  }

  /**
   * @param persistenceManager
   * @param session
   */
  public void migrate(PersistenceManager persistenceManager, Session session) {
    List<User> users = session.createCriteria(User.class).list();
    List<CallRecord> callRecords = session.createCriteria(CallRecord.class).list();
    for (CallRecord callRecord : callRecords) {
      Call call = new Call(callRecord.getCallDate(), NUMBER_FUNCTION.apply(callRecord.getTelephoneNumber()), null);
      persistenceManager.makePersistent(call);
      log.info("Imported call " + call);
    }
    for (User user : users) {
      List<OauthToken> oldOauthTokens = user.getOauthTokens();
      OAuthTokens oAuthTokens =
          new OAuthTokens(Iterables
              .find(oldOauthTokens, new TokenTypePredicate(OauthTokenType.ACCESS), null)
              .getToken(), Iterables
              .find(oldOauthTokens, new TokenTypePredicate(OauthTokenType.REFRESH), null)
              .getToken());
      List<Contact> oldContacts = session.createCriteria(Contact.class).list();
      List<uk.co.unclealex.callerid.google.model.Contact> contacts = Lists.newArrayList();
      for (Contact oldContact : oldContacts) {
        List<String> telephoneNumbers = Lists.newArrayList(Iterables.transform(oldContact.getTelephoneNumbers(), NUMBER_FUNCTION));
        contacts.add(new uk.co.unclealex.callerid.google.model.Contact(oldContact.getName(), telephoneNumbers));
      }
      uk.co.unclealex.callerid.google.model.User googleUser =
          new uk.co.unclealex.callerid.google.model.User(user.getUsername(), oAuthTokens, contacts);
      persistenceManager.makePersistent(googleUser);
    }
  }

  class TokenTypePredicate implements Predicate<OauthToken> {
    OauthTokenType oauthTokenType;

    /**
     * @param oauthTokenType
     */
    public TokenTypePredicate(OauthTokenType oauthTokenType) {
      super();
      this.oauthTokenType = oauthTokenType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(OauthToken oauthToken) {
      return oauthTokenType == oauthToken.getTokenType();
    }

  }

  public String getHost() {
    return host;
  }

  public Database getOldDatabase() {
    return oldDatabase;
  }

  public Database getNewDatabase() {
    return newDatabase;
  }

}
