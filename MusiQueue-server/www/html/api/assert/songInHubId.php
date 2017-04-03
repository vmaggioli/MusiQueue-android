<?php
apiDocsCouldError("SONG_NOT_FOUND", "If a song with that id in the given hub cannot be found.");

assertGiven('hubId');
assertGiven('songId');

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);
$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Songs WHERE id = '$songId' AND hub_id='$hubId'");
$data = mysqli_fetch_assoc($result);
if($data['total'] < 1) {
	respondError("SONG_NOT_FOUND", "A song with that id does not exist in that hub.");
}
?>
