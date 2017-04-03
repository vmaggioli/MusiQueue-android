<?php
// given hubId in the request, ensure that the song at the top of the queue is playing.
assertGiven('hubId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

$result = $conn->query("
	UPDATE Songs
	SET playing = 1
	WHERE hub_id = '$hubId'
	ORDER BY $RANK_ORDER
	LIMIT 1
");

if(!$result) {
	respondError("DB_ISSUE", "mysql error in ensureSongPlaying: " . $conn->error);
}

?>
