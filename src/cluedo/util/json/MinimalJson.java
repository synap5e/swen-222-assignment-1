package cluedo.util.json;

import java.io.File;
import java.io.FileNotFoundException;
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

	public static void main(String[] args) throws JsonParseException, FileNotFoundException{
		System.out.println(MinimalJson.parseJson("{abc : [12, \"asdad\" , 3,4.8], a:3, c:[1,2], d:{a:2,b:{c:1}}, k:[], l:{}, m : null  ,\tn:[null,null,{a:null} ] }"));
		System.out.println(MinimalJson.parseJson("{weapons : [\"1\"]}"));
		System.out.println(MinimalJson.parseJson(new File("./rules/cards.json")));
	}
	
	public static JsonObject parseJson(String s) throws JsonParseException{
		Scanner scan = new Scanner(s);
		return parseJson(scan);
	}
	
	public static JsonObject parseJson(File f) throws JsonParseException, FileNotFoundException{
		return parseJson(new Scanner(f));
	}
	
	static JsonObject parseJson(Scanner scan) throws JsonParseException{
		scan.useDelimiter(Pattern.compile("[ \t\n\r]+|(?=[\"{}\\[\\],:])|(?<=[\"{}\\[\\],:])"));
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
		} else if (scan.hasNext(Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?"))){
			return parseNumber(scan);
		} else if (scan.hasNext("null")){
			scan.next();
			return null;
		}
		throw new JsonParseException("Could not parse " + scan.next() + " as json entity");
	}

	private static JsonEntity parseNumber(Scanner scan) {
		return new JsonNumber(Double.parseDouble(scan.next()));
	}

	private static JsonEntity parseString(Scanner scan) throws JsonParseException {
		String s = scan.next("[^\"\\r\\n]*");
		require("\"", scan);
		return new JsonString(s);
	}

	private static JsonEntity parseList(Scanner scan) throws JsonParseException {
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
				String name = scan.next();
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
