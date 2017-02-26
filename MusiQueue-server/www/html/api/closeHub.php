<?php
require_once "connection.php";

apiDocs("
closeHub:
    Params:
        hubId - the hub id of the hub the user is closing
        phoneId - the creator's phoneId
    Returns on success:
        true
");
apiDocsCouldError('HUB_NOT_FOUND');
apiDocsCouldError('NOT_HUB_CREATOR');


require_once "assert/hubExists.php";
require_once "assert/phoneIdIsCreator.php";

$hubId = mysqli_real_escape_string($conn, $_REQUEST['hubId']);

$result = mysqli_query($conn, "
    UPDATE Hubs
    SET `closed` = 1
    WHERE `id` = '$hubId'
");

respondSuccess(true);
