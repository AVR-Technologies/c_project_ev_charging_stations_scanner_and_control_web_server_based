<?php
    include("connection.php");

    $username = $_GET["username"];
    $password = $_GET["password"];

    $query = "SELECT * FROM admin WHERE username = '$username' AND password ='$password'";
    $query_run = mysqli_query($connection, $query);

    if($query_run){
        if(mysqli_num_rows($query_run) > 0){
            $row=mysqli_fetch_assoc($query_run);
            $response["success"] = true;
            $response["message"] = "login successful";
            $response["balance"] = intval($row["balance"]);
        } else{ 
            $response["success"] = false;
            $response["message"] = "login failed! user not available";
        }
    }else{
        $response["success"] = false;
        $response["message"] = "unknown error";
    }

    echo json_encode($response);
    mysqli_close($connection);
?>