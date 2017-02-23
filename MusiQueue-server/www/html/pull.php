<?php
    $out = [];
    exec("cd /home/musiqueue/MusiQueue;git pull -v 2>&1", $out);
    echo implode("<br/>\n", $out);
?>
