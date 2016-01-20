package servlet;

/* *********************************************************************
* Course: CMPT350
* Purpose: the class for managing data that transfer between the database and client website
* Author: Weixiong Guan
* The method that relative to database:
************************************************************************
* 	protected void onTextMessage(CharBuffer buffer) throws IOException;
*		this method is get the JSON data from the client website,
*		and determin where it go and what to do (insert or delete) with the database
************************************************************************
*	private void helpingSendFuntion(String restaurant)
*		this method is get the data from the database,
*		and sent back to coordinated client website 
*/

import org.json.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.sql.*;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

public class CMPT350WebSocketInbound extends MessageInbound {

    private final String user;

    public CMPT350WebSocketInbound(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        CMPT350WebSocketInboundPool.addMessageInbound(this);
        super.onOpen(outbound);
    }

    @Override
    protected void onClose(int status) {;
        CMPT350WebSocketInboundPool.removeMessageInbound(this);
        super.onClose(status);
    }

    @Override
    protected void onBinaryMessage(ByteBuffer buffer) throws IOException {
        ;
    }

    @Override
 protected void onTextMessage(CharBuffer buffer) throws IOException {
        String msg = buffer.toString();
        String query;

        // insert the data to the database first
        JSONObject jsonobject = new JSONObject(msg);
        JSONArray jsonarray = jsonobject.getJSONArray("orderArray");
        Connection conn = CMPT350Database.getInstance();

        for (int i = 0; i < jsonarray.length(); i++) {
            int command = jsonarray.getJSONObject(i).getInt("command");
			
			// deternment the where the data go
            if (0 == command) { // 0 is a delete command
                // delete the order from the database
                query = "DELETE FROM orders WHERE ord_id=" + jsonarray.getJSONObject(i).getInt("orderId") + ";";
                CMPT350Database.modifyDatabase(query);
            } else if (1 == command) { // 1 is order command
                //insert to the database first
                query = "INSERT INTO orders (ord_fname, ord_lname, ord_phonenum,"
                        + "ord_picktime, ord_res, ord_totalprice, ord_food) "
                        + "VALUES (\'" + jsonarray.getJSONObject(i).getString("firstName") + "\',"
                        + "\'" + jsonarray.getJSONObject(i).getString("lastName") + "\',"
                        + jsonarray.getJSONObject(i).getLong("phone") + ","
                        + "\'" + jsonarray.getJSONObject(i).getString("pickUpTime") + "\',"
                        + "\'" + jsonarray.getJSONObject(i).getString("restaurant") + "\',"
                        + jsonarray.getJSONObject(i).getDouble("price") + ","
                        + "\'" + jsonarray.getJSONObject(i).getJSONArray("food").toString() + "\');";
                CMPT350Database.modifyDatabase(query);

            } else if (2 == command) {
                // for the first connection
            }
        }
        // send the order to the cordinate restaurant
        helpingSendFuntion("UmiSushi");
        helpingSendFuntion("Harveys");
        helpingSendFuntion("ExtremePita");
    }

 private void helpingSendFuntion(String restaurant) {
        // get back the result from the order for the UmiSushi
        String query = "SELECT ord_id, ord_fname, ord_lname, ord_phonenum, "
                + "ord_picktime, ord_res, ord_totalprice, ord_food "
                + "FROM orders WHERE ord_res=\'" + restaurant + "\' ORDER BY ord_picktime;";
        ResultSet result = CMPT350Database.runGetFromDatabaseSQL(query);
        String msg = "{\"orderArray\":["; //init the msg
       try {
            while (result.next()) {
                msg += "{\"command\":1,";
                msg += "\"orderId\":" + result.getInt("ord_id") + ", ";
                msg += "\"firstName\":\"" + result.getString("ord_fname") + "\", ";
                msg += "\"lastName\":\"" + result.getString("ord_lname") + "\", ";
                msg += "\"phone\":\"" + result.getLong("ord_phonenum") + "\", ";
                msg += "\"pickUpTime\":\"" + result.getTimestamp("ord_picktime").toString() + "\", ";
                msg += "\"restaurant\":\"" + restaurant + "\", ";
                msg += "\"price\":" + result.getFloat("ord_totalprice") + ", ";
                msg += "\"food\":" + result.getString("ord_food") + "}, ";
            }
            msg += "]}";
        } catch (SQLException e) {

        }
        CMPT350WebSocketInboundPool.sendMessageToUser(restaurant, msg);
    }
}
