package data;

import burp.api.montoya.ui.contextmenu.WebSocketMessage;
import burp.api.montoya.websocket.Direction;

import java.time.LocalDateTime;

public class WebSocketMessageData
{
    private final String message;
    private final Direction direction;
    private final LocalDateTime timestamp;
    private final String upgradeRequestUrl;

    public WebSocketMessageData(String message, Direction direction, LocalDateTime timestamp, String upgradeRequestUrl)
    {
        this.message = message;
        this.direction = direction;
        this.timestamp = timestamp;
        this.upgradeRequestUrl = upgradeRequestUrl;
    }

    public static WebSocketMessageData from(WebSocketMessage webSocketMessage)
    {
        return new WebSocketMessageData(
                webSocketMessage.payload().toString(),
                webSocketMessage.direction(),
                LocalDateTime.now(),
                webSocketMessage.upgradeRequest().url()
        );
    }

    public String getMessage()
    {
        return message;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public String getUpgradeRequestUrl()
    {
        return upgradeRequestUrl;
    }

    public boolean isClientToServer()
    {
        return direction == Direction.CLIENT_TO_SERVER;
    }

    public boolean isServerToClient()
    {
        return direction == Direction.SERVER_TO_CLIENT;
    }
} 