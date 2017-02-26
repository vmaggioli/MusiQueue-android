<?php

apiDocsCouldError("SONG_NOT_FOUND");

assertGiven('hubId');
assertGiven('songId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);

$result = mysqli_query($conn, "
	SELECT COUNT(*) as total
	FROM Songs
	WHERE id = '$songId'
	AND hub_id = '$hubId'
");

if(mysqli_fetch_assoc($result)['total'] < 1) {
	respondError("SONG_NOT_FOUND", "That song could not be found on that hub.");
}

?>
