<?php
require_once "connection.php";

apiDocs("
hubSongList:
	Params:
		hubId - the hub the song is getting added to
		phoneId - the phone id of the user requesting this list
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
		}
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

$result = $conn->query("
	SELECT
		Songs.id,
		Songs.song_id,
		Songs.song_title,
		Songs.time_added,
		Songs.up_votes,
		Songs.down_votes,
        Users.id as user_id,
        Users.name as user_name
	FROM Songs
	INNER JOIN Users on Users.phone_id = Songs.phone_id
	WHERE
		Songs.hub_id='$hubId'
		AND Users.hub_id='$hubId'
");

$arr = array();
while($assoc = mysqli_fetch_assoc($result)) {
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
