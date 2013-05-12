/**
 * Copyright 2013 Alex Jones
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
 * @author unclealex72
 *
 */

/**
 * @author alex
 *
 */
import play.api.GlobalSettings
import module.DefaultModule
import com.google.inject.Guice
import org.squeryl.adapters.{ H2Adapter, PostgreSqlAdapter }
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{ Session, SessionFactory }
import play.api.db.DB
import play.api.GlobalSettings
import uk.co.unclealex.callerid.remote.model.CallerIdSchema._
import uk.co.unclealex.callerid.remote.model.CallerIdSchema
import play.api.Application
import org.pac4j.oauth.client.Google2Client
import org.pac4j.http.client.BasicAuthClient
import org.pac4j.http.credentials.UsernamePasswordAuthenticator
import org.pac4j.http.credentials.UsernamePasswordCredentials
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.client.Clients
import org.pac4j.play.Config
object Global extends GlobalSettings {

  // Guice
  private lazy val injector = Guice.createInjector(new DefaultModule)

  // Guice
  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }

  override def onStart(app: Application) {

    def propertyExtractor: String => String => String =
      prefix => key => {
        val fullKey = s"$prefix.$key"
        app.configuration.getString(fullKey).getOrElse(
          throw new IllegalArgumentException(s"Cannot find configuration option $fullKey"))
      }

    // Security
    val googleClient = {
      def googleProperty = propertyExtractor("google")
      new Google2Client(googleProperty("consumerId"), googleProperty("consumerSecret"))
    }
    val basicHttpClient = {
      def basicHttpProperty = propertyExtractor("security")
      val username = basicHttpProperty("username")
      val password = basicHttpProperty("password")
      val authenticator = new UsernamePasswordAuthenticator() {
        def validate(credentials: UsernamePasswordCredentials) = {
          if (credentials.getUsername() != username || credentials.getPassword() != password) {
            throw new CredentialsException("Please go away.")
          }
        }
      }
      new BasicAuthClient(authenticator)
    }
    val clients = new Clients(propertyExtractor("google")("callback"), basicHttpClient, googleClient)
    Config.setClients(clients)

    // Set up Squeryl database access
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("org.postgresql.Driver") => Some(() => getSession(new PostgreSqlAdapter, app))
      case _ => sys.error("Database driver must be either org.h2.Driver or org.postgresql.Driver")
    }
  }

  def getSession(adapter: DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)

}