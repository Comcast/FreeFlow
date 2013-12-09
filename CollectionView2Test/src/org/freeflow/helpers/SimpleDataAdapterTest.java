package org.freeflow.helpers;

import junit.framework.TestCase;

public class SimpleDataAdapterTest extends TestCase {
	
	public void testGeneratesCorrectSectionCounts(){
		SimpleDataAdapter adapter = new SimpleDataAdapter(null, 5, 10);
		assertEquals(5, adapter.getSections().size());
	}

}
