<?php
require_once "util/util.php";
require_once "../../inc/dbinfo.inc";

apiDocsCouldError("DB_ISSUE");

if(!isset($conn)) {
	$conn = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);
  	if (mysqli_connect_errno()) {
    	respondError("DB_ISSUE", "Failed to connect to MySQL: " . mysqli_connect_error());
  	}
}
?>
