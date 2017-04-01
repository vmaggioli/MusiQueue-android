<?php
require_once "connection.php";

apiDocs("
hubSongList:
	Params:
		hubId - the hub the song is getting added to
    songId - the songId of the song to remove
    phoneId - the phone id of the user adding the song
	Returns on success:
		array of songs:
		{
			id,
            song_id - string, youtube id value,
            song_title - string,
            time_added - timnestamp,
            up_votes - int,
            down_votes - int,
            user_id - The id of the Users table entry of the user who added the song,
            user_name - The name of the user who added the song
            rank - The song's rank. Higher is better
		}
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";


$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);
$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);


$removeResult = $conn->query("
  DELETE FROM Songs
  WHERE id = '$songId'
");

printf("res: %d", $removeResult);
echo "error: " . $conn->error;

require "hubSongList.php";
?>
