<?php
require_once "connection.php";

apiDocs("
createHub:
    Params:
        hubName - the name the hub should be called
        hubPin (optional) - the pin required for joining the hub
        phoneId - the user's phoneId
        username - the name the user wants to be called
    Returns on success:
        info about the hub we connect to:
        {
            hub_id - int
        }
    Common errors:
        HUB_NAME_TAKEN
    // TODO: add hub location?
");

assertGiven("hubName");
assertGiven("phoneId");
assertGiven("username");

$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);
$username = mysqli_real_escape_string($conn, $_REQUEST['username']);
$hubName = mysqli_real_escape_string($conn, $_REQUEST['hubName']);
if(isset($_REQUEST['hubPin'])) {
	$hasPin = true;
    $hubPin = mysqli_real_escape_string($conn, $_REQUEST['hubPin']);
}else{
	$hasPin = false;
}

// check if this hub name is available
$result = mysqli_query($conn, "
	SELECT COUNT(*) as total
	FROM Hubs
	WHERE `hub_name` = '$hubName'
");
$taken = mysqli_fetch_assoc($result)['total'];

if($taken) {
	respondError("HUB_NAME_TAKEN", "That hub name is already taken. Please choose a different name.");
}

// create the hub
$result = mysqli_query($conn, "
	INSERT INTO Hubs
	(hub_name, time_last_active, hub_pin)
	VALUES ('$hubName', CURRENT_TIME(), ".($hasPin ? "'$hubPin'" : 'NULL').")
");
$hubId = mysqli_insert_id($conn);

// create the user
$result = mysqli_query($conn, "
    INSERT INTO Users (phone_id, hub_id, name)
    VALUES ('$phoneId', '".$hubId."', '$username')
");
$userId = mysqli_insert_id($conn);

// update the hub owner
$result = mysqli_query($conn, "
	UPDATE Hubs
	SET hub_creator_id = $userId
	WHERE id = $hubId
");

// return
respondSuccess(array(
    "hub_id" => $hubId
));

?>
