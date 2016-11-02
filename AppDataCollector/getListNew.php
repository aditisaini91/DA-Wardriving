i<?php
/**
 * Created by PhpStorm.
 * User: Disha Bhat
 * Date: 09/01/2016
 * Time: 5:20 PM
 */

//db connection

$servername = "127.0.0.1";
$username = "root";
$password = "root";

try {
        $db = new PDO("mysql:host=$servername;dbname=dawardriving", $username, $password);
        // set the PDO error mode to exception
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        echo "Connection Successful.";
}
catch(PDOException $e)
{
    echo "Connection failed: " . $e->getMessage();
}


// array for JSON respone
$result = file_get_contents('php://input');
$maxID = $db->prepare("SELECT MAX(scan_id) as maxid FROM WifiScans;");
$maxID->execute();
$scanIdfromDB = $maxID->fetch(PDO::FETCH_ASSOC);
$id = $scanIdfromDB['maxid'];
if($id == NULL){
 $id = 0;
}
$res = json_decode($result,true);
$scanID = array_keys($res);
$scanID = $scanID[0];
$dataArray = $res[$scanID];
$myKeys = "";
foreach($res as $k=>$v)
{
     	$nextId = ((int)$k+(int)$id);
	$scanID = (string)($nextId);
	foreach($v as $row)
	{	
		
		$lat_current = $row['currentLat'];
		$long_current = $row['currentLng'];
		$BSSID = $row['bssid'];
		$SSID = $row['ssid'];
		$frequency = $row['frequency'];
		$RSSI = $row['rssi'];
		$timestamp = $row['timestamp'];
		$capabilities = $row['capabilities'];
		$distance = $row['distance'];
		$username = $row['username'];

	 $stmt = $db->prepare("INSERT INTO WifiScans(BSSID, SSID, frequency, lat_current, long_current, RSSI, timestamp, capabilities, scan_id, distance, username)VALUES('$BSSID','$SSID','$frequency','$lat_current','$long_current','$RSSI','$timestamp','$capabilities','$scanID','$distance','$username');");
        //echo $stmt;
        $stmt->execute();		
	} 
}
die;
?>

