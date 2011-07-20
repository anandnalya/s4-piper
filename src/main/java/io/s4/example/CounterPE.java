/*
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License. See accompanying LICENSE file. 
 */
package io.s4.example;

import io.s4.App;
import io.s4.Event;
import io.s4.ProcessingElement;
import io.s4.Stream;

public class CounterPE extends ProcessingElement {

    final private Stream<CountEvent> countStream;
    
    public CounterPE(App app, Stream<CountEvent> countStream) {
        super(app);
        this.countStream = countStream;
    }

    private long counter = 0;

	/* (non-Javadoc)
	 * @see io.s4.ProcessingElement#processInputEvent(io.s4.Event)
	 */
	@Override
	protected void processInputEvent(Event event) {

	    counter += 1;
	}

	/* (non-Javadoc)
	 * @see io.s4.ProcessingElement#sendOutputEvent()
	 */
	@Override
	public void sendEvent() {
		
        CountEvent countEvent = new CountEvent(countStream.getName(), counter);
        countStream.put(countEvent);
	}

	/* (non-Javadoc)
	 * @see io.s4.ProcessingElement#init()
	 */
	@Override
	protected void initPEInstance() {
		// TODO Auto-generated method stub
		
	}
}