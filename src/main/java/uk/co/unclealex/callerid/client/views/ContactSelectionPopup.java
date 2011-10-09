/**
 * 
 */
package uk.co.unclealex.callerid.client.views;

import java.util.Arrays;

import javax.inject.Inject;

import uk.co.unclealex.callerid.client.presenters.ContactSelectionPopupPresenter.Display;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
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
public class ContactSelectionPopup extends SimplePanel implements Display {

	@UiTemplate("ContactSelectionPopup.ui.xml")
	public interface Binder extends UiBinder<Widget, ContactSelectionPopup> {
    // No extra method
  }
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField DialogBox popupPanel;
	@UiField Button submitButton;
	@UiField SuggestBox contactName;
	private final String[] i_suggestions;
	
	@Inject
	public ContactSelectionPopup(@Assisted String[] suggestions) {
		i_suggestions = suggestions;
		add(binder.createAndBindUi(this));
	}

	@UiFactory
	public SuggestBox createSuggestBox() {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.addAll(Arrays.asList(getSuggestions()));
		return new SuggestBox(oracle);
	}
	
	public Button getSubmitButton() {
		return submitButton;
	}

	public DialogBox getPopupPanel() {
		return popupPanel;
	}

	public String[] getSuggestions() {
		return i_suggestions;
	}

	public HasText getContactName() {
		return contactName;
	}
}
