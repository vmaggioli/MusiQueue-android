<?php
require_once "connection.php";

apiDocs("
recentHubs:
	Params:
		  phoneId - the user's phoneId
      networkName - name of the wifi network
	Returns on success:
		  array of hub names pertaining to the hubs the user has been active in
      within the past 7 days
");

assertGiven("phoneId");
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);
$networkName = mysqli_real_escape_string($conn, $_REQUEST['networkName']);

$result = mysqli_query($conn, "
SELECT
	Hubs.hub_name,
	Hubs.hub_pin IS NOT Null as hub_pin_required,
	creators.name as hub_creator_name,
	COUNT(joined.id) as is_rejoin
FROM Hubs
INNER JOIN Users creators ON Hubs.hub_creator_id = creators.id
LEFT JOIN Users joined ON joined.hub_id = Hubs.id AND joined.phone_id = '$phoneId'
WHERE Hubs.network_name = '$networkName'
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
