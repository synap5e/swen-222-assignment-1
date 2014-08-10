package util.json;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cluedo.model.Room;
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

	public JsonEntity get(int i) {
		return elems.get(i);
	}

	public int size() {
		return elems.size();
	}

}
