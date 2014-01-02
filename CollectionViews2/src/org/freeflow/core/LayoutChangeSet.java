package org.freeflow.core;

import java.util.ArrayList;

import android.util.Pair;

public class LayoutChangeSet {
	protected ArrayList<Pair<ItemProxy, Frame>> moved = new ArrayList<Pair<ItemProxy, Frame>>();
	protected ArrayList<ItemProxy> removed = new ArrayList<ItemProxy>();
	protected ArrayList<ItemProxy> added = new ArrayList<ItemProxy>();

	public LayoutChangeSet() {
	}

	public void addToMoved(ItemProxy proxy, Frame oldFrame) {
		moved.add(new Pair<ItemProxy, Frame>(proxy, oldFrame));
	}

	public void addToDeleted(ItemProxy proxy) {
		removed.add(proxy);
	}

	public void addToAdded(ItemProxy proxy) {
		added.add(proxy);
	}

	public ArrayList<ItemProxy> getAdded() {
		return added;
	}
	
	public ArrayList<ItemProxy> getRemoved() {
		return removed;
	}

	public ArrayList<Pair<ItemProxy, Frame>> getMoved() {
		return moved;
	}

	@Override
	public String toString() {
		return 	"Added: " + added.size() + "," +
				"Removed: " + removed.size()+ ","+
				"Moved: " + moved.size();
	}

	public boolean isEmpty() {
		return (added.size() == 0 && removed.size() == 0 && moved.size() == 0);	
	}

}
