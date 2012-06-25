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
 * @author unclealex72
 *
 */

package uk.co.unclealex.callerid.phonenumber.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import uk.co.unclealex.callerid.phonenumber.model.TelephoneNumber;

/**
 * @author alex
 * 
 */
@Transactional
public class JdoTelephoneNumberDao implements TelephoneNumberDao {

	/**
	 * The JDO {@link PersistenceManagerFactory} used to get
	 * {@link PersistenceManager}s.
	 * 
	 */
	private final PersistenceManagerFactory persistenceManagerFactory;

	public JdoTelephoneNumberDao(PersistenceManagerFactory persistenceManagerFactory) {
		super();
		this.persistenceManagerFactory = persistenceManagerFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TelephoneNumber findById(String id) {
		Map<String, String> parameters = Collections.singletonMap("id", id);
		return findUniqueByParameters(parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TelephoneNumber findByNumber(String internationalPrefix, String stdCode, String number) {
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("internationalPrefix", internationalPrefix);
		parameters.put("stdCode", stdCode);
		parameters.put("number", number);
		return findUniqueByParameters(parameters);
	}

	public TelephoneNumber findUniqueByParameters(Map<String, String> parameters) {
		Query query = getPersistenceManager().newQuery(TelephoneNumber.class);
		Set<String> parameterNames = parameters.keySet();
		query.setFilter(Joiner.on(" && ").join(Iterables.transform(parameterNames, new FilterFunction())));
		query.declareParameters(Joiner.on(", ").join(Iterables.transform(parameterNames, new DeclarationFunction())));
		@SuppressWarnings("unchecked")
		List<TelephoneNumber> telephoneNumbers =
				(List<TelephoneNumber>) query.executeWithMap(parameters);
		return telephoneNumbers.isEmpty() ? null : telephoneNumbers.get(0);
	}

	static class FilterFunction implements Function<String, String> {
		
		@Override
		public String apply(String variable) {
			return String.format("%s == %s", variable, variable);
		}
	}
	
	static class DeclarationFunction implements Function<String, String> {
		
		@Override
		public String apply(String variable) {
			return String.format("String %s", variable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(TelephoneNumber telephoneNumber) {
		getPersistenceManager().makePersistent(telephoneNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(String id) {
		TelephoneNumber telephoneNumber = findById(id);
		if (telephoneNumber != null) {
			getPersistenceManager().deletePersistent(telephoneNumber);
		}
	}

	/**
	 * Get the JDO {@link PersistenceManager}.
	 * @return The JDO {@link PersistenceManager}
	 */
	public PersistenceManager getPersistenceManager() {
		return getPersistenceManagerFactory().getPersistenceManager();
	}

	public PersistenceManagerFactory getPersistenceManagerFactory() {
		return persistenceManagerFactory;
	}

}
