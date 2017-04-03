<?php
require_once "connection.php";

if(VOTE_DIRECTION == -1) {
	apiDocs("voteDownSong:\n");
}else{
	apiDocs("voteUpSong:\n");
}

apiDocs("
	Params:
		hubId - the hub the song is on
		phoneId - the phone id of the user voting
		songId - the id of the song entry
	Returns on success:
		See hubSongList endpoint.
");
apiDocsCouldError("DB_ISSUE");

require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/hubIdOpen.php";
require_once "assert/songOnHub.php";

// Look for previous vote
$result = $conn->query("
	SELECT IFNULL(SUM(Votes.vote), 0) as oldVote
	FROM Votes
	WHERE
		Votes.song_id = '$songId'
		AND Votes.phone_id = '$phoneId'
");
$oldVote = mysqli_fetch_assoc($result)['oldVote'];

// insert new vote
$newVote = VOTE_DIRECTION;
$result = $conn->query("
	REPLACE INTO Votes
	SET
		Votes.vote = $newVote,
		Votes.song_id = '$songId',
		Votes.phone_id = '$phoneId'
");

// cancel any previous votes
$upVoteChange = 0;
$downVoteChange = 0;

if($oldVote < 0) {
	$downVoteChange += $oldVote;
}else{
	$upVoteChange -= $oldVote;
}
if($newVote < 0) {
	$downVoteChange -= $newVote;
}else{
	$upVoteChange += $newVote;
}

$result = mysqli_query($conn, "
	UPDATE Songs
	SET
		down_votes = down_votes + $downVoteChange,
		up_votes = up_votes + $upVoteChange
	WHERE id = '$songId'
");

if(!$result) {
	respondError("DB_ISSUE", "Could not decrease vote total on song");
}

require "hubSongList.php";

?>
