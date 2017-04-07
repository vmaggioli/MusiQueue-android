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
    INNER JOIN `Users` ON `Hubs`.`hub_creator_id` = `Users`.`id`
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
            `last_active` = NOW()
        WHERE `id` = ".$userInfo['id']."
    ");
}else{
    // the user is not in the hub
    // check if the hub pin is correct
    // mitigate against brute force attacks
    if($hub['connection_failures'] < $hub['connection_successes'] * 2 + 5) {
        sleep(1);
    }else{
        $ip = mysqli_real_escape_string($conn, $_SERVER['REMOTE_ADDR']);

        $timeResult = mysqli_query($conn, "SELECT *, TIMESTAMPDIFF(SECOND, NOW(), time) as secs FROM IpLockout WHERE ip = '$ip'");
        if(mysqli_num_rows($timeResult)) {
            $secs = mysqli_fetch_assoc($timeResult)['secs'];
            $secs = max(0, $secs);
            $secs += 6;
        }else{
            $secs = 6;
        }
        mysqli_query($conn, "REPLACE INTO IpLockout SET time = NOW() + INTERVAL $secs SECOND, ip = '$ip'");
        sleep($secs);
    }

    if($hub['hub_pin'] != $hubPin && $hub['hub_pin'] != null) {

        $hubId = $hub['id'];
        mysqli_query($conn, "UPDATE Hubs SET connection_failures = connection_failures + 1 WHERE id = $hubId");

        respondError('HUB_PIN_WRONG', "The pin provided for this hub is not correct.");
    }
    // add the user to the hub
    $result = mysqli_query($conn, "
        INSERT INTO Users (phone_id, hub_id, name)
        VALUES ('$phoneId', '".$hub['id']."', '$username')
    ");

    $hubId = $hub['id'];
    mysqli_query($conn, "UPDATE Hubs SET connection_successes = connection_successes + 1 WHERE id = $hubId");
}

respondSuccess(array(
    "hub_id" => $hub['id'],
    "is_creator" => ($hub['hub_creator_phone_id'] == $_REQUEST['phoneId'])
));

?>
