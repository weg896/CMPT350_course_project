package servlet;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CMPT350WebSocketInboundPool {

    /**
     * the connection map container, work as a pool/
     */
    private static final Map<String, CMPT350WebSocketInbound> connections = new HashMap<String, CMPT350WebSocketInbound>();

    /**
     * add the connection to the pool
     *
     * @param inbound the inbound
     */
    public static void addMessageInbound(CMPT350WebSocketInbound inbound) {
        // System.out.println("user : " + inbound.getUser() + " join..");
        connections.put(inbound.getUser(), inbound);
    }

    /**
     * get all the user that connected to the server.
     *
     * @return the string set of all the user
     */
    public static Set<String> getOnlineUser() {
        return connections.keySet();
    }

    /**
     * remove the connection from the server
     *
     * @param inbound
     */
    public static void removeMessageInbound(CMPT350WebSocketInbound inbound) {
        connections.remove(inbound.getUser());
    }

    /**
     * send the message to a specific user
     *
     * @param user the user who receive the message
     * @param message the message that needed to be send
     */
    public static void sendMessageToUser(String user, String message) {
        try {
            CMPT350WebSocketInbound inbound = connections.get(user);
            if (inbound != null) {
                inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send message to all user
     *
     * @param message the message that needed to be send
     */
    public static void sendMessage(String message) {
        try {
            Set<String> keySet = connections.keySet();
            for (String key : keySet) {
                CMPT350WebSocketInbound inbound = connections.get(key);
                if (inbound != null) {
                    inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
