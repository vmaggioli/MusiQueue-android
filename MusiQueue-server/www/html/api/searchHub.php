<?php
require_once "connection.php";

apiDocs("
searchHub:
	Params:
		  searchHub - the input for a hub name searched
	Returns on success:
		  array of hub names containing the searched input
");


$hubName = mysqli_real_escape_string($conn, $_REQUEST['hubName']);

$result = mysqli_query($conn, "SELECT hub_name FROM Hubs WHERE hub_name LIKE '%$hubName%';");

if ($result->num_rows == 0) {
  echo "nothing found\n";
  exit;
}

$arr = array();
while($assoc = mysqli_fetch_assoc($result)) {
	$arr[] = $assoc;
}

respondSuccess($arr);
?>
