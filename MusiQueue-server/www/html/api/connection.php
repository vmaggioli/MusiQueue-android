<?php
require_once "util/util.php";
require_once "../../inc/dbinfo.inc";

if(!isset($conn)) {
	$conn = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);
  	if (mysqli_connect_errno()) {
    	respondError("Failed to connect to MySQL: " . mysqli_connect_error());
  	}
}
?>
