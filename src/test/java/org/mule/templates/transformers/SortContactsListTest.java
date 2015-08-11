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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.transformer.TransformerException;

@SuppressWarnings({ "unchecked", "deprecation" })
@RunWith(MockitoJUnitRunner.class)
public class SortContactsListTest {
	
	private static final Logger LOGGER = LogManager.getLogger(SortContactsListTest.class);
	
	@Mock
	private MuleContext muleContext;
			
	@Mock
	private MuleConfiguration muleConfiguration;

	@Test
	public void testSort() throws TransformerException {
		Mockito.when(muleContext.getConfiguration()).thenReturn(muleConfiguration);
		Mockito.when(muleConfiguration.getDefaultEncoding()).thenReturn("UTF-8");
		
		MuleMessage message = new DefaultMuleMessage(createOriginalList().iterator(),
				muleContext);

		SortContactList transformer = new SortContactList();
		List<Map<String, String>> sortedList = (List<Map<String, String>>) transformer
				.transform(message, "UTF-8");

		LOGGER.info(sortedList);
		Assert.assertEquals("The merged list obtained is not as expected",
				createExpectedList(), sortedList);
	}

	private List<Map<String, String>> createExpectedList() {
		Map<String, String> contact0 = new HashMap<String, String>();
		contact0.put("IDInSalesforce", "0");
		contact0.put("IDInMsDynamics", "");
		contact0.put("Email", "some.email.0@fakemail.com");

		Map<String, String> contact1 = new HashMap<String, String>();
		contact1.put("IDInSalesforce", "1");
		contact1.put("IDInMsDynamics", "1");
		contact1.put("Email", "some.email.1@fakemail.com");

		Map<String, String> contact2 = new HashMap<String, String>();
		contact2.put("IDInSalesforce", "");
		contact2.put("IDInMsDynamics", "2");
		contact2.put("Email", "some.email.2@fakemail.com");

		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
		contactList.add(contact0);
		contactList.add(contact2);
		contactList.add(contact1);

		return contactList;

	}

	private List<Map<String, String>> createOriginalList() {
		Map<String, String> contact0 = new HashMap<String, String>();
		contact0.put("IDInSalesforce", "0");
		contact0.put("IDInMsDynamics", "");
		contact0.put("Email", "some.email.0@fakemail.com");

		Map<String, String> contact1 = new HashMap<String, String>();
		contact1.put("IDInSalesforce", "1");
		contact1.put("IDInMsDynamics", "1");
		contact1.put("Email", "some.email.1@fakemail.com");

		Map<String, String> contact2 = new HashMap<String, String>();
		contact2.put("IDInSalesforce", "");
		contact2.put("IDInMsDynamics", "2");
		contact2.put("Email", "some.email.2@fakemail.com");

		List<Map<String, String>> contactList = new ArrayList<Map<String, String>>();
		contactList.add(contact0);
		contactList.add(contact1);
		contactList.add(contact2);

		return contactList;

	}

}
