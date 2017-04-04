<?php
require_once "connection.php";

apiDocs("
removeSong:
	Params:
        hubId - the hub the song is getting added to
        songId - the songId of the song to remove
        phoneId - the phone id of the user adding the song
	Returns on success:
		See hubSongList.php
");

apiDocsCouldError("DB_ISSUE");

require_once "assert/hubExists.php";
require_once "assert/phoneIdIsCreator.php";
require_once "assert/hubIdOpen.php";
require_once "assert/songOnHub.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);

$removeResult = $conn->query("
    DELETE FROM Songs
    WHERE id = '$songId'
");

if(!$removeResult) {
    respondError("DB_ISSUE", "mysql error: ".$conn->error);
}

require "util/ensureSongPlaying.php";

require "hubSongList.php";

?>
