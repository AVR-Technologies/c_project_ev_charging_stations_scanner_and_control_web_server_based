<?php
    include("connection.php");

    $username = $_GET["username"];  //to check and deduct balance
    $password = $_GET["password"];  //to check and deduct balance
    $port     = $_GET["port"];      //which port to start
    $value    = $_GET["value"];     //for how much of time: and charging rate = 1rs / 1minute

    # check balance
    $query = "SELECT balance FROM admin WHERE username = '$username' AND password ='$password'";
    $query_run = mysqli_query($connection, $query);

    if($query_run){
        if(mysqli_num_rows($query_run) > 0){

            $row=mysqli_fetch_assoc($query_run);
            $balance = intval($row["balance"]);
            #check if balance is more than value
            if($value <= $balance){
                $balance = $balance - $value;

                #deduct and update balance
                $query = "UPDATE admin SET balance = '$balance' WHERE username = '$username' AND password ='$password'";
                $query_run = mysqli_query($connection, $query);

                if($query_run){

                    #update station port / start charging
                    $query = "UPDATE stations SET $port = $value WHERE id = 1";
                    $query_run = mysqli_query($connection, $query);
                
                    if($query_run){
                        $response["success"] = true;
                        $response["message"] = "charging started";
                        $response['balance'] = $balance;
                    }else{
                        $response["success"] = false;
                        $response["message"] = "failed to turn on charging port";
                    }

                }else{
                    $response["success"] = false;
                    $response["message"] = "failed to update balance";
                }
            }else{
                $response["success"] = false;
                $response["message"] = "not enough balance";
            }
        } else{ 
            $response["success"] = false;
            $response["message"] = "user not available";
        }
    }else{
        $response["success"] = false;
        $response["message"] = "unknown error";
    }

    echo json_encode($response);
    mysqli_close($connection);
?>