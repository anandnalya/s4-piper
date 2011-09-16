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
package io.s4.example.twittertrending;

import io.s4.core.App;
import io.s4.core.ProcessingElement;
import io.s4.core.Stream;
import io.s4.example.counter.PrintPE;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/*
 * This is an sample application to test a new A4 API. 
 * See README file for details.
 * 
 * */

final public class MyApp extends App {

    final private String twitterUsername;
    final private String twitterPassword;
    private TwitterFeedListener twitterFeedListener;

    /*
     * We use Guice to pass parameters to the application. This is just a
     * trivial example where we get the value for the variable interval from a
     * properties file. (Saved under "src/main/resources".) All configuration
     * details are done in Module.java.
     * 
     * The application graph itself is created in this Class. However,
     * developers may provide tools for creating apps which will generate the
     * objects.
     * 
     * IMPORTANT: we create a graph of PE prototypes. The prototype is a class
     * instance that is used as a prototype from which all PE instance will be
     * created. The prototype itself is not used as an instance. (Except when
     * the PE is of type Singleton PE). To create a data structure for each PE
     * instance you must do in the method ProcessingElement.onCreate().
     */
    @Inject
    public MyApp(@Named("twitter.username") String twitterUsername,
	    @Named("twitter.password") String twitterPassword) {
	this.twitterUsername = twitterUsername;
	this.twitterPassword = twitterPassword;
    }

    /*
     * Build the application graph using POJOs. Don't like it? Write a nice
     * tool.
     * 
     * @see io.s4.App#init()
     */
    @Override
    protected void init() {
	/* PE that prints counts to console. */
	ProcessingElement printPE = new PrintPE(this);

	// Stream that will have have various reports in text format.
	Stream<TextEvent> reportStream = new Stream<TextEvent>(this,
		"ReportStream", new TextKeyFinder(), printPE);

	// PE to find top N entities
	ProcessingElement topNTopiePe = new TopNEntityPE(this, reportStream);
	topNTopiePe.setOutputIntervalInEvents(2);

	// Stream that will have aggregate counts for various entity streams.
	Stream<EntityEvent> aggregateEngityCountStream = new Stream<EntityEvent>(
		this, "AggregatedEntityCount", new EntityReportKeyFinder(),
		topNTopiePe);

	// PEs that count entities and attach report ids
	ProcessingElement hashtagStreamCounter = new EntityCounterAndReporterPE(
		this, aggregateEngityCountStream, 1, "Top Hashtags");
	hashtagStreamCounter.setOutputIntervalInEvents(2);
	ProcessingElement wordStreamCounter = new EntityCounterAndReporterPE(
		this, aggregateEngityCountStream, 1, "Top Words");
	wordStreamCounter.setOutputIntervalInEvents(2);
	ProcessingElement userStreamCounter = new EntityCounterAndReporterPE(
		this, aggregateEngityCountStream, 1, "Top Users");
	userStreamCounter.setOutputIntervalInEvents(2);

	// Streams that carries events for entities (#tags, word frequency and
	// @mentions)
	Stream<EntityEvent> hashtagSeenStream = new Stream<EntityEvent>(this,
		"HashtagStream", new EntityKeyFinder(), hashtagStreamCounter);
	Stream<EntityEvent> wordFrequecyStream = new Stream<EntityEvent>(this,
		"WordStream", new EntityKeyFinder(), wordStreamCounter);
	Stream<EntityEvent> userMentionStream = new Stream<EntityEvent>(this,
		"UserStream", new EntityKeyFinder(), userStreamCounter);
	

	// PE that extracts #tags, words and @mentions
	ProcessingElement entityExtractorPE = new EntityExtractorPE(this,
		hashtagSeenStream, userMentionStream, wordFrequecyStream);

	// Stream on which the twitter client sends StatusEvents
	Stream<StatusEvent> statusStream = new Stream<StatusEvent>(this,
		"RawStream", new StatusKeyFinder(), entityExtractorPE);

	// Initialize Twitter Client
	twitterFeedListener = new TwitterFeedListener(statusStream);
	twitterFeedListener.setUserid(twitterUsername);
	twitterFeedListener.setPassword(twitterPassword);
    }

    /*
     * Start twitter feed listener
     * 
     * @see io.s4.App#start()
     */
    @Override
    protected void start() {
	twitterFeedListener.init();
	twitterFeedListener.run();

	try {
	    Thread.sleep(1000);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	System.out.println("Done. Closing...");
	removeAll();
    }

    @Override
    protected void close() {
	System.out.println("Bye.");

    }

    public static void main(String[] args) {
	Injector injector = Guice.createInjector(new Module());
	MyApp myApp = injector.getInstance(MyApp.class);
	myApp.init();
	myApp.start();
    }
}
