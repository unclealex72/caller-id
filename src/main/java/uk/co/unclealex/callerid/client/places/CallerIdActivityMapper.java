/**
 * 
 */
package uk.co.unclealex.callerid.client.places;

import javax.inject.Inject;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

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
public class CallerIdActivityMapper implements ActivityMapper {

	@Inject
	public CallerIdActivityMapper() {
	}

	@Override
	public Activity getActivity(Place place) {
		return new ActivityProvider(place).asActivity();
	}
	
	protected class ActivityProvider implements CallerIdPlaceVisitor<Activity>, Activity {

		private final Place i_place;
		private Activity i_activity;
		
		public ActivityProvider(Place place) {
			super();
			i_place = place;
		}

		public Activity asActivity() {
			Place place = getPlace();
			if (place instanceof CallerIdPlace) {
				Activity activity = ((CallerIdPlace) place).accept(this);
				setActivity(activity);
			}
			return this;
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ActivityProvider) && (getPlace().equals(((ActivityProvider) obj).getPlace()));
		}
		
		@Override
		public String mayStop() {
			return getActivity().mayStop();
		}

		@Override
		public void onCancel() {
			getActivity().onCancel();
		}

		@Override
		public void onStop() {
			getActivity().onStop();
		}

		@Override
		public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
			final Activity activity = getActivity();
			activity.start(panel, eventBus);
		}

		@Override
		public Activity visit(CallerIdPlace callerIdPlace) {
			return null;
		}
		
		@Override
		public Activity visit(CallListPlace callListPlace) {
			return null;
		}
		
		public Activity getActivity() {
			return i_activity;
		}

		public void setActivity(Activity activity) {
			i_activity = activity;
		}

		public Place getPlace() {
			return i_place;
		}

	}
}
