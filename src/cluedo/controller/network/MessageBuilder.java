package cluedo.controller.network;

import util.json.JsonBoolean;
import util.json.JsonEntity;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;

/** This class provides an easy way to build a json message to send data (events, requests, responses) 
 * between the server and client versions of the game
 * 
 * @author Simon Pinfold
 *
 */
public class MessageBuilder {
	
	private JsonObject message = new JsonObject();

	/** Set the type of the message */
	public MessageBuilder type(String type) {
		message.put("type", type);
		return this;
	}

	/** Set the name of the push/pull event. This should not be set on responses */
	public MessageBuilder name(String name) {
		message.put("name", name);
		return this;
	}

	/** Add a parameter to the parameter object of the message*/
	public MessageBuilder parameter(String paramName, int value) {
		return parameter(paramName, new JsonNumber(value));
	}
	
	/** Add a parameter to the parameter object of the message*/
	public MessageBuilder parameter(String paramName, String value) {
		return parameter(paramName, new JsonString(value));
	}
	
	/** Add a parameter to the parameter object of the message*/
	public MessageBuilder parameter(String paramName, boolean value) {
		return parameter(paramName, new JsonBoolean(value));
	}
	
	/** Add a parameter to the parameter object of the message*/
	public MessageBuilder parameter(String paramName, JsonEntity value) {
		if (!message.containsKey("parameters")){
			message.put("parameters", new JsonObject());
		}
		JsonObject parameters = (JsonObject) message.get("parameters");
		parameters.put(paramName, value);
		return this;
	}
	
	/** Set the return value for the message. This should only be used when returning data */
	public MessageBuilder returnValue(JsonEntity ret) {
		message.put("return", ret);
		return this;
	}

	/** Build the message */
	public JsonObject build(){
		return message;
	}

	

	

	
}
