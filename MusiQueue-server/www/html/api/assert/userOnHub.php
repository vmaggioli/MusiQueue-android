<?php

apiDocsCouldError("USER_NOT_FOUND");

assertGiven('hubId');
assertGiven('userId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$userId = mysqli_real_escape_string($conn, $_REQUEST['userId']);

$result = mysqli_query($conn, "
	SELECT COUNT(*) as total
	FROM Users
	WHERE id = '$userId'
	AND hub_id = '$hubId'
");

if(mysqli_fetch_assoc($result)['total'] < 1) {
	respondError("USER_NOT_FOUND", "That user could not be found on that hub.");
}

?>
