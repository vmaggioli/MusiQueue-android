<?php
define("PRETTY_PRINT", isset($_REQUEST["pretty"]));
if(!isset($GLOBALS['_apiDocsString'])) $GLOBALS['_apiDocsString'] = "";

function assertGiven($param) {
	if(!isset($_REQUEST[$param]))
		respondError("INVALID_REQUEST", "You must provide a `" . $param . "`");
}

// apiDocs is a way of adding a comment in the code that can also
// be displayed on the web if the api endpoint is hit with ?pretty
function apiDocs($string) {
	if(!PRETTY_PRINT) return;

	$GLOBALS['_apiDocsString'] .= $string . "\n";
}

// wrapper for respondJson when we want to give an object back
function respondSuccess($resultObj) {
	respondJson(array(
		"error" => false,
		"result" => $resultObj
	));
}

// wrapper for respondJson when we want to give an error
function respondError($errorCode, $message = '') {
	respondJson(array(
		"error" => true,
		"errorCode" => $errorCode,
		"errorMessage" => $message,
		"result" => null
	));
}

// builds a standardized json response as follows:
// {
//     error: bool, whether the request failed
//     [errorCode]: string, a error code that shouldn't change
//     [errorMessage]: string, a pretty error message we can display
//     result: an object of whatever data this endpoint actually returns. Null if error is true.
// }
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
