package org.freeflow.layouts.tests;

import junit.framework.TestCase;

import org.freeflow.core.FrameDescriptor;
import org.freeflow.layouts.HLayout;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HLayoutTest extends TestCase {

	private HLayout layout = new HLayout();

	public HLayoutTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		layout.setItems(itemAdapter);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testViewPortCorrectFrames1() {
		layout.setItemWidth(100);
		layout.setDimensions(500, 500);
		layout.generateFrameDescriptors();
		SparseArray<FrameDescriptor> descs = layout.getFrameDescriptors(100, 0);

		assertNotNull(descs);
		assertEquals(1, descs.get(descs.keyAt(0)).itemIndex);
		FrameDescriptor desc = descs.get(descs.keyAt(0));
		assertEquals(0, desc.frame.left);

		assertEquals(5, descs.size());

		descs = layout.getFrameDescriptors(500, 0);
		assertNotNull(descs);
		assertEquals(5, descs.get(descs.keyAt(0)).itemIndex);
		desc = descs.get(descs.keyAt(0));
		assertEquals(0, desc.frame.left);

		descs = layout.getFrameDescriptors(550, 0);
		assertNotNull(descs);
		assertEquals(5, descs.get(descs.keyAt(0)).itemIndex);
		desc = descs.get(descs.keyAt(0));
		assertEquals(-50, desc.frame.left);

	}

	public void testViewPortCorrectFrames2() {
		layout.setItemWidth(50);
		layout.setDimensions(500, 500);
		layout.generateFrameDescriptors();
		SparseArray<FrameDescriptor> descs = layout.getFrameDescriptors(100, 0);

		assertNotNull(descs);
		assertEquals(2, descs.get(descs.keyAt(0)).itemIndex);
		FrameDescriptor desc = descs.get(descs.keyAt(0));
		assertEquals(0, desc.frame.left);

		assertEquals(10, descs.size());

		descs = layout.getFrameDescriptors(500, 0);
		assertNotNull(descs);
		assertEquals(10, descs.get(descs.keyAt(0)).itemIndex);
		desc = descs.get(descs.keyAt(0));
		assertEquals(0, desc.frame.left);

		descs = layout.getFrameDescriptors(25, 0);
		assertNotNull(descs);
		assertEquals(0, descs.get(descs.keyAt(0)).itemIndex);
		desc = descs.get(descs.keyAt(0));
		assertEquals(-25, desc.frame.left);
	}

	BaseAdapter itemAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return new Integer(position);
		}

		@Override
		public int getCount() {
			return 30;
		}
	};

}
