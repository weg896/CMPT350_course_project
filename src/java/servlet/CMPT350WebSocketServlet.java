package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

@WebServlet("/message")
public class CMPT350WebSocketServlet extends WebSocketServlet {

    // for avoid the duplcate user name
    public static int ONLINE_USER_COUNT = 1;

    /**
     * get the user name from the session
     * @param request the request from the client side
     * @return the user name
     */
    public String getUser(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("user");
    }

    @Override
    protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
        return new CMPT350WebSocketInbound(this.getUser(request));
    }
}
