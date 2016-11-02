<?php
/**
 * Created by PhpStorm.
 * User: aditi
 * Date: 11/22/2015
 * Time: 5:49 PM
 */


$servername = "127.0.0.1";
$username = "root";
$password = "root";

function respond($name, $value){
    $response = array(
        $name => $value
    );
    echo json_encode($response);
}

try {
	  $db = new PDO("mysql:host=$servername;dbname=dawardriving", $username, $password);
    // set the PDO error mode to exception
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $db->prepare('SELECT * FROM AccessPointDetails');
//	echo($stmt);
    $stmt->execute();
    respond("markers", $stmt->fetchAll(PDO::FETCH_ASSOC));
}
catch(PDOException $e)
{
    echo "Connection failed: " . $e->getMessage();
}

//echo file_get_contents("data.json");

?>
