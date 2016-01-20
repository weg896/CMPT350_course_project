<%@ page language="java" pageEncoding="UTF-8" import="servlet.CMPT350WebSocketServlet"%>
<%
    String path = request.getContextPath();
    String WsBasePath = "ws://" + request.getServerName() + ":"
            + request.getServerPort() + path + "/";

    String user = (String) session.getAttribute("user");
    if (user == null) {
        user = "Harveys";
        CMPT350WebSocketServlet.ONLINE_USER_COUNT++;
        session.setAttribute("user", user);
    }
    pageContext.setAttribute("user", user);
%>
<html>
    <head>
        <title>Orders</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" type="text/css" href="jquery.mobile-1.4.2/jquery.mobile-1.4.2.css"/>
        <script type="text/javascript" src="jquery-2.1.0.min.js"></script>
        <script type="text/javascript" src="jquery.mobile-1.4.2/jquery.mobile-1.4.2.min.js"></script>
        <script>
            var socket;

            // input a json string, then the screen will show up all orders
            function writeToTheAccordionDiv(object) {
                var jsonobject = eval("(" + object + ")");
                var receive_text = "";
                for (var i = 0; i < jsonobject.orderArray.length; i++) {
                    receive_text += "<div id=\"set" + jsonobject.orderArray[i].orderId + "\" data-role=\"collapsible\" data-collapsed=\"true\">";
                    receive_text += "<h3>Order: " + jsonobject.orderArray[i].orderId + "</h3>";
                    receive_text += "<div>Name: " + jsonobject.orderArray[i].firstName + " " + jsonobject.orderArray[i].lastName + "<br/>";
                    receive_text += "Phone: " + jsonobject.orderArray[i].phone + "<br/>";
                    receive_text += "Pick Up Time: " + jsonobject.orderArray[i].pickUpTime + "<br/>";
                    receive_text += "Total Price: " + jsonobject.orderArray[i].price + "<br/>";
                    receive_text += "Food:<br/>";
                    for (var j = 0; j < jsonobject.orderArray[i].food.length; j++) {
                        receive_text += "      " + jsonobject.orderArray[i].food[j].foodName + ": " + jsonobject.orderArray[i].food[j].quantity + " order<br/>";
                    }
                    receive_text += "<button onclick=\"deleteOrder(" + jsonobject.orderArray[i].orderId + ")\">finish</button></div>";
                    receive_text += "</div></div>";
                }
                document.getElementById("set").innerHTML = receive_text;
                $("#set").append("").collapsibleset('refresh');
            }

            function deleteOrder(orderId) {
                var send_text = '{' +
                        '"orderArray": [' +
                        '{' +
                        '"command":0,' +
                        '"orderId":' + orderId + ',' +
                        '"firstName": "delete",' +
                        '"lastName": "delete",' +
                        '"phone": 3388438,' +
                        '"pickUpTime": "delete",' +
                        '"restaurant": "delete",' +
                        '"food": [' +
                        '{"foodName": "delete", "quantity": 1},' +
                        '{"foodName": "delete", "quantity": 1}' +
                        ']' +
                        '}]}';
                socket.send(send_text);
            }

            // JSON.stringify(message);
            var connection = function() {
                try {
                    socket = new WebSocket('<%=WsBasePath + "message"%>');
                } catch (e) {
                    alert("server error!");
                }

                socket.onopen = function(event) {
                    refreshPaga();
                    document.getElementById("connect").disabled = true; 
                };

                socket.onmessage = function(event) {
                    //document.getElementById("set").innerHTML = event.data; 
                    
                    writeToTheAccordionDiv(event.data);
                    //alert("end");
                };

                socket.onclose = function(event) {
                    alert("close");
                };
            }

            function refreshPaga() {
                var send_text = '{' +
                        '"orderArray": [' +
                        '{' +
                        '"command":3,' +
                        '"orderId":10000001,' +
                        '"firstName": "connect",' +
                        '"lastName": "connect",' +
                        '"phone": 3388438,' +
                        '"pickUpTime": "connect",' +
                        '"restaurant": "Harveys",' +
                        '"food": [' +
                        '{"foodName": "connect", "quantity": 1},' +
                        '{"foodName": "connect", "quantity": 1}' +
                        ']' +
                        '}]}';
                socket.send(send_text);
            }
        </script>
    </head>

    <body>
        <div id="page1" data-role="page">
            <div data-role="header"> 
                <h1>Harvey's Order List</h1>
            </div>
            <div data-role="content"> 
                <div id="set" data-role="collapsible-set" data-content-theme="d">
                </div>
            </div>
            <div data-role="footer">
                <button id="connect" onclick="connection()">Connection To Server</button>
                <!--button id="connect" onclick="refreshPaga()">Connection To Server</button-->
            </div>

        </div>
    </body>
</html>