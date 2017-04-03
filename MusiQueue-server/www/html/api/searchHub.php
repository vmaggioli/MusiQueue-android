<?php
require_once "connection.php";

apiDocs("
searchHub:
	Params:
		  hubName - the input for a hub name searched
		  phoneId - to calculate rejoins
	Returns on success:
		  array of hub names containing the searched input
");

assertGiven("hubName");
assertGiven("phoneId");
$hubName = mysqli_real_escape_string($conn, trim($_REQUEST['hubName']));
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$result = mysqli_query($conn, "
	SELECT
		Hubs.hub_name,
		Hubs.hub_pin IS NOT Null as hub_pin_required,
		creators.name as hub_creator_name,
		COUNT(joined.id) as is_rejoin
	FROM Hubs
	INNER JOIN Users creators ON Hubs.hub_creator_id = creators.id
	LEFT JOIN Users joined ON joined.hub_id = Hubs.id
	WHERE Hubs.hub_name LIKE '%$hubName%'
	AND joined.phone_id = '$phoneId'
	GROUP BY Hubs.hub_name
");
$arr = array();

if ($result->num_rows == 0) {
    respondSuccess($arr);
}

while($assoc = mysqli_fetch_assoc($result)) {
	$assoc['hub_pin_required'] = !!$assoc['hub_pin_required'];
	$assoc['is_rejoin'] = !!$assoc['is_rejoin'];
	if('is_rejoin') $assoc['hub_pin_required'] = false;
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
