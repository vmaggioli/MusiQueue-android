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
            rank - The song's rank. Higher is better
		}
");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

// We do an inner join here to convert Users.phone_id into the
// more anonymous `Users`.`id`, the id of the connection between
// the user who added the song and this hub. The logic being
// that we don't want to shout user's phone id out to the public,
// and all the info the client needs to kick somebody is their
// `Users`.`id`. We also add the pretty user_name. Woo.
$result = $conn->query("
	SELECT
		Songs.id,
		Songs.song_id,
		Songs.song_title,
		Songs.time_added,
		Songs.up_votes,
		Songs.down_votes,
        Users.id as user_id,
        Users.name as user_name,
        (-UNIX_TIMESTAMP(Songs.time_added) + (Songs.up_votes - Songs.down_votes)*60) as rank
	FROM Songs
	INNER JOIN Users on Users.id = Songs.user_id
	WHERE
		Songs.hub_id='$hubId'
	ORDER BY rank DESC
");

$arr = array();
while($assoc = mysqli_fetch_assoc($result)) {
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
