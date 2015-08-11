/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleContext;
import org.mule.api.transformer.TransformerException;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.class)
public class ContactsMergeTest {

	private static final Logger LOGGER = LogManager.getLogger(ContactsMergeTest.class);
	
	@Mock
	private MuleContext muleContext;

	@Test
	public void testMerge() throws TransformerException {
		List<Map<String, String>> contactsA = createLists("A", 0, 1);
		List<Map<String, String>> contactsB = createLists("B", 1, 2);
		
		ContactMerger sfdcContactMerge = new ContactMerger();
		List<Map<String, String>> mergedList = sfdcContactMerge.mergeList(contactsA, contactsB);

		LOGGER.info(mergedList);
		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);

	}

	@Test
	public void testMergeWithNullEmailValues() throws TransformerException {
		List<Map<String, String>> contactsA = createLists("A", 0, 1);
		contactsA.get(0).put("Email", null);

		List<Map<String, String>> contactsB = createLists("B", 1, 2);

		ContactMerger sfdcContactMerge = new ContactMerger();
		List<Map<String, String>> mergedList = sfdcContactMerge.mergeList(contactsA, contactsB);

		LOGGER.info(mergedList);

		List<Map<String, String>> expectedList = createExpectedList();
		expectedList.get(0).put("Email", null);
		Assert.assertEquals("The merged list obtained is not as expected", expectedList, mergedList);

	}

	static List<Map<String, String>> createExpectedList() {
		Map<String, String> contact0 = new HashMap<String, String>();
		contact0.put("IDInSalesforce", "0");
		contact0.put("IDInMsDynamics", "");
		contact0.put("Email", "some.email.0@fakemail.com");
		contact0.put("ContactNameInSalesforce", "SomeName_0");
		contact0.put("ContactNameInMsDynamics", "");

		Map<String, String> contact1 = new HashMap<String, String>();
		contact1.put("IDInSalesforce", "1");
		contact1.put("IDInMsDynamics", "1");
		contact1.put("Email", "some.email.1@fakemail.com");
		contact1.put("ContactNameInMsDynamics", "SomeName_1");
		contact1.put("ContactNameInSalesforce", "SomeName_1");

		Map<String, String> contact2 = new HashMap<String, String>();
		contact2.put("IDInSalesforce", "");
		contact2.put("IDInMsDynamics", "2");
		contact2.put("Email", "some.email.2@fakemail.com");
		contact2.put("ContactNameInMsDynamics", "SomeName_2");
		contact2.put("ContactNameInSalesforce", "");

		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
		contactList.add(contact0);
		contactList.add(contact1);
		contactList.add(contact2);

		return contactList;

	}

	static List<Map<String, String>> createLists(String orgId, int start, int end) {
		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
		for (int i = start; i <= end; i++) {
			contactList.add(createContact(orgId, i));
		}
		return contactList;
	}

	static Map<String, String> createContact(String orgId, int sequence) {
		Map<String, String> contact = new HashMap<String, String>();
		
		contact.put("Id",new Integer(sequence).toString());	
		contact.put("Name", "SomeName_" + sequence);
		contact.put("Email", "some.email." + sequence + "@fakemail.com");

		return contact;
	}
}
