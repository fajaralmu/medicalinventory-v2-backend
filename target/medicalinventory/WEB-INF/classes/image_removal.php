<?php
/*
    upload file to apache server script
*/

    $method = $_SERVER['REQUEST_METHOD'];
    if ($method != 'POST') {
        http_response_code(405);
        echo 'not allowed';
        exit();
    }
    ini_set('display_errors', '1');
    ini_set('display_startup_errors', '1');
    error_reporting(E_ALL);
   
    $name = $_POST['name'];
    
	$result = unlink("images/". $name); 
      
    $response = [
       'message'=> $result,
       'name'=> $name
       ];
    echo json_encode($response);
    
    exit();
    
?>