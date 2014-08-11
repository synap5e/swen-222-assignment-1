package util.json;

import java.io.InputStream;
import java.util.Iterator;

public class JsonStreamReader implements Iterable<JsonObject>{

	private Iterator<JsonObject> it;

	public JsonStreamReader(final InputStream inputStream) {
		this.it = new Iterator<JsonObject>() {
			
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public JsonObject next() {
				try {
					return MinimalJson.parseJson(inputStream);
				} catch (JsonParseException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	public JsonObject next(){
		return it.next();
	}

	@Override
	public Iterator<JsonObject> iterator() {
		return it;
	}

	

}
