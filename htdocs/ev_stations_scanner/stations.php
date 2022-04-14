<?php
    include("connection.php");

    $query = "SELECT * FROM stations";
    $query_run = mysqli_query($connection, $query);
    $r = array();
    if($query_run){
        if(mysqli_num_rows($query_run) > 0){
            while($row = mysqli_fetch_assoc($query_run)){
                $r[] = $row;
            }
            $response["success"] = true;
            $response["message"] = "data load successful";
            $response["stations"] = $r;
        } else{ 
            $response["success"] = false;
            $response["message"] = "data not found";
        }
    }else{
        $response["success"] = false;
        $response["message"] = "unknown error";
    }

    echo json_encode($response);
    mysqli_close($connection);
?>