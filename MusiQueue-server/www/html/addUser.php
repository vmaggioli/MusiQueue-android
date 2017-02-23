<?php
require_once "connection.php";

  $phoneid = isset($_POST['userId']) ? $_POST['userId'] : '';
  $username = isset($_POST['username']) ? $_POST['username'] : '';

  echo "phoneid = " . $phoneid;
  echo "username = " . $username;

  //$time = isset($_POST['dateTime']) ? $_POST['username'] : '';
  //$dateTime = new DateTime($time);
  //$dateTime = $dateTime->format('Y-m-d H:i:s');
  echo "checking if user exists\n";

  $result = mysqli_query($conn, "SELECT name FROM Users WHERE phone_id = '$phoneid'");

  if ($result) {
    echo "updating\n";
    $row = mysqli_fetch_row($result);
    $updateStmt = $conn->prepare("UPDATE Users SET name=? WHERE phone_id=?");
    $updateStmt->bind_param("ss", $username, $phoneid);
    $updateStmt->execute();
    exit();
  }
  echo "creating new user\n";
  $stmt = $conn->prepare("INSERT INTO Users (phone_id, name) VALUES (?,?)");
  $stmt->bind_param("ss", $phoneid, $username);
  $stmt->execute();
  exit();
?>
