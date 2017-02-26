<?php

apiDocsCouldError("NOT_HUB_CREATOR", "If the given phoneId is not the hub creator.");
apiDocsCouldError("DB_ISSUE");

assertGiven("hubId");
assertGiven("phoneId");

require_once "hubExists.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

$result = mysqli_query($conn, "
	SELECT `phone_id`
	FROM `Users`
	WHERE `id` = (
		SELECT `hub_creator_id`
		FROM `Hubs`
		WHERE id = '$hubId'
	)
");

if(mysqli_num_rows($result) == 0) {
	respondError("DB_ISSUE", "Something strange happened, we know that this hub must exist, but it appears to not have a creator in the Users table.");
}

$phoneId = mysqli_fetch_assoc($result)['phone_id'];
if($phoneId != $_REQUEST['phoneId']) {
	respondError("NOT_HUB_CREATOR", "You must be the creator of the hub to perform this action");
}
?>
