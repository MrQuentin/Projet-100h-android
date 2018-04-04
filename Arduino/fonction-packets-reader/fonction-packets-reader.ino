#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include <bluefruit.h>

#define NEOPIXEL_VERSION_STRING "Neopixel v2.0"

#define MAXCOMPONENTS  4
uint8_t *pixelBuffer = NULL;
uint8_t width = 0;
uint8_t height = 0;
uint8_t stride;
uint8_t componentsValue;
bool is400Hz;
uint8_t components = 3;     // only 3 and 4 are valid values

Adafruit_NeoPixel neopixel = Adafruit_NeoPixel();

// BLE Service
BLEDis  bledis;
BLEUart bleuart;

String direction;
int distance;

void setup()
{
    Serial.begin(115200);
    Serial.println("Adafruit Bluefruit Neopixel Test");
    Serial.println("--------------------------------");

    Serial.println();
    Serial.println("Please connect using the Bluefruit Connect LE application");

    // Config Neopixels
    neopixel.begin();

    // Init Bluefruit
    Bluefruit.autoConnLed(true);
    Bluefruit.begin();
    // Set max power. Accepted values are: -40, -30, -20, -16, -12, -8, -4, 0, 4
    Bluefruit.setTxPower(4);
    Bluefruit.setName("Bluefruit52");
    Bluefruit.setConnectCallback(connect_callback);

    // Configure and Start Device Information Service
    bledis.setManufacturer("Adafruit Industries");
    bledis.setModel("Bluefruit Feather52");
    bledis.begin();

    // Configure and start BLE UART service
    bleuart.begin();

    // Set up and start advertising
    startAdv();
}

void startAdv(void)
{
    // Advertising packet
    Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);
    Bluefruit.Advertising.addTxPower();

    // Include bleuart 128-bit uuid
    Bluefruit.Advertising.addService(bleuart);

    // Secondary Scan Response packet (optional)
    // Since there is no room for 'Name' in Advertising packet
    Bluefruit.ScanResponse.addName();

    /* Start Advertising
     * - Enable auto advertising if disconnected
     * - Interval:  fast mode = 20 ms, slow mode = 152.5 ms
     * - Timeout for fast mode is 30 seconds
     * - Start(timeout) with timeout = 0 will advertise forever (until connected)
     *
     * For recommended advertising interval
     * https://developer.apple.com/library/content/qa/qa1931/_index.html
     */
    Bluefruit.Advertising.restartOnDisconnect(true);
    Bluefruit.Advertising.setInterval(32, 244);    // in unit of 0.625 ms
    Bluefruit.Advertising.setFastTimeout(30);      // number of seconds in fast mode
    Bluefruit.Advertising.start(0);                // 0 = Don't stop advertising after n seconds
}

void connect_callback(uint16_t conn_handle)
{
    char central_name[32] = { 0 };
    Bluefruit.Gap.getPeerName(conn_handle, central_name, sizeof(central_name));

    Serial.print("Connected to ");
    Serial.println(central_name);

    Serial.println("Please select the 'Neopixels' tab, click 'Connect' and have fun");
}

void loop() {

    if ( Bluefruit.connected() && bleuart.notifyEnabled() ) {

        int command = bleuart.read();

        switch (command) {
            case 'D': {   // Set Direction
                setDirection();
                break;
            }
            case 'L': {   // Set Distance
                setDistance();
                break;
            }
        }
    }
}

String readData(){ //read sent data
  String buffer;
  while(bleuart.available()){
    buffer += (char) bleuart.read();
  }
  return buffer;
}

int readDataAsNumber() {
  String buffer;
  while(bleuart.available()){
    buffer += bleuart.read() - 48;
  }
  return buffer.toInt();
}

void setDirection() {
  Serial.print("setDirection : ");
  direction = readData();
  Serial.println("Data : " + direction);
}

void setDistance(){
  Serial.print("setDistance : ");
  distance = readData().toInt();
  Serial.println("Data: " + distance ); 
}



