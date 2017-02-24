<?php
require_once "connection.php";

apiDocs("
voteUpSong:
	Params:
		hubId - the hub the song is on
		phoneId - the phone id of the user voting
		songId - the id of the song entry
	Returns on success:
		See hubSongList endpoint.
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";
require_once "assert/songOnHub.php";

$result = mysqli_query($conn, "
	UPDATE Songs
	SET up_votes = up_votes + 1
	WHERE id = '$songId'
");

if(!$result) {
	respondError("DB_ISSUE", "Could not increase vote total on song");
}

require "hubSongList.php";

?>
