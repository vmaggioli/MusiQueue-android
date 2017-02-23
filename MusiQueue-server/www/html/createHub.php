<?php
require_once "connection.php";

  $hubName = isset($_POST['hubName']) ? $_POST['hubName'] : '';
  $passPin = isset($_POST['passPin']) ? $_POST['passPin'] : '';
  $creatorId = isset($_POST['creatorId']) ? $_POST['creatorId'] : '';

  $stmt = $conn->prepare("INSERT INTO Hubs (hub_creator_id, hub_name, hub_pin) VALUES (?,?,?)");
  $stmt->bind_param("ssi", $creatorId, $hubName, $passPin);
  $stmt->execute();
  echo "exiting\n";
  exit();
?>
