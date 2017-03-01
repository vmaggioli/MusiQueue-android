<?php
require_once "connection.php";

apiDocs("
searchHub:
	Params:
		  hubName - the input for a hub name searched
	Returns on success:
		  array of hub names containing the searched input
");

assertGiven("hubName");
$hubName = mysqli_real_escape_string($conn, $_REQUEST['hubName']);

$result = mysqli_query($conn, "
	SELECT
		Hubs.hub_name,
		Hubs.hub_pin IS NOT Null as hub_pin_required,
		Users.name as hub_creator_name
	FROM Hubs
	INNER JOIN Users ON Hubs.hub_creator_id = Users.id
	WHERE hub_name LIKE '%$hubName%'
");
$arr = array();

if ($result->num_rows == 0) {
    respondSuccess($arr);
}

while($assoc = mysqli_fetch_assoc($result)) {
	$assoc['hub_pin_required'] = !!$assoc['hub_pin_required'];
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
