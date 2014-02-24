package com.comcast.freeflow.core;

public class IndexPath {
	public int section;
	public int positionInSection;

	public IndexPath(int section, int position) {
		this.section = section;
		this.positionInSection = position;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != IndexPath.class) {
			return false;
		}
		IndexPath p = (IndexPath) o;
		return ((p.section == section) && (p.positionInSection == positionInSection));
	}

	@Override
	public String toString() {
		return "Section: " + section + " index: " + positionInSection;
	}

}