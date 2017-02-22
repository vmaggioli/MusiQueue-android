<?php include "../inc/dbinfo.inc"; ?>
<?php
  echo "at the top\n";
  $conn = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_DATABASE);
  if (mysqli_connect_errno()) {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    exit();
  }

  $hubName = isset($_POST['hubName']) ? $_POST['hubName'] : '';
  $passPin = isset($_POST['passPin']) ? $_POST['passPin'] : '';
  $creatorId = isset($_POST['creatorId']) ? $_POST['creatorId'] : '';

  $stmt = $conn->prepare("INSERT INTO Hubs (hub_creator_id, hub_name, hub_pin) VALUES (?,?,?)");
  $stmt->bind_param("ssi", $creatorId, $hubName, $passPin);
  $stmt->execute();
  echo "exiting\n";
  exit();
?>
