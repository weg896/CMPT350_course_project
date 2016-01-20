var socket;

var totalPrice = 0;
var para_array = new Array();

var head;
var totalOrder = new Array();

var order1 = new Array();
var order2 = new Array();
var order3 = new Array();

var food1 = new Array();
var food2 = new Array();
var food3 = new Array();

totalOrder[0] = order1;
totalOrder[1] = order2;
totalOrder[2] = order3;

// command
order1[0] = 1;
order2[0] = 1;
order3[0] = 1;

// rest name
order1[6] = 'UmiSushi';
order2[6] = 'Harveys';
order3[6] = 'ExtremePita';

// price
order1[7] = 0;
order2[7] = 0;
order3[7] = 0;

// food order
order1[8] = food1;
order2[8] = food2;
order3[8] = food3;

/*
 * JSon Format:
 * {"orderArray": [
 *        {"command": 1,
 *           "orderId": 123123,
 *           "firstName": "Tom",
 *            "lastName": "Lee",
 *            "phone": 3388438,
 *            "pickUpTime": "2013-02-01 00:00:00",
 *            "restaurant": "UmiSushi",
 *            "price":55.5,
 *            "food": [
 *                {"foodName": "chicken", "quantity": 5},
 *                {"foodName": "egg", "quantity": 2},
 *                {"foodName": "cock", "quantity": 1}
 *            ]
 *        },
 *        {
 *            "command": 1,
 *            "orderId": 456456,
 *            "firstName": "Tom",
 *            "lastName": "Lee",
 *            "phone": 3388438,
 *            "pickUpTime": "2013-02-01",
 *            "restaurant": "Harveys",
 *            "price":12.5,
 *            "food": [
 *                {"foodName": "fruit", "quantity": 5},
 *                {"foodName": "juice", "quantity": 2},
 *                {"foodName": "coffee", "quantity": 1}
 *            ]
 *        }
 * ]}
 */


/*
 * add selected food into shopping cart
 */
function addToCart(resturantName, foodName, foodPrice) {
    alert("you order a "+foodName);

    $('#cart_list').append('<li id="' + foodName + '"><a><h4>' + foodName + '</h4><p>' + resturantName +
        '</p><p> $' + foodPrice + '</p><a href="#" class="ui-icon-delete" onclick="removeFood(' + "'" + resturantName + "'," + "'" + foodName + "'," + foodPrice + ')"></a></a></li>');

    culculateTotalPrice(foodPrice);

    // add data into array
    if (resturantName == 'UmiSushi') {
        order1[7] += foodPrice;
        food1[food1.length] = foodName;
    } else if (resturantName == 'Harveys') {
        order2[7] += foodPrice;
        food2[food2.length] = foodName;
    } else {
        order3[7] += foodPrice;
        food3[food3.length] = foodName;
    }
    $('#cart_list').listview('refresh');
}

/*
 * remove selected food from shopping cart
 */
function removeFood(resturantName, foodName, foodPrice) {

    var removeFood = document.getElementById(foodName);
    removeFood.parentElement.removeChild(removeFood);

    foodPrice = foodPrice * (-1);
    culculateTotalPrice(foodPrice);

    // modify data in array
    if (resturantName == 'UmiSushi') {
        foodPrice = parseFloat(foodPrice.toFixed(2));
        order1[7] += foodPrice;
        delete food1[food1.indexOf(foodName)];
    } else if (resturantName == 'Harveys') {
        foodPrice = parseFloat(foodPrice.toFixed(2));
        order2[7] += foodPrice;
        delete food2[food2.indexOf(foodName)];
    } else {
        foodPrice = parseFloat(foodPrice.toFixed(2));
        order3[7] += foodPrice;
        delete food3[food3.indexOf(foodName)];
    }
    $('#cart_list').listview('refresh');
}

/*
 * calculate the total price of the shopping cart
 */
function culculateTotalPrice(price) {
    var totalPrice = parseFloat(document.getElementById('price1').innerHTML);
    var total = document.getElementById('price1');
	var total2 =  document.getElementById('price2');
    totalPrice = parseFloat(totalPrice) + parseFloat(price);
    total.innerHTML = totalPrice.toFixed(2);
	total2.innerHTML = totalPrice.toFixed(2);
}

function checkOut() {
	
	if((document.getElementById("firstname").value == "") ||
	(document.getElementById("lastname").value == "" ) ||
	(document.getElementById("phonenum").value == "" ) ||
	(document.getElementById("date-1").value == "" ) ||
	(document.getElementById("bday").value == "" )){
		alert("please fill in your information");
		return;
	}
	
	for(var i=0;i<totalOrder.length;i++){
		// order ID
		totalOrder[i][1] = 1;
		// First Name
		totalOrder[i][2] = document.getElementById("firstname").value;
		// Last Name
		totalOrder[i][3] = document.getElementById("lastname").value;
		// Phone Number
		totalOrder[i][4] = document.getElementById("phonenum").value;
		//Pickup date
		totalOrder[i][9] = document.getElementById("date-1").value;
		// Pickup Time
		totalOrder[i][5] = document.getElementById("bday").value;
	}
    var sentoutdata = "{\"orderArray\":[";
	
	for(var j=0; j<totalOrder.length; j++){
		if(totalOrder[j][8].length != 0){
			sentoutdata += "{\"command\":1,";
			sentoutdata += "\"orderId\":0001,";
			sentoutdata += "\"firstName\":\"" + totalOrder[j][2] + "\",";
			sentoutdata += "\"lastName\":\"" + totalOrder[j][3] + "\",";
			sentoutdata += "\"phone\":" + totalOrder[j][4] + ",";
			sentoutdata += "\"pickUpTime\":\"" + totalOrder[j][9] +" "+ totalOrder[j][5] + "\",";
			sentoutdata += "\"restaurant\":\"" + totalOrder[j][6] +"\",";
			sentoutdata += "\"price\":" + totalOrder[j][7] + ",";
			sentoutdata += "\"food\":[";

			for (var i = 0; i < totalOrder[j][8].length; i++) {
				if( 0 != "undefined".localeCompare(totalOrder[j][8][i])){
					sentoutdata += "{\"foodName\":\"" + totalOrder[j][8][i] + "\",\"quantity\":1},";
				}
			}
			sentoutdata += "]},";
		}
	}
	sentoutdata += "]}";
    socket.send(sentoutdata);
	alert("Thank you!");
}

	 var connection = function() {
                try {
                    socket = new WebSocket('ws://localhost:8084/temp350/message');
                } catch (e) {
                    alert("server error!");
                }

                socket.onopen = function(event) {
					document.getElementById("order").disabled=false;
					document.getElementById("connect").disabled=true;
                };

                socket.onmessage = function(event) {
                };

                socket.onclose = function(event) {
                    alert("close");
					document.getElementById("order").disabled=true;
					document.getElementById("connect").disabled=false;
                };
            }