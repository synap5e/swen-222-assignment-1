package cluedo.util.json;

import java.util.Iterator;
import java.util.List;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonList implements JsonEntity, Iterable<JsonEntity>{

	private List<JsonEntity> elems;

	public JsonList(List<JsonEntity> elems) {
		this.elems = elems;
	}

	@Override
	public String toString() {
		return elems.toString();
	}

	@Override
	public Iterator<JsonEntity> iterator() {
		return elems.iterator();
	}

}