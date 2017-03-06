<?php
require_once "connection.php";

apiDocs("
joinHub:
    Params:
        hubName - the hub name of the hub the user is joining
        hubPin (optional) - the pin required for joining the hub
        phoneId - the user's phoneId
        username - the name the user wants to be called
    Returns on success:
        info about the hub we connect to:
        {
            hub_id - int
            is_creator - whether the joining user is the creator of the hub
        }
");
apiDocsCouldError("HUB_NOT_FOUND");
apiDocsCouldError("HUB_CLOSED");
apiDocsCouldError("USER_KICKED", "If the user cannot join the hub because they were kicked");
apiDocsCouldError("HUB_PIN_WRONG");

assertGiven("phoneId");
assertGiven("username");
require_once "assert/hubNameExists.php";
require_once "assert/hubNameOpen.php";

$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);
$username = mysqli_real_escape_string($conn, $_REQUEST['username']);
$hubName = mysqli_real_escape_string($conn, $_REQUEST['hubName']);
if(isset($_REQUEST['hubPin'])) {
    $hubPin = mysqli_real_escape_string($conn, $_REQUEST['hubPin']);
}else{
    $hubPin = "0000";
}

// get hub info
$result = mysqli_query($conn, "
    SELECT
    	`Hubs`.*,
    	`Users`.`phone_id` as `hub_creator_phone_id`
    FROM `Hubs`
    INNER JOIN `Users` ON `Users`.`hub_creator_id` = `Users`.`id`
    WHERE
        `Hubs`.`hub_name` = '$hubName'
");
$hub = mysqli_fetch_assoc($result);

// check if the user is already in the hub
$result = mysqli_query($conn, "
    SELECT * FROM Users
    WHERE phone_id = '$phoneId'
    AND `hub_id` = ".$hub['id']."
");

if(mysqli_num_rows($result)) {
    // the user is already in the hub, or has been in the past
    $userInfo = mysqli_fetch_assoc($result);
    if($userInfo['kicked'] == 1) {
        respondError('USER_KICKED', "You cannot join this hub because you have been kicked from it.");
    }
    // update the user information, don't require the pin again
    $result = mysqli_query($conn, "
        UPDATE Users
        SET `name` = '$username',
            `active` = 1,
            `last_active` = CURRENT_TIME()
        WHERE `id` = ".$userInfo['id']."
    ");
}else{
    // the user is not in the hub
    // check if the hub pin is correct

    if($hub['hub_pin'] != $hubPin && $hub['hub_pin'] != null) {
        respondError('HUB_PIN_WRONG', "The pin provided for this hub is not correct.");
    }
    // add the user to the hub
    $result = mysqli_query($conn, "
        INSERT INTO Users (phone_id, hub_id, name)
        VALUES ('$phoneId', '".$hub['id']."', '$username')
    ");
}

respondSuccess(array(
    "hub_id" => $hub['id'],
    "is_creator" => ($hub['hub_creator_phone_id'] == $_REQUEST['phoneId'])
));

?>
