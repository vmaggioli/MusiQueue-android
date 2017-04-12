<?php
require_once "connection.php";

apiDocs("
searchHub:
	Params:
		  otherPhoneId - the id of the potential creator
		  phoneId - to calculate rejoins
	Returns on success:
		  array of hub names containing the searched input
");

assertGiven("otherPhoneId");
assertGiven("phoneId");
$otherPhoneId = mysqli_real_escape_string($conn, trim($_REQUEST['otherPhoneId']));
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$result = mysqli_query($conn, "
	SELECT
		Hubs.hub_name,
		Hubs.hub_pin IS NOT Null as hub_pin_required,
		creators.name as hub_creator_name,
		COUNT(joined.id) as is_rejoin
	FROM Hubs
	INNER JOIN Users creators ON Hubs.hub_creator_id = creators.id
	LEFT JOIN Users joined ON joined.hub_id = Hubs.id AND joined.phone_id = '$phoneId'
	WHERE creators.phone_id LIKE '%$otherPhoneId%'
	GROUP BY Hubs.hub_name
");
$arr = array();

if ($result->num_rows == 0) {
    respondSuccess($arr);
}

while($assoc = mysqli_fetch_assoc($result)) {
	$assoc['hub_pin_required'] = !!$assoc['hub_pin_required'];
	$assoc['is_rejoin'] = !!$assoc['is_rejoin'];
	if($assoc['is_rejoin']) $assoc['hub_pin_required'] = false;
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
