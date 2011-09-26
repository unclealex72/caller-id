/**
 * 
 */
package uk.co.unclealex.callerid.client.gin;

import javax.inject.Singleton;

import uk.co.unclealex.callerid.client.presenters.HasDisplay;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.user.client.ui.IsWidget;

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
public class CallerIdClientModule extends AbstractGinModule {

	@Override
	protected void configure() {
		
	}

	protected <D extends IsWidget, P extends HasDisplay<D>> void bindPresenterWithDisplay(
			Class<D> displayInterface, Class<? extends D> displayImplementation, 
			Class<P> presenterImplementation, Class<?> factoryInterface) {
    bindDisplay(displayInterface, displayImplementation);
    bindPresenter(presenterImplementation, factoryInterface);

	}

	protected <D extends IsWidget, P extends HasDisplay<D>> void bindSingletonPresenter(
			Class<P> presenterClass, Class<D> displayClass, Class<? extends D> displayImplementationClass) {
		bind(presenterClass).in(Singleton.class);
		bind(displayClass).to(displayImplementationClass).in(Singleton.class);
	}
	
	protected <P> void bindPresenter(Class<P> presenterImplementation, Class<?> factoryInterface) {
		install(new GinFactoryModuleBuilder().implement(presenterImplementation, presenterImplementation).
        build(factoryInterface));
	}

	protected <D> void bindDisplay(Class<D> displayInterface, Class<? extends D> displayImplementation) {
		bind(displayInterface).to(displayImplementation);
	}
}
