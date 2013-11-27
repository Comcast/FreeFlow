package org.freeflow.core;

import java.util.ArrayList;

import android.util.Pair;

public class LayoutChangeSet {
	protected ArrayList<Pair<ItemProxy, Frame>> moved = new ArrayList<Pair<ItemProxy,Frame>>();
	protected ArrayList<ItemProxy> removed;
	protected ArrayList<ItemProxy> added;
	
	
	public LayoutChangeSet(){
		
		removed = new ArrayList<ItemProxy>();
		added = new ArrayList<ItemProxy>();
	}
	
	public void addToMoved(ItemProxy proxy, Frame oldFrame ){
		moved.add(new Pair<ItemProxy, Frame>(proxy, oldFrame));
	}
	public void addToDeleted(ItemProxy proxy){
		removed.add(proxy);
	}
	public void addToAdded(ItemProxy proxy){
		added.add(proxy);
	}
	
	public ArrayList<Pair<ItemProxy, Frame>> getMoved(){
		return moved;
	}
	
	
}
