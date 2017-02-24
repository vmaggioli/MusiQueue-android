<?php
require_once "api/connection.php";

  $name = isset($_POST['name']) ? $_POST['name'] : '';
  $addr = isset($_POST['addr']) ? $_POST['addr'] : '';

  $stmt0 = $conn->prepare("INSERT INTO Employees (Name, Address) VALUES (?,?)");
  $stmt0->bind_param("ss", $name, $addr);
  $stmt0->execute();

  $stmt = "SELECT * FROM Employees;";
  $result = mysqli_query($conn, $stmt) or die($conn->error);

  if ($result) {
    while ($row = mysqli_fetch_row($result)) {
      echo $row[1];
      echo "\t";
      echo $row[2];
      echo "\n";
    }
  } else {
    echo "Failed to Login";
  }
  exit();
?>
