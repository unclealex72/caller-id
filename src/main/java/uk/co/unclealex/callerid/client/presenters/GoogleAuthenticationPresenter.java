/**
 * 
 */
package uk.co.unclealex.callerid.client.presenters;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.presenters.GoogleAuthenticationPresenter.Display;
import uk.co.unclealex.callerid.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.callerid.client.util.CanWait;
import uk.co.unclealex.callerid.client.util.ClickHandlerAndFailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.callerid.shared.remote.CallerIdServiceAsync;
import uk.co.unclealex.callerid.shared.service.Constants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.assistedinject.Assisted;

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
public class GoogleAuthenticationPresenter extends AbstractPopupPresenter<PopupPanel, Display> implements HasDisplay<Display> {

	public static interface Display extends AbstractPopupPresenter.Display<PopupPanel>, CanWait {
		
		HasText getSuccessCode();
		HasClickHandlers getSubmitButton();
		HasClickHandlers getCancelButton();
    Anchor getAuthenticationAnchor();
	}
	
	private final Display i_display;
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final String i_username;
	private final UsersPresenter i_usersPresenter;
	
	@Inject
	public GoogleAuthenticationPresenter(
	    Display display, AsyncCallbackExecutor asyncCallbackExecutor, 
	    UsersPresenter usersPresenter,
	    @Assisted String username) {
		super();
		i_display = display;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_username = username;
		i_usersPresenter = usersPresenter;
	}

	@Override
	protected void prepare(final Display display) {
		ClickHandler submitHandler = 
				new ClickHandlerAndFailureAsPopupExecutableAsyncCallback<Void>(getAsyncCallbackExecutor(), "Authenticating") {
			public void onSuccess(Void result) {
				getUsersPresenter().refreshUsernames();
				hide();
			}
			public void execute(CallerIdServiceAsync callerIdService, AsyncCallback<Void> callback) {
				callerIdService.addUser(getUsername(), display.getSuccessCode().getText(), callback);
			}
		};
		ClickHandler cancelHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		};
		display.getSubmitButton().addClickHandler(submitHandler);
		display.getCancelButton().addClickHandler(cancelHandler);
    display.getAuthenticationAnchor().setHref(Constants.AUTHENTICATION_URL);
		Window.open(Constants.AUTHENTICATION_URL, "google-oauth", "width=800,height=600");
	}

	public Display getDisplay() {
		return i_display;
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public String getUsername() {
		return i_username;
	}

	public UsersPresenter getUsersPresenter() {
		return i_usersPresenter;
	}
}
