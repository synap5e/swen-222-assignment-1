package util.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonTest {
	
	@Test
	public void rulesWithoutError() throws FileNotFoundException, JsonParseException{
		JsonParser.parseJson(new File("./rules.json"));
	}
	
	@Test
	public void testString() throws JsonParseException{
		JsonEntity expected = new JsonString("abc");
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"abc\" }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"abc\", \"b\":\"bca\" }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"bca\", \"b\":\"abc\" }").get("b"));
	}
	
	@Test
	public void testEmptyString() throws JsonParseException{
		JsonEntity expected = new JsonString("");
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"\" }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"\", \"b\":\"\" }").get("b"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":\"abc\", \"b\":\"\" }").get("b"));
	}
	
	@Test
	public void testNumber() throws JsonParseException{
		JsonEntity expected = new JsonNumber(1);
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":1 }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":2, \"b\":1 }").get("b"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":1, \"b\":1 }").get("b"));
		
		expected = new JsonNumber(1.5);
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":1.5 }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":2, \"b\":1.5 }").get("b"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":1.7, \"b\":1.5 }").get("b"));
		
		expected = new JsonNumber(-1.5);
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":-1.5 }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":2, \"b\":-1.5 }").get("b"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":1.7, \"b\":-1.5 }").get("b"));
		
		expected = new JsonNumber(-1e6);
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":-1e6 }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":2, \"b\":-1e6 }").get("b"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":-1e9, \"b\":-1e6 }").get("b"));
		
		expected = new JsonNumber(-1.1111e6);
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":-1.1111e6 }").get("a"));
	}
	
	@Test(expected=JsonParseException.class)
	public void testInvalidNumber() throws JsonParseException{
		JsonParser.parseJson("{\"a\":1ee6}");
	}
	
	@Test
	public void testObject() throws JsonParseException{
		JsonObject expected = new JsonObject();
		expected.put("key1", new JsonString("value"));
		expected.put("key2", new JsonNumber(5.5));
		
		assertEqualsJson(expected, JsonParser.parseJson("{\"key1\":\"value\", \"key2\":5.5}"));
		assertEqualsJson(expected, JsonParser.parseJson("{\"key2\":5.5,\"key1\":\"value\"}"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"key1\" :\t\"value\"  ,\"key2\"\t:\t5.5  \t  }"));
	}
	
	@Test
	public void testEmptyObject() throws JsonParseException{
		JsonEntity expected = new JsonObject();
		
		assertEqualsJson(expected, JsonParser.parseJson("{}"));
		assertEqualsJson(expected, JsonParser.parseJson("{ }"));
		assertEqualsJson(expected, JsonParser.parseJson(" { }"));
		assertEqualsJson(expected, JsonParser.parseJson("{\t}"));
		assertEqualsJson(expected, JsonParser.parseJson(" {} "));
		assertEqualsJson(expected, JsonParser.parseJson(" \t{   \t  }"));
	}
	
	@Test
	public void testList() throws JsonParseException{
		JsonList expected = new JsonList();
		expected.append(new JsonNumber(1));
		expected.append(new JsonString("abc"));
		
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":[1, \"abc\"] }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":[  1  ,\"abc\"] }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{\"a\":[1,\"abc\"]}").get("a"));
	}
	
	@Test
	public void testEmptyList() throws JsonParseException{
		JsonList expected = new JsonList();
		
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":[] }").get("a"));
		assertEqualsJson(expected, JsonParser.parseJson("{ \"a\":  [] }").get("a"));
	}
	
	private static void assertEqualsJson(JsonEntity expected, JsonEntity actual) {
		if (expected instanceof JsonObject){
			assertTrue(actual instanceof JsonObject);
			
			JsonObject eob = (JsonObject) expected;
			JsonObject aob = (JsonObject) actual;
			
			assertEquals(eob.keys().size(), aob.keys().size());
			for (String key : eob.keys()){
				assertEqualsJson(eob.get(key), aob.get(key));
			}
		} else if (expected instanceof JsonList){
			JsonList eli = (JsonList) expected;
			JsonList ali = (JsonList) actual;
			
			assertEquals(eli.size(), ali.size());
			

			Iterator<JsonEntity> eit = eli.iterator();
			Iterator<JsonEntity> ait = ali.iterator();
			
			while (eit.hasNext()){
				assertEqualsJson(eit.next(), ait.next());
			}
			
			
		} else if (expected instanceof JsonString){
			assertEquals(((JsonString) expected).value(), ((JsonString) actual).value());
		} else if (expected instanceof JsonNumber){
			assertEquals(((JsonNumber) expected).value(), ((JsonNumber) actual).value(), 0.001);
			
		} else {
			throw new RuntimeException("Don't know how to test " + expected.getClass());
		}
		
		// test tostrings
		assertEquals(expected.toString(), actual.toString());
	}

}
