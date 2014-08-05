package cluedo.util.json;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonString implements JsonEntity {

	private String value;

	public JsonString(String next) {
		this.value = next;
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public String value() {
		return value;
	}

}
