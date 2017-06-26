<?php
require_once "connection.php";

set_time_limit(0);
ob_implicit_flush();

$ip = '52.14.50.251';
$port = 3636;

if (($sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false) {
  echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) ."\n";
}

if (socket_bind($sock, $ip, $port) === false) {
  echo "socket_bind() failed: reason: " .socket_strerror(socket_last_error($sock)) ."\n";
}

if (socket_listen($sock, 5) === false) {
  echo "socket_listen() failed: reason: " . socket_strerror(socket_last_error($sock)) ."\n";
}

do {
  if (($msgsock = socket_accept($sock)) === false) {
    echo "socket_accept() failed: reason: " . socket_strerror(socket_last_error($sock)) ."\n";
    break;
  }

  /* send instruction */
  $msg = "\nWe have received your message here on the server side. \n" .
  "To quit, type 'quit'. To shut down the server type 'shutdown'.\n";
  socket_write($msgsock, $msg, strlen($msg));

  do {
    /* 2048 is the length of string it will attempt to read */
    if (false === ($buf = socket_read($msgsock, 2048, PHP_NORMAL_READ))) {
      echo "socket_read() failed: reason: " .socket_strerror(socket_last_error($msgsock)) ."\n";
      break 2;
    }
    if (!$buf = trim($buf)) {
      continue;
    }
    if ($buf == 'quit') {
      break;
    }
    if ($buf == 'shutdown') {
      socket_close($msgsock);
      break 2;
    }

    $talkback = "PHP: You said '$buf'.\n";
    socket_write($msgsock, $talkback, strlen($talkback));
    echo "$buf\n";
  } while (true);
  socket_close($msgsock);
} while (true);
socket_close($sock);
?>
