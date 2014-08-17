package util.json;

import java.io.InputStream;
import java.util.Scanner;

public class JsonStreamReader{

	private Scanner scan;

	public JsonStreamReader(final InputStream inputStream) {
		this.scan = new Scanner(inputStream);
		MinimalJson.initialise(scan);
	}
	
	public JsonObject next(){
		try {
			return MinimalJson.parseJson(scan, true);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	

}
