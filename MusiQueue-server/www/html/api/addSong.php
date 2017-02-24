<?php
require_once "connection.php";

apiDocs("
addSong:
	Params:
		hubId - the hub the song is getting added to
		phoneId - the phone id of the user adding the song
		songId - the youtube string id for the video being added
		songTitle - title of the song being added
	Returns on success:
		See hubSongList endpoint.
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";
require_once "assert/songGiven.php";

// check for duplicates
$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);

$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Songs WHERE hub_id = '$hubId' AND song_id = '$songId' ");
$data = mysqli_fetch_assoc($result);
if($data['total'] > 0) {
	respondError("SONG_ALREADY_IN_QUEUE", "That song is already in this queue.");
}

// add the song
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);
$songTitle = mysqli_real_escape_string($conn, $_REQUEST['songTitle']);

$result = $conn->query("
	INSERT INTO
	Songs (`hub_id`, `user_id`, `song_id`, `song_title`)
	VALUES ('$hubId', (
		SELECT `id` FROM `Users` WHERE `phone_id` = '$phoneId' AND `hub_id` = '$hubId' LIMIT 1
	), '$songId', '$songTitle')
");

// seemes successful, return the list of songs in the hub
require "hubSongList.php";

?>
