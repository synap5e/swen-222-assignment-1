package cluedo.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MinimalJson {

	public static void main(String[] args) throws JsonParseException{
		System.out.println(new MinimalJson().parse("{a : [1,2,3]}"));
	}

	public JsonObject parse(String s) throws JsonParseException{
		Scanner scan = new Scanner(s);
		scan.useDelimiter(Pattern.compile("\\s|\\{|\\[|\\]|\\}"));
		require("\\{", scan);
		return parseObject(scan);
	}

	private JsonEntity parseEntity(Scanner scan) throws JsonParseException {
		if (gobble("\\{", scan)){
			return parseObject(scan);
		} else if (gobble("\\[", scan)){
			return parseList(scan);
		} else if (gobble("\"", scan)){
			return parseString(scan);
		} else if (gobble(Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?"), scan)){
			return parseNumber(scan);
		}
		return null;
	}

	private JsonEntity parseNumber(Scanner scan) {
		return new JsonNumber(Double.parseDouble(scan.next()));
	}

	private JsonEntity parseString(Scanner scan) {
		return new JsonString(scan.next("[^\"\\r\\n]*"));
	}

	private JsonEntity parseList(Scanner scan) throws JsonParseException {
		List<JsonEntity> elems = new ArrayList<JsonEntity>();
		do {
			elems.add(parseEntity(scan));
		} while(gobble(",", scan));
		require("]", scan);
		return new JsonList(elems);
	}

	private JsonObject parseObject(Scanner scan) throws JsonParseException {
		Map<String, JsonEntity> elems = new HashMap<String, JsonEntity>();
		while (!gobble("}", scan)){
			String name = scan.next();
			require(":", scan);
			elems.put(name, parseEntity(scan));
		}
		return new JsonObject(elems);
	}

	private void require(String str, Scanner scan) throws JsonParseException {
		if (!gobble(str, scan)){
			throw new JsonParseException("Expected " + str + ", got " + scan.next());
		}
	}

	private boolean gobble(Pattern pat, Scanner scan) {
		if (scan.hasNext(pat)){
			scan.next(pat);
			return true;
		}
		return false;
	}

	private boolean gobble(String str, Scanner scan) {
		if (scan.hasNext(str)){
			scan.next(str);
			return true;
		}
		return false;
	}

}
