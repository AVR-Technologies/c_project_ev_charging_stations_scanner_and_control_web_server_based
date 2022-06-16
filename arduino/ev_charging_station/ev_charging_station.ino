#include <SoftwareSerial.h>
#include "ArduinoJson.h"
SoftwareSerial esp(2,3);
DynamicJsonDocument  doc(200);
int port1=0,port2=0,port3=0;
void setup() {
  Serial.begin(9600);
  esp.begin(9600);
  sendCommand("AT"); 
  sendCommand("AT+CIPMUX=0");
  sendCommand("AT+CWMODE=3");

//  esp.println("AT+CWJAP=\"AVR_TECH\",\"//avrtech;\"");
//    while(!esp.find("OK"))
//    {
//      Serial.print(".");
//      delay(100);
//    }
//    Serial.print("Wifi Conneted");

    pinMode(12,OUTPUT);
    pinMode(10,OUTPUT);
    pinMode(11,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  updateData();
  read_port();

}

void read_port()
{
  if(port1!=0)
  {
    digitalWrite(12,HIGH);
    port1=(port1*1000);
    long last = millis();
    Serial.println("Charging Started at Port 1");
    while((millis()-last)<port1)
    {
      Serial.println((millis()-last)/1000);
      delay(1000);
    }
    Serial.print("Charging Completed");
    digitalWrite(12,LOW);
  }
  else if(port2!=0)
  {
    digitalWrite(11,HIGH);
    
    port2=(port2*1000);
    long last = millis();
    Serial.println("Charging Started at Port 2");
    while((millis()-last)<port2)
    {
      Serial.println((millis()-last)/1000);
      delay(1000);
    }
    Serial.print("Charging Completed");
    digitalWrite(11,LOW);
  }
  else if(port3!=0)
  {
    digitalWrite(10,HIGH);
    port3=(port3*1000);
    long last = millis();
    Serial.println("Charging Started at Port 3");
    while((millis()-last)<port3)
    {
      Serial.println((millis()-last)/1000);
      delay(1000);
    }
    Serial.print("Charging Completed");
    digitalWrite(10,LOW);
  }
}

void updateData() {

  String cmd = "AT+CIPSTART=\"TCP\",\"192.168.1.2\",80";  //   ThingSpeak Server Ip address
  sendCommand(cmd);                                           //   Send Command to ESP8266 for connection with server of ThingSpeak
  cmd = "GET http://localhost/ev_stations_scanner/read_state.php";
  delay(500);
  

  int cmdL = cmd.length();
  cmdL = cmdL + 2;                                            //   Get length of Characters to send
  String Send = "AT+CIPSEND=" + String(cmdL);
  sendCommand(Send);
  //   Command to intialize the ESP8266 to send data
  delay(500);

  esp.println(cmd);
  Serial.println(esp.readString());
  String res = esp.readString();
  Serial.println(res);
  String res_sub = res.substring(res.indexOf("{"),(res.indexOf("}")+1));
  Serial.println(res_sub);
  readData(res_sub);

//  int cnt=0;
//  while(cnt<5)                                 //   Command to ESP8266 TO send data
//  {
//    cnt++;
//    Serial.println(esp.readString());
//    Serial.print(cnt);
//   delay(1);
//  }
  
  delay(500);
}

void sendCommand(String command) {
  esp.println(command);
  Serial.println(esp.readString());
}

void readData(String json)
{
   DeserializationError error = deserializeJson(doc, json);

  // Test if parsing succeeds.
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }

   port1 = doc["port1"];
   port2 = doc["port2"];
   port3 = doc["port3"];

  // Print values.
  Serial.print("port1 = ");
  Serial.print(port1);
  Serial.print("port2 = ");
  Serial.print(port2);
  Serial.print("port3 = ");
  Serial.println(port3);
}
