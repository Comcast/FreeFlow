/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
