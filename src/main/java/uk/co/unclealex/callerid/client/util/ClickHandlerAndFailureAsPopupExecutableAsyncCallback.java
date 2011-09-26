/**
 * 
 */
package uk.co.unclealex.callerid.client.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


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
public abstract class ClickHandlerAndFailureAsPopupExecutableAsyncCallback<T> extends FailureAsPopupExecutableAsyncCallback<T> implements ClickHandler {

	private final String i_message;
	private final CanWait[] i_canWaits;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	
	public ClickHandlerAndFailureAsPopupExecutableAsyncCallback(
			AsyncCallbackExecutor asyncCallbackExecutor, String message, CanWait... canWaits) {
		super();
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_message = message;
		i_canWaits = canWaits;
	}

	@Override
	public final void onClick(ClickEvent event) {
		getAsyncCallbackExecutor().executeAndWait(this, getMessage(), getCanWaits());
	}
	
	public void onSuccess(T result) {
		// Default is to do nothing.
	}
	
	public String getMessage() {
		return i_message;
	}

	public CanWait[] getCanWaits() {
		return i_canWaits;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}
	
	
}
