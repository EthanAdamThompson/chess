package client;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.net.URI;
import jakarta.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebFacade {
    private Session session;
    private final Gson gson = new Gson();
    private final MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessage(ServerMessage message);
    }

    public WebFacade(int port, MessageHandler messageHandler) throws Exception {
        this.messageHandler = messageHandler;
        URI uri = new URI("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        messageHandler.onMessage(serverMessage);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.session = null;
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void connect(String authToken, int gameID) throws Exception {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendCommand(command);
    }
}
