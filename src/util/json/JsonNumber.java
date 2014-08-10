package util.json;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonNumber implements JsonEntity {

	private double value;

	public JsonNumber(double parseDouble) {
		this.value = parseDouble;
	}

	@Override
	public String toString() {
		return value + "";
	}

	public double value() {
		return value;
	}

}
