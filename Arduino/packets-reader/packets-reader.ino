#include <bluefruit.h>
#include <stdio.h>
#include <stdlib.h>

BLEDis  bledis;
BLEUart bleuart;
BLEBas  blebas;

String recievedHex = "";
String recievedStr = "";


SoftwareTimer blinkTimer;

void setup() {
  Serial.begin(115200);
  Serial.println("Bluefruit52 BLEUART packet reader");
  Serial.println("---------------------------------\n");

  blinkTimer.begin(1000,blink_timer_callback);
  blinkTimer.start();

  Bluefruit.autoConnLed(true);
  Bluefruit.configPrphBandwidth(BANDWIDTH_MAX);
  Bluefruit.begin();
  Bluefruit.setTxPower(4);// Set max power. Accepted values are: -40, -30, -20, -16, -12, -8, -4, 0, 4
  Bluefruit.setName("Bike The Way");
  Bluefruit.setConnectCallback(connect_callback);
  Bluefruit.setDisconnectCallback(disconnect_callback);

  //configure et lance le service d'information de l'appareil
  bledis.setManufacturer("Adafruit Industries");
  bledis.setModel("Bluefruit Feather52");
  bledis.begin();
  
  //configure et lance le service BLE Uart
  bleuart.begin();
  
  //configure et lance le service de batterie
  blebas.begin();
  blebas.write(100);

  startAdv();
}

void startAdv(void){
  // Advertising packet
  Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
  Bluefruit.Advertising.addTxPower();
  // Include bleuart 128-bit uuid
  Bluefruit.Advertising.addService(bleuart);

  Bluefruit.Advertising.restartOnDisconnect(true);
  Bluefruit.Advertising.setInterval(32, 244);    // in unit of 0.625 ms
  Bluefruit.Advertising.setFastTimeout(30);      // number of seconds in fast mode
  Bluefruit.Advertising.start(0);                // 0 = Don't stop advertising after n seconds  
}

void loop() {
  if (bleuart.available()){
    int command = bleuart.read();
    switch(command){
      case 'D': {
        setDirection();
        break;
      }
      case 'L': {
        setDistance();
        break;
      }
    }
  }
}

void setDirection(){
  Serial.printf("setDirection : color(%d, %d, %d)\n", bleuart.read(), bleuart.read(), bleuart.read());
  
}
void setDistance(){
  Serial.println("setDistance");
}

void connect_callback(uint16_t conn_handle){
  char central_name[32] = { 0 };
  Bluefruit.Gap.getPeerName(conn_handle, central_name, sizeof(central_name));

  Serial.print("Connected to ");
  Serial.println(central_name);
}

void disconnect_callback(uint16_t conn_handle, uint8_t reason){
  (void) conn_handle;
  (void) reason;

  Serial.println();
  Serial.println("Disconnected");
}

void blink_timer_callback(TimerHandle_t xTimerID)
{
  (void) xTimerID;
  //digitalToggle(LED_RED);
}



