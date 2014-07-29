package cluedo.util.json;

import java.util.List;

public class JsonList implements JsonEntity {

	private List<JsonEntity> elems;

	public JsonList(List<JsonEntity> elems) {
		this.elems = elems;
	}

	@Override
	public String toString() {
		return elems.toString();
	}

}
