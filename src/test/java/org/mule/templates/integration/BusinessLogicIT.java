/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.ObjectBuilder;

import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Mule Template that make calls to external systems.
 * 
 * @author cesar.garcia
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	protected static final String TEMPLATE_NAME = "contact-aggregation";
	
	private List<Map<String, Object>> createdContactsInA = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdContactsInB = new ArrayList<Map<String, Object>>();

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {

		createContacts();
	}

	@SuppressWarnings("unchecked")
	private void createContacts() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("createContactInAFlow");
		flow.initialise();

		Map<String, Object> contact = createContact("A", 0);
		createdContactsInA.add(contact);

		MuleEvent event = flow.process(getTestEvent(createdContactsInA,	MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		
		for (int i = 0; i < results.size(); i++) {
			createdContactsInA.get(i).put("Id", results.get(i).getId());
		}

		flow = getSubFlow("createContactInBFlow");
		flow.initialise();

		contact = createContact("B", 0);
		createdContactsInB.add(contact);

		event = flow.process(getTestEvent(createdContactsInB, MessageExchangePattern.REQUEST_RESPONSE));
		results = (List<SaveResult>) event.getMessage().getPayload();
		
		Map<String, Object> retrieveMap = new HashMap<String, Object>();
		retrieveMap.put("lastname",	createdContactsInB.get(0).get("lastname") );
		
		flow = getSubFlow("retrieveContactFromMsDynamics");
		flow.initialise();
		event = flow.process(getTestEvent(retrieveMap, MessageExchangePattern.REQUEST_RESPONSE));
		HashMap<?,?> msDynContactMap = (HashMap<?,?>) ((Iterator<?>) event.getMessage().getPayload()).next();
			
		createdContactsInB.get(0).put("Id", msDynContactMap.get("contactid"));
	}

	protected Map<String, Object> createContact(String orgId, int sequence) {
		if (orgId.equals("A")) {
			return ObjectBuilder.aContact()
					.with("FirstName", "FirstName_" + orgId + sequence)
					.with("LastName", buildUniqueName(TEMPLATE_NAME, "LastName_" + sequence + "_"))
					.with("Email", buildUniqueEmail("some.email." + sequence))
					.with("Description", "Some fake description")
					.with("MailingCity", "Denver").with("MailingCountry", "US")
					.with("MobilePhone", "123456789")
					.with("Department", "department_" + sequence + "_" + orgId)
					.with("Phone", "123456789").with("Title", "Dr").build();
		} 
		if (orgId.equals("B")) {
			return ObjectBuilder.aContact()
					.with("firstname", "FirstName_" + orgId + sequence)
					.with("lastname", buildUniqueName(TEMPLATE_NAME, "LastName_" + sequence + "_"))
					.with("emailaddress1", buildUniqueEmail("some.email." + sequence)).build();
		}
		return null;
	}

	protected String buildUniqueName(String templateName, String name) {
		String timeStamp = new Long(new Date().getTime()).toString();

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(templateName);
		builder.append(timeStamp);

		return builder.toString();
	}

	protected String buildUniqueEmail(String contact) {
		String server = "fakemail";

		StringBuilder builder = new StringBuilder();
		builder.append(TEMPLATE_NAME + contact);
		builder.append("@");
		builder.append(server);
		builder.append(".com");

		return builder.toString();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestContactFromCompany(createdContactsInA,	"deleteContactFromAFlow");
		deleteTestContactFromCompany(createdContactsInB,	"deleteContactFromBFlow");
	}

	protected void deleteTestContactFromCompany(List<Map<String, Object>> createdContacts, String deleteFlow) throws Exception {
		List<String> idList = new ArrayList<String>();

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();
		for (Map<String, Object> c : createdContacts) {
			idList.add((String) c.get("Id"));
		}
		flow.process(getTestEvent(idList,
				MessageExchangePattern.REQUEST_RESPONSE));
		idList.clear();

	}	
	
	@Test
	public void testMainFlow() throws Exception {
		MuleEvent event = runFlow("mainFlow");
		Assert.assertTrue("The payload should not be null.", "Please find attached your Contacts Report".equals(event.getMessage().getPayload()));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGatherDataFlow() throws Exception {		
		MuleEvent event = runFlow("gatherDataFlow");
		Iterator<Map<String, String>> mergedContactList = (Iterator<Map<String, String>>)event.getMessage().getPayload();
		
		Assert.assertTrue("There should be contacts from source A or source B.", mergedContactList.hasNext());
	}

}
