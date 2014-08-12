package cluedo.controller.network;

import java.util.HashMap;
import java.util.Map;

import util.json.JsonBoolean;
import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class MessageBuilder {
	
	private JsonObject message = new JsonObject();

	public MessageBuilder type(String type) {
		message.put("type", type);
		return this;
	}

	public MessageBuilder name(String name) {
		message.put("name", name);
		return this;
	}

	public MessageBuilder parameter(String paramName, int i) {
		return parameter(paramName, new JsonNumber(i));
	}
	
	public MessageBuilder parameter(String paramName, String s) {
		return parameter(paramName, new JsonString(s));
	}
	
	public MessageBuilder parameter(String paramName, boolean b) {
		return parameter(paramName, new JsonBoolean(b));
	}
	
	public MessageBuilder parameter(String paramName, JsonEntity val) {
		if (!message.containsKey("parameters")){
			message.put("parameters", new JsonObject());
		}
		JsonObject parameters = (JsonObject) message.get("parameters");
		parameters.put(paramName, val);
		return this;
	}
	
	public MessageBuilder returnValue(JsonEntity ret) {
		message.put("return", ret);
		return this;
	}

	
	public JsonObject build(){
		return message;
	}

	

	

	
}
