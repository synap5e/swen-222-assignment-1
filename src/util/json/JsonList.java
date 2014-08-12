package util.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cluedo.model.card.Room;
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

	public JsonList() {
		this.elems = new ArrayList<JsonEntity>();
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

	public void append(JsonEntity e) {
		this.elems.add(e);
	}

	public void append(int i) {
		append(new JsonNumber(i));
	}

}
