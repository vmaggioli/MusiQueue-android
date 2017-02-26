<?php
apiDocsCouldError("HUB_CLOSED", "If the hub with that name is closed.");

assertGiven('hubName');
$result = mysqli_query($conn, "
	SELECT `closed` FROM Hubs
	WHERE `hub_name` = '$hubName'
");
$closed = mysqli_fetch_assoc($result)['closed'];
if($closed) {
	respondError("HUB_CLOSED", "This hub is currently closed.");
}
?>
