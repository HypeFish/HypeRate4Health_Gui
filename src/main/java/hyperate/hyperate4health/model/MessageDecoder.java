package hyperate.hyperate4health.model;

import com.google.gson.Gson;
import hyperate.hyperate4health.model.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;

public class MessageDecoder implements Decoder.Text<Message> {
    private static final Gson gson = new Gson();
    @Override
    public Message decode(String s) throws DecodeException {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(javax.websocket.EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}