<?php
define("PRETTY_PRINT", isset($_REQUEST["pretty"]));
if(!isset($GLOBALS['_apiDocsString'])) $GLOBALS['_apiDocsString'] = "";

function assertGiven($param) {
	if(!isset($_REQUEST[$param]))
		respondError("INVALID_REQUEST", "You must provide a `" . $param . "`");
}

function apiDocs($string) {
	if(!PRETTY_PRINT) return;

	$GLOBALS['_apiDocsString'] .= $string . "\n";
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
	if(PRETTY_PRINT) {
		if($GLOBALS['_apiDocsString'] != "") {
			echo "<pre>";
			echo str_replace("\t", "    ", $GLOBALS['_apiDocsString']);
			echo "</pre>";
		}

		echo "<pre>";
		echo json_encode($obj, JSON_PRETTY_PRINT);
		echo "</pre>";
	}else{
		echo json_encode($obj);
	}
	die();
}
?>
