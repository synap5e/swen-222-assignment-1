package cluedo.util.json;

import java.util.Map;
import java.util.Set;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonObject implements JsonEntity {

	private Map<String, JsonEntity> elems;

	public JsonObject(Map<String, JsonEntity> elems) {
		this.elems = elems;
	}

	@Override
	public String toString() {
		return elems.toString();
	}

	public JsonEntity get(String string) {
		return elems.get(string);
	}

	public Set<String> keys() {
		return elems.keySet();
	}



}
