package util.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/** A quick, minimal json parser hacked up in half an hour.
 * 
 * @author Simon Pinfold
 *
 */
public class MinimalJson {

	private static Pattern pat = Pattern.compile("[ \t\n\r]+|(?=[\"{}\\[\\],:])|(?<=[\"{}\\[\\],:])");

	public static void main(String[] args) throws JsonParseException, FileNotFoundException{
		System.out.println(MinimalJson.parseJson("{\"abc\" : [12, \"asdad\" , 3,4.8], \"a\":3, \"c\":[1,2], \"d\":{\"a\":2,\"b\":{\"c\":1}}, "
											   + "\"k\":[], \"l\":{}, \"m\" : null  ,\t\"n\":[null,null,{\"a\":null} ] }"));
		System.out.println(MinimalJson.parseJson(new File("./rules/cards.json")));
	}
	
	public static JsonObject parseJson(String s) throws JsonParseException{
		Scanner scan = new Scanner(s);
		return parseJson(scan);
	}
	
	public static JsonObject parseJson(File f) throws JsonParseException, FileNotFoundException{
		return parseJson(new Scanner(f));
	}
	
	public static JsonObject parseJson(InputStream stream) throws JsonParseException {
		return parseJson(new Scanner(stream));
	}
	
	static JsonObject parseJson(Scanner scan) throws JsonParseException{
		scan.useDelimiter(pat);
		require("\\{", scan);
		return parseObject(scan);
	}

	private static JsonEntity parseEntity(Scanner scan) throws JsonParseException {
		if (gobble("\\{", scan)){
			return parseObject(scan);
		} else if (gobble("\\[", scan)){
			return parseList(scan);
		} else if (gobble("\"", scan)){
			return parseString(scan);
		} else if (scan.hasNext(Pattern.compile("-?0*(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?"))){
			return parseNumber(scan);
		} else if (scan.hasNext("null")){
			scan.next();
			return null;
		}
		throw new JsonParseException("Could not parse " + scan.next() + " as json entity");
	}

	private static JsonNumber parseNumber(Scanner scan) {
		return new JsonNumber(Double.parseDouble(scan.next()));
	}

	private static JsonString parseString(Scanner scan) throws JsonParseException {
		scan.useDelimiter("[\"\\r\\n]");
		String s = scan.next();
		scan.useDelimiter(pat);
		require("\"", scan);
		return new JsonString(s);
	}

	private static JsonList parseList(Scanner scan) throws JsonParseException {
		List<JsonEntity> elems = new ArrayList<JsonEntity>();
		if (!scan.hasNext("\\]")){
			do {
				elems.add(parseEntity(scan));
			} while(gobble(",", scan));
		}
		require("\\]", scan);
		return new JsonList(elems);
	}

	private static JsonObject parseObject(Scanner scan) throws JsonParseException {
		Map<String, JsonEntity> elems = new HashMap<String, JsonEntity>();
		if (!scan.hasNext("\\}")){
			do {
				require("\"", scan);
				String name = parseString(scan).value();
				if (elems.containsKey(name)){
					throw new JsonParseException("The object already contains key \"" + name + "\"");
				}
				require(":", scan);
				elems.put(name, parseEntity(scan));
			} while(gobble(",", scan));
		}
		require("\\}", scan);
		return new JsonObject(elems);
	}

	private static void require(String str, Scanner scan) throws JsonParseException {
		if (!gobble(str, scan)){
			throw new JsonParseException("Expected " + str + ", got " + scan.next());
		}
	}

	private static boolean gobble(String str, Scanner scan) {
		if (scan.hasNext(str)){
			scan.next(str);
			return true;
		}
		return false;
	}

}
