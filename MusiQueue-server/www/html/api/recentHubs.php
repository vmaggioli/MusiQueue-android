<?php
require_once "connection.php";

apiDocs("
recentHubs:
	Params:
		  phoneID - the user's phoneID
	Returns on success:
		  array of hub names pertaining to the hubs the user has been active in
      within the past 7 days
");


$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$result = mysqli_query($conn, "
	SELECT Hubs.hub_name
  FROM Hubs
  INNER JOIN Users
  ON Users.hub_id = Hubs.id
  WHERE Users.phone_id LIKE '%$phoneId%'
  AND DATEDIFF(CURRENT_TIME(), Users.last_active) <= 7
");



$arr = array();

if ($result->num_rows == 0) {
    respondSuccess($arr);
}

while($assoc = mysqli_fetch_assoc($result)) {
	//$assoc['hub_pin_required'] = !!$assoc['hub_pin_required'];
	$arr[] = $assoc;
}

respondSuccess($arr);

?>
