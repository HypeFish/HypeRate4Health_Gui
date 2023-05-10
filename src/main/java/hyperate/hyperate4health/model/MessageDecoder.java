package hyperate.hyperate4health.model;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<String> {
    private static final Gson gson = new Gson();

    @Override
    public String decode(String message) {
        return gson.fromJson(message, String.class);
    }

    @Override
    public boolean willDecode(String message) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }


}