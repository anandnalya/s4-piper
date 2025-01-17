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
package io.s4.core;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stream<T extends Event> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Stream.class);

    final static private String DEFAULT_SEPARATOR = "^";
    final static private int CAPACITY = 1000;
    final private String name;
    final private Key<T> key;
    final private ProcessingElement[] targetPEs;
    final private BlockingQueue<T> queue = new ArrayBlockingQueue<T>(CAPACITY);
    final private Thread thread;

    /*
     * Streams send event of a given type using a specific key to target
     * processing elements.
     */
    public Stream(App app, String name, KeyFinder<T> finder,
            ProcessingElement... processingElements) {

        app.addStream(this);
        this.name = name;

        if (finder == null) {
            this.key = null;
        } else {
            this.key = new Key<T>(finder, DEFAULT_SEPARATOR);
        }
        this.targetPEs = processingElements;

        /* Start streaming. */
        /*
         * TODO: This is only for prototyping. Comm layer will take care of this
         * in the real implementation.
         */
        // logger.trace("Start thread for stream " + name);
        thread = new Thread(this, name);
        thread.start();
    }

    /*
     * This constructor will create a broadcast stream. That is, the events will
     * be sent to all the PE instances.
     */
    public Stream(App app, String name, ProcessingElement... processingElements) {
        this(app, name, null, processingElements);
    }

    public void put(T event) {
        try {
//            if(queue.remainingCapacity() < 150 ) {
//                logger.debug("Remaining capacity: " + queue.remainingCapacity());
//                Thread.sleep(50);
//            }
            queue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Interrupted while waiting to put an event in the queue: {}.", e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the key
     */
    public Key<T> getKey() {
        return key;
    }

    /**
     * @return the list of target processing elements.
     */
    public ProcessingElement[] getTargetPEs() {
        return targetPEs;
    }

    public void close() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                /* Get oldest event in queue. */
                T event = queue.take();

                /* Send event to each target PE. */
                for (int i = 0; i < targetPEs.length; i++) {

                    if (key == null) {

                        /* Broadcast to all PE instances! */

                        /* STEP 1: find all PE instances. */

                        Collection<ProcessingElement> pes = targetPEs[i]
                                .getInstances();

                        /* STEP 2: iterate and pass event to PE instance. */
                        for (ProcessingElement pe : pes) {

                            pe.handleInputEvent(event);
                        }

                    } else {

                        /* We have a key, send to target PE. */

                        /* STEP 1: find the PE instance for key. */
                        ProcessingElement pe = targetPEs[i]
                                .getInstanceForKey(key.get(event));

                        /* STEP 2: pass event to PE instance. */
                        pe.handleInputEvent(event);
                    }
                }

            } catch (InterruptedException e) {
                logger.info("Closing stream {}.", name);
                return;
            }
        }
    }
}
