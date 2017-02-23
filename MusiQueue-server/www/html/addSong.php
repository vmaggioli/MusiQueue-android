<?php
require_once "connection.php";
require_once "assert/hubExists.php";
require_once "assert/userConnectedToHub.php";
require_once "assert/songGiven.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);
$songId = mysqli_real_escape_string($conn, $_REQUEST['songId']);

$result = mysqli_query($conn, "SELECT COUNT(*) as total FROM Songs WHERE hub_id = '$hubId' AND song_id = '$songId' ");
$data = mysqli_fetch_assoc($result);
if($data['total'] > 0) {
	respondError("SONG_ALREADY_IN_QUEUE", "That song is already in this queue.");
}

$phoneId = mysqli_real_escape_string($conn, $_REQUEST['phoneId']);
$songTitle = mysqli_real_escape_string($conn, $_REQUEST['songTitle']);

$result = $conn->query("
	INSERT INTO
	Songs (`hub_id`, `phone_id`, `song_id`, `song_title`)
	VALUES ('$hubId', '$phoneId', '$songId', '$songTitle')
");

require "hubSongList.php";

?>
