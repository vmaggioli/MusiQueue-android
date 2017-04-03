<?php
require_once "connection.php";

apiDocs("
recentHubs:
	Params:
		  phoneId - the user's phoneID
	Returns on success:
		  array of hub names pertaining to the hubs the user has been active in
      within the past 7 days
");

assertGiven("phoneId");

$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$result = mysqli_query($conn, "
	SELECT
		Hubs.hub_name,
		creators.name as hub_creator_name
  	FROM Hubs
  	INNER JOIN Users u ON u.hub_id = Hubs.id
  	INNER JOIN Users creators ON Hubs.hub_creator_id = creators.id
  	WHERE u.phone_id = '$phoneId'
  	AND DATEDIFF(CURRENT_TIME(), u.last_active) <= 7
");
$arr = array();

if ($result->num_rows == 0) {
    respondSuccess($arr);
}

while($assoc = mysqli_fetch_assoc($result)) {
	$assoc['hub_pin_required'] = false;
	$arr[] = $assoc;
}

respondSuccess($arr);lo
?>
