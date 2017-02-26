<?php
apiDocsCouldError("HUB_CLOSED", "If the hub with that id is closed.");

assertGiven('hubId');
$result = mysqli_query($conn, "
	SELECT `closed` FROM Hubs
	WHERE `id` = '$hubId'
");
$closed = mysqli_fetch_assoc($result)['closed'];
if($closed) {
	respondError("HUB_CLOSED", "This hub is currently closed.");
}
?>
