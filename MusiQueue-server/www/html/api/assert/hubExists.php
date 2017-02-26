<?php
apiDocsCouldError("HUB_NOT_FOUND", "If a hub with that id cannot be found.");

assertGiven('hubId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Hubs WHERE id = '$hubId'");
$data = mysqli_fetch_assoc($result);
if($data['total'] < 1) {
	respondError("HUB_NOT_FOUND", "A hub with that id does not exist.");
}
?>
