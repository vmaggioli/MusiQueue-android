<?php
require_once "connection.php";

apiDocs("
hubUsers:
	Params:
		hubId - the hub the song is getting added to
		phoneId - the phone id of the user requesting this list
	Returns on success:
		array of users:
		{
            id,
            name,
            last_active
		}
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

$result = $conn->query("
    SELECT
        id,
        name,
        last_active
    FROM Users
    WHERE
        Users.hub_id='$hubId'
        AND active = 1
        AND kicked = 0
");

if(!$result) {
    respondError("DB_ISSUE", "mysql error in hubUsers: " . $conn->error);
}

$arr = array();
while($assoc = mysqli_fetch_assoc($result)) {
    $arr[] = $assoc;
}

respondSuccess($arr);
