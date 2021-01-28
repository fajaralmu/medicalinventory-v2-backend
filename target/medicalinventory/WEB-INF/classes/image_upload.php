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
    $order = $_POST['order'];
    $total = $_POST['total'];
    $partialData = $_POST['partialData'];
    
    $temp_file_name = "images/tmp/". $name .'.tmp.txt';
   
    //make sure the temp file is exists
    $temp_file_created = file_exists($temp_file_name) ? fopen($temp_file_name, 'rw+') : fopen($temp_file_name, 'w+');
    
    if ($temp_file_created  == false) {
        echo json_encode(['message'=>'Error Creating Temp File']);
        exit();
    }
    fclose($temp_file_created);
  
    $temp_file_content = file_get_contents($temp_file_name);
   
    if (!$temp_file_content || is_null($temp_file_content)) {
       $temp_file_content = "";
    }
    $result = false;
    if ($order == $total) {
        
        $ifp = fopen("images/". $name, 'wb');
        $base64FullData = $temp_file_content.$partialData;
        $base64RawData = explode(',', $base64FullData);
        $result = fwrite($ifp, base64_decode($base64RawData[1]));
       // file_put_contents($temp_file_name, $base64FullData);
       //delete temp file
        unlink($temp_file_name);
        fclose($ifp);
    } else {
        $result = file_put_contents($temp_file_name, $temp_file_content.$partialData);
    }
      
    $response = [
       'message'=> $result,
       'order' => $order,
       'total' => $total,
       'name'=>$name,
    //   'data'=>$partialData
       ];
    echo json_encode($response);
    
    exit();
    
?>