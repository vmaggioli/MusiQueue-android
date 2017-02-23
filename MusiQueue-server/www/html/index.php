<h3>API Help:</h3>
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
