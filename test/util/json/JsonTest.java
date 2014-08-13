package util.json;

import org.junit.Test;

public class JsonTest {
	
	@Test
	public void testEmptyString() throws JsonParseException{
		String s = "{ \"a\":\"\" }";
		MinimalJson.parseJson(s);
	}

}
