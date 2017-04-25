<?php
require_once "connection.php";

apiDocs("
createHub:
    Params:
        hubName - the name the hub should be called
        hubPin (optional) - the pin required for joining the hub
        phoneId - the user's phoneId
        username - the name the user wants to be called
        lat - the latitude of the created hub (0 if user didn't allow location)
        long - the longitude of the created hub (0 if user didn't allow location)
        networkName - the name of the wifi network the hubCreator is apart of (0 if user didn't allow wifi)

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
$lat = mysqli_real_escape_string($conn, $_REQUEST['lat']);
$long = mysqli_real_escape_string($conn, $_REQUEST['long']);
if(array_key_exists('networkName', $_REQUEST)) {
	$networkName = mysqli_real_escape_string($conn, $_REQUEST['networkName']);
}else{
	$networkName = '';
}

// check if this hub name is available
$result = mysqli_query($conn, "
	SELECT
		`Hubs`.*,
		`Users`.`phone_id` as creator_phone_id
	FROM Hubs
	INNER JOIN `Users` ON `Users`.`id` = `Hubs`.`hub_creator_id`
	WHERE `hub_name` = '$hubName'
	LIMIT 1
");
$taken = mysqli_num_rows($result);

if($taken) {
	$hub = mysqli_fetch_assoc($result);
	if($hub['creator_phone_id'] != $_REQUEST['phoneId']) {
		respondError("HUB_NAME_TAKEN", "That hub name is already taken. Please choose a different name.");
	}
	// reopen the hub
	$hubId = $hub['id'];

	// update hub info
	$result = mysqli_query($conn, "
		UPDATE Hubs
		SET
			closed = 0,
			time_last_active = CURRENT_TIME(),
			hub_pin = ".($hasPin ? "'$hubPin'" : 'NULL')."
		WHERE hub_name = '$hubName'
	");

	if(!$result) {
		respondError("DB_ISSUE", "Could not update hub info to reopen");
	}

	// update creator info
	$result = mysqli_query($conn, "
		UPDATE Users
		SET
			name = '$username',
			last_active = CURRENT_TIME()
		WHERE phone_id = '$phoneId'
		AND hub_id = '$hubId'
	");

	if(!$result) {
		respondError("DB_ISSUE", "Could not update creator info");
	}
}else{
	// create the hub
	$result = mysqli_query($conn, "
		INSERT INTO Hubs
		(hub_name, time_last_active, hub_pin, latitude, longitude, network_name)
		VALUES ('$hubName', CURRENT_TIME(), ".($hasPin ? "'$hubPin'" : 'NULL').", $lat, $long, '$networkName')
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
}

// return
respondSuccess(array(
    "hub_id" => $hubId
));

?>
