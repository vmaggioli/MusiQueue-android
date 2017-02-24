<?php

assertGiven('phoneId');
assertGiven('hubId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Users WHERE hub_id = '$hubId' AND phone_id = '$phoneId' AND `active`=1 AND `kicked`=0 ");
$data = mysqli_fetch_assoc($result);
if($data['total'] < 1) {
	respondError("USER_NOT_CONNECTED", "You must be connected to that hub to add a song to it.");
}
?>
