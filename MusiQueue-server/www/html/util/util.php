<?php
function assertGiven($param) {
	if(!isset($_REQUEST[$param]))
		respondError("INVALID_REQUEST", "You must provide a `" . $param . "`");
}

function respondSuccess($resultObj) {
	respondJson(array(
		"error" => false,
		"result" => $resultObj
	));
}

function respondError($errorCode, $message = '') {
	respondJson(array(
		"error" => true,
		"errorCode" => $errorCode,
		"message" => $message,
		"result" => null
	));
}

function respondJson($obj) {
	if(isset($_REQUEST['pretty'])) {
		echo "<pre>";
		echo json_encode($obj, JSON_PRETTY_PRINT);
		echo "</pre>";
	}else{
		echo json_encode($obj);
	}
	die();
}
?>
