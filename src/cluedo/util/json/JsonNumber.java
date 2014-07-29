package cluedo.util.json;

public class JsonNumber implements JsonEntity {

	private double value;

	public JsonNumber(double parseDouble) {
		this.value = parseDouble;
	}

	@Override
	public String toString() {
		return value + "";
	}

}
