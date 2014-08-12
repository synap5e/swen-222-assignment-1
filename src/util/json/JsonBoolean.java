package util.json;

public class JsonBoolean implements JsonEntity {

	private boolean value;

	public JsonBoolean(boolean value) {
		this.value = value;
	}
	
	public JsonBoolean(String s) {
		this.value = Boolean.parseBoolean(s);
	}

	public boolean value() {
		return value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}

}
