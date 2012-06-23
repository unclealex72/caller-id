/**
 * 
 */
package uk.co.unclealex.callerid.server.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.co.unclealex.callerid.shared.exceptions.GoogleAuthenticationFailedException;
import uk.co.unclealex.callerid.shared.model.CallRecords;
import uk.co.unclealex.callerid.shared.remote.CallerIdService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Copyright 2011 Alex Jones
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
public class CallerIdServlet extends RemoteServiceServlet implements CallerIdService {

	private BeanFactory i_beanFactory;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		setBeanFactory(applicationContext);
	}

	@Override
	protected String readContent(HttpServletRequest request) throws ServletException, IOException {
		request.getSession();
		return super.readContent(request);
	}

	protected CallerIdService createCallerIdService() {
		final CallerIdService callerIdService = getBeanFactory().getBean(CallerIdService.class);
		final Logger log = LoggerFactory.getLogger(getClass());
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(callerIdService, args);
				}
				catch (InvocationTargetException e) {
					Throwable targetException = e.getTargetException();
					log.error(method.getName(), targetException);
					throw targetException;
				}
				catch (Throwable t) {
					log.error(method.getName(), t);
					throw t;
				}
			}
		};
		return (CallerIdService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { CallerIdService.class }, handler);
	}
	
	@Override
	public void addUser(String username, String token) throws IOException, GoogleAuthenticationFailedException {
		createCallerIdService().addUser(username, token);
	}
	
	@Override
	public void removeUser(String username) {
		createCallerIdService().removeUser(username);
	}
	
	@Override
	public String[] getAllUsernames() {
		return createCallerIdService().getAllUsernames();
	}

	@Override
	public void updateContacts() throws GoogleAuthenticationFailedException, IOException {
		createCallerIdService().updateContacts();
	}

	@Override
	public CallRecords getAllCallRecords(int page, int callsPerPage) {
		return createCallerIdService().getAllCallRecords(page, callsPerPage);
	}
	
	@Override
	public String[] getAllContactNames() {
		return createCallerIdService().getAllContactNames();
	}
	
	@Override
	public void associateCallRecordToContactName(Date callRecordTime, String contactName) {
		createCallerIdService().associateCallRecordToContactName(callRecordTime, contactName);
	}
	
	@Override
	public void removeContact(String name) {
		createCallerIdService().removeContact(name);
	}
	
	public BeanFactory getBeanFactory() {
		return i_beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		i_beanFactory = beanFactory;
	}

}
