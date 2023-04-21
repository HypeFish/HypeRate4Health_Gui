package hyperate.hyperate4health.model;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;

public class MessageEncoder implements Encoder.Text<String> {
    private static final Gson gson = new Gson();

    @Override
    public String encode(String message) throws EncodeException{
        return gson.toJson(message);
    }

    @Override
    public void init(javax.websocket.EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}