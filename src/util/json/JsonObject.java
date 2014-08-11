package util.json;

import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
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
		StringBuilder sb = new StringBuilder(" ");
		for (Entry<String, JsonEntity> es : elems.entrySet()){
			sb.append("\"");
			sb.append(es.getKey());
			sb.append("\"");
			sb.append(" : ");
			sb.append(es.getValue());
			sb.append(",");
		}
		return "{" + sb.substring(0, sb.length()-1) + "}";
	}

	public JsonEntity get(String string) {
		return elems.get(string);
	}

	public Set<String> keys() {
		return elems.keySet();
	}

}
