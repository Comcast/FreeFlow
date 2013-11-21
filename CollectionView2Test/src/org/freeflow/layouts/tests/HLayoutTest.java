package org.freeflow.layouts.tests;

import junit.framework.TestCase;

import org.freeflow.core.BaseSectionedAdapter;
import org.freeflow.core.ItemProxy;
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
		// layout.setItems(itemAdapter);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// private SparseArray<FrameDescriptor> prepareLayout(int itemWidth, int
	// width, int height, int viewportX) {
	// layout.setItemWidth(itemWidth);
	// layout.setDimensions(width, height);
	// layout.generateFrameDescriptors();
	// return layout.getFrameDescriptors(100, 0);
	// }
	//
	// public void testViewPortCorrectIndex1() {
	// SparseArray<FrameDescriptor> descs = prepareLayout(100, 500, 500, 100);
	// assertNotNull(descs);
	// assertEquals(1, descs.get(descs.keyAt(0)).itemIndex);
	// assertEquals(5, descs.size());
	//
	// descs = layout.getFrameDescriptors(500, 0);
	// assertNotNull(descs);
	// assertEquals(5, descs.get(descs.keyAt(0)).itemIndex);
	//
	// descs = layout.getFrameDescriptors(550, 0);
	// assertNotNull(descs);
	// assertEquals(5, descs.get(descs.keyAt(0)).itemIndex);
	//
	// }
	//
	// public void testViewPortCorrectFraming1() {
	// SparseArray<FrameDescriptor> descs = prepareLayout(100, 500, 500, 100);
	// assertNotNull(descs);
	//
	// FrameDescriptor desc = descs.get(descs.keyAt(0));
	// assertEquals(0, desc.frame.left);
	// assertEquals(0, desc.frame.top);
	// assertEquals(100, desc.frame.width);
	// assertEquals(500, desc.frame.height);
	//
	// descs = layout.getFrameDescriptors(550, 0);
	// desc = descs.get(descs.keyAt(0));
	// assertEquals(-50, desc.frame.left);
	// assertEquals(0, desc.frame.top);
	// assertEquals(100, desc.frame.width);
	// assertEquals(500, desc.frame.height);
	//
	// }
	//
	// public void testViewPortCorrectIndex2() {
	// SparseArray<FrameDescriptor> descs = prepareLayout(50, 500, 500, 100);
	// assertNotNull(descs);
	//
	// assertNotNull(descs);
	// assertEquals(2, descs.get(descs.keyAt(0)).itemIndex);
	// assertEquals(10, descs.size());
	//
	// descs = layout.getFrameDescriptors(500, 0);
	// assertNotNull(descs);
	// assertEquals(10, descs.get(descs.keyAt(0)).itemIndex);
	//
	// descs = layout.getFrameDescriptors(25, 0);
	// assertNotNull(descs);
	// assertEquals(0, descs.get(descs.keyAt(0)).itemIndex);
	//
	// }
	//
	// public void testViewPortCorrectFraming2() {
	// SparseArray<FrameDescriptor> descs = prepareLayout(50, 500, 500, 100);
	// assertNotNull(descs);
	//
	// FrameDescriptor desc = descs.get(descs.keyAt(0));
	// assertEquals(0, desc.frame.left);
	// assertEquals(0, desc.frame.top);
	// assertEquals(50, desc.frame.width);
	// assertEquals(500, desc.frame.height);
	//
	// descs = layout.getFrameDescriptors(525, 0);
	// desc = descs.get(descs.keyAt(0));
	// assertEquals(-25, desc.frame.left);
	// assertEquals(0, desc.frame.top);
	// assertEquals(50, desc.frame.width);
	// assertEquals(500, desc.frame.height);
	//
	// }
	//
	// BaseSectionedAdapter itemAdapter = new BaseSectionedAdapter() {
	//
	// @Override
	// public View getViewForSection(int section, int position, View
	// convertView, ViewGroup parent) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int section, int position) {
	// return 0;
	// }
	//
	// @Override
	// public View getHeaderViewForSection(int section, View convertView,
	// ViewGroup parent) {
	// return null;
	// }
	// };
}
