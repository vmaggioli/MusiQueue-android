<?php
require_once "connection.php";

$result = mysqli_query($conn, "
	DELETE FROM Hubs WHERE DATEDIFF(CURRENT_TIME(), time_last_active) > 7
");

?>
