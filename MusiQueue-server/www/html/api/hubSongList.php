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
            time_added - timestamp,
            up_votes - int,
            down_votes - int,
            user_id - The id of the Users table entry of the user who added the song,
            user_name - The name of the user who added the song
            rank - The song's rank. Higher is better
            voted - -1/0/1, the user's vote on this song. (-1 is downvote, 0 is no vote, 1 is upvote)
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
		Songs.playing,
        Users.id as user_id,
        Users.name as user_name,
        $RANK_FORMULA as rank,
        IFNULL(Votes.vote, 0) as voted
	FROM Songs
	INNER JOIN Users on Users.id = Songs.user_id
	LEFT JOIN Votes on Songs.id = Votes.song_id AND Votes.phone_id = '$phoneId'
	WHERE
		Songs.hub_id='$hubId'
	ORDER BY
		$RANK_ORDER
");

if(!$result) {
	respondError("DB_ISSUE", "mysql error in hubSongList: " . $conn->error);
}

$arr = array();
while($assoc = mysqli_fetch_assoc($result)) {
	$assoc['playing'] = !!$assoc['playing'];
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
