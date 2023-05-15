package CLI.model;

import com.google.gson.Gson;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Encodes a message to JSON format.
 */
public class MessageEncoder implements Encoder.Text<String> {

	private static final Gson gson = new Gson();

	@Override
	public String encode(String message) {
		return gson.toJson(message);
	}

	@Override
	public void init(EndpointConfig endpointConfig) {

	}


	@Override
	public void destroy() {

	}
}