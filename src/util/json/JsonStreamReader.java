package util.json;

import java.io.InputStream;
import java.util.Scanner;

public class JsonStreamReader{

	private Scanner scan;

	public JsonStreamReader(final InputStream inputStream) {
		this.scan = new Scanner(inputStream);
		JsonParser.initialise(scan);
	}
	
	public JsonObject next(){
		try {
			return JsonParser.parseJson(scan, true);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	

}
