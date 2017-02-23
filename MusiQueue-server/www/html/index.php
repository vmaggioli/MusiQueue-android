<h3>API Help:</h3>
<pre>
All endpoints should return a json response in the following format:

{
    error: bool, whether the request failed
    errorCode (optional): string, an error code that shouldn't change
    errorMessage (optional): string, a pretty error message we can display
    result: an object of whatever data this endpoint actually returns. Null if error is true.
}

The value of result (on success) will vary by endpoint.

Additionally, endpoints should print information about themselves if called with "?pretty".
</pre>
<h3>Endpoints Available:</h3>
<ul>
<?php
$handle = opendir('.');
while(false !== ($entry = readdir($handle))) {
    if(!is_dir($entry)) {
    	if(in_array($entry, ["index.php", "pull.php", "connection.php"])) continue;
        echo "<li><a href='$entry?pretty'>" . $entry . "</a></li>\n";
    }
}
closedir($handle);
?>
</ul>
