<?php
assertGiven('hubName');

$hubName = mysqli_real_escape_string($conn, $_REQUEST['hubName']);
$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Hubs WHERE hub_name = '$hubName'");
$data = mysqli_fetch_assoc($result);
if($data['total'] < 1) {
	respondError("HUB_NOT_FOUND", "A hub with that name does not exist.");
}
?>
