<?php
    include("connection.php");

    $query = "SELECT port1, port2, port3 FROM stations WHERE id = 1 ";
    $query_run = mysqli_query($connection, $query);

    if($query_run){
        if(mysqli_num_rows($query_run) > 0){
            $row = mysqli_fetch_assoc($query_run);

            $query = "UPDATE stations SET port1 = '0', port2 = '0', port3 = '0' WHERE id = 1";
            $query_run = mysqli_query($connection, $query);
            if($query_run){
                $response["success"] = true;
                $response["message"] = "data load successful";
                $response["port1"] = $row["port1"];
                $response["port2"] = $row["port2"];
                $response["port3"] = $row["port3"];

            } else {
                $response["success"] = false;
                $response["message"] = "unknown error while updating";
            }

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