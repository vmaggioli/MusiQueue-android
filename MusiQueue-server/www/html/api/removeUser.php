<?php
require_once "connection.php";

apiDocs("
removeUser:
	Params:
        hubId - the hub id
        phoneId - the phone id of the hub admin
        userId - the userId of the user that should be removed
	Returns on success:
		See hubUsers.php
");

apiDocsCouldError("DB_ISSUE");

require_once "assert/hubExists.php";
require_once "assert/phoneIdIsCreator.php";
require_once "assert/hubIdOpen.php";
require_once "assert/userOnHub.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$userId = mysqli_real_escape_string($conn, $_REQUEST['userId']);
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$removeResult = $conn->query("
    UPDATE Users
    SET kicked = 1, active = 0
    WHERE id = '$userId'
");

if(!$removeResult) {
    respondError("DB_ISSUE", "mysql error: ".$conn->error);
}

require "hubUsers.php";

?>
