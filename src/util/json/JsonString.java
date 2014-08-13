package util.json;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonString implements JsonEntity {

	private String value;

	public JsonString(String value) {
		assert value != null;
		this.value = value;
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public String value() {
		return value;
	}

}
