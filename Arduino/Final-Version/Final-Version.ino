#include <Arduino.h>
#include <Wire.h>
#include <LSM303.h>
#include <Adafruit_NeoPixel.h>
#include <bluefruit.h>

#define debug true // mettre en commentaire pour désactiver le mode debug

//Pin sur lequel est connecté le NeaoPixel Ring
#define PIN 7
//Version de NeoPixel uttilisée
#define NEOPIXEL_VERSION_STRING "Neopixel v2.0"

//variables de la communication BLE
#define MAXCOMPONENTS  4
uint8_t *pixelBuffer = NULL;
uint8_t width = 0;
uint8_t height = 0;
uint8_t stride;
uint8_t componentsValue;
bool is400Hz;
uint8_t components = 3;     // only 3 and 4 are valid values

//création de l,'objet qui retiendra le capteur
LSM303 compass;
//init du NeoPixel Ring
Adafruit_NeoPixel neopixel = Adafruit_NeoPixel(24, PIN, NEO_GRB + NEO_KHZ800);

// BLE Service
BLEDis  bledis; 
BLEUart bleuart;

//valeur angulaire des directions par rapport au nord
float default_d = 0.0;
float gauche_d = -90.0;
float droite_d = 90.0;
float gauche_b = -45.0;
float droite_b = 45.0;
float demitour = 180;
float direction = default_d;  // variable qui contient la direction

int indicator;                //contient l'indice de la LED de direction
const byte level = 10;        //niveau de Luminositée des LED (0-255)
float heading;                //angle par rapport au nord du capteur

void setup()
{
    //init du capteur LSMD303
    Wire.begin();
    compass.init();
    compass.enableDefault();
    compass.m_min = (LSM303::vector<int16_t>){  +197,   -148,   -439};  //paramètres du capteur déterminé par un autre programme
    compass.m_max = (LSM303::vector<int16_t>){  +202,   -144,   -435};  //paramètres du capteur déterminé par un autre programme

    #ifdef debug
      Serial.begin(115200);
      Serial.println("Adafruit Bluefruit Neopixel Test");
      Serial.println("--------------------------------");

      Serial.println();
      Serial.println("Please connect using the Bluefruit Connect LE application");
    #endif

    // Config Neopixels
    neopixel.begin();
    neopixel.show();

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

/**
 * initialise les paramètre de l'appareil bluetooth 
 * qui serons visible par les autres appareils
 */
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
    Bluefruit.Advertising.restartOnDisconnect(true);
    Bluefruit.Advertising.setInterval(32, 244);    // in unit of 0.625 ms
    Bluefruit.Advertising.setFastTimeout(30);      // number of seconds in fast mode
    Bluefruit.Advertising.start(0);                // 0 = Don't stop advertising after n seconds
}
/**
 * Callback de connection BLE
 */
void connect_callback(uint16_t conn_handle)
{
    char central_name[32] = { 0 };
    Bluefruit.Gap.getPeerName(conn_handle, central_name, sizeof(central_name));
    #ifdef debug
      Serial.print("Connected to ");
      Serial.println(central_name);
      Serial.println("Please select the 'Neopixels' tab, click 'Connect' and have fun");
    #endif
}

void loop() {

    if ( Bluefruit.connected() && bleuart.notifyEnabled() ) {
        //lecture du buffer bluetooth
        int command = bleuart.read();

        //annalyse du message reçu
        switch (command) {
            case 'D': {   // Set Direction
                setDirection();
                break;
            }
            //exemple d'ajout d'annalyse (message commençant par L
            //case 'L':{ //set Distance
            //    setDistance();
            //}
        }
        //met à jour l'affichage sur les LEDs
        updateCompass();
    }
}

/**
 * Met à jour l'état des LEDs en fonction de la valeur
 * lu sur le capteur LSMD303
 */
void updateCompass(){
    compass.read();
    heading = 360 - compass.heading() + direction;
    
    if (heading < 0 ){
      heading += 360;
    }else if (heading > 360){
      heading -= 360;
    }
    
    setIndicator();
    showIndicator();
}

/**
 * Fonction que va changer la direction du pointeur de
 * la bousole en ce basant sur le message reçu par le
 * Bluetooth
 */
void setDirection() {
  int dir = bleuart.read();
  switch (dir){
    case '0':{ //direction tout droit
      direction = default_d;
      #ifdef debug
        Serial.println("tout droit");
      #endif
      break;
    }
    case '1':{
      direction = gauche_d;
      #ifdef debug
        Serial.println("gauche droit");
      #endif
      break;
    }
    case '2':{
      direction = droite_d;
      #ifdef debug
        Serial.println("droite droit");
      #endif
      break;
    }
    case '3':{
      direction = gauche_b;
      #ifdef debug
        Serial.println("gauche diag");
      #endif
      break;
    }
    case '4':{
      direction = droite_b;
      #ifdef debug
        Serial.println("droite diag");
      #endif
      break;
    }
    case '5':{
      direction = demitour;
      #ifdef debug
        Serial.println("demi tour");
      #endif
      break;
    } 
    default:{
      direction = demitour;
      #ifdef debug
        Serial.println("default");
      #endif
      break;   
    }
  }
}

/**
 * Change le numéro de LED lié à la direction en ce 
 * basant sur la valeur de l'angle par rapport au nord
 * (heading) obtenu par le capteur
 */
void setIndicator(){
  if((heading >= 7.5)&&(heading < 97.5)){//division par 4 (1/4)
    if((heading >= 7.5)&&(heading < 52.5)){//division par 8 (1/8)
      if((heading >= 7.5)&&(heading < 22.5)){// LED 1
        indicator = 1;
        #ifdef debug
          Serial.println("LED 1");
        #endif  
      }
      if((heading >= 22.5)&&(heading < 37.5)){// LED 2
        indicator = 2;
        #ifdef debug
          Serial.println("LED 2");
        #endif 
      } 
      if((heading >= 37.5)&&(heading < 52.5)){// LED 3
        indicator = 3; 
        #ifdef debug
          Serial.println("LED 3");
        #endif 
      }   
    }
    if((heading >= 52.5)&&(heading < 97.5)){//division par 8 (2/8)
      if((heading >= 52.5)&&(heading < 67.5)){// LED 4
        indicator = 4;
        #ifdef debug
          Serial.println("LED 4");
        #endif 
      }
      if((heading >= 67.5)&&(heading < 82.5)){// LED 5
        indicator = 5;
        #ifdef debug
          Serial.println("LED 5");
        #endif 
      } 
      if((heading >= 82.5)&&(heading < 97.5)){// LED 6
        indicator = 6;
        #ifdef debug
          Serial.println("LED 6");
        #endif 
      }    
    }
  }
  if((heading >= 97.5)&&(heading < 187.5)){//division par 4 (2/4)
    if((heading >= 97.5)&&(heading < 142.5)){//division par 8 (3/8)
      if((heading >= 97.5)&&(heading < 112.5)){// LED 7
        indicator = 7;
        #ifdef debug
          Serial.println("LED 7");
        #endif 
      }
      if((heading >= 112.5)&&(heading < 127.5)){// LED 8
        indicator = 8;
        #ifdef debug
          Serial.println("LED 8");
        #endif 
      } 
      if((heading >= 127.5)&&(heading < 142.5)){// LED 9
        indicator = 9;
        #ifdef debug
          Serial.println("LED 9");
        #endif 
      }    
    }
    if((heading >= 142.5)&&(heading < 187.5)){//division par 8 (4/8)
      if((heading >= 142.5)&&(heading < 157.5)){// LED 10
        indicator = 10;
        #ifdef debug
          Serial.println("LED 10");
        #endif 
      }
      if((heading >= 157.5)&&(heading < 172.5)){// LED 11
        indicator = 11;
        #ifdef debug
          Serial.println("LED 11");
        #endif 
      } 
      if((heading >= 172.5)&&(heading < 187.5)){// LED 12
        indicator = 12;
        #ifdef debug
          Serial.println("LED 12");
        #endif 
      }      
    }
  }
  if((heading >= 187.5)&&(heading < 277.5)){//division par 4 (3/4)
    if((heading >= 187.5)&&(heading < 232.5)){//division par 8 (5/8)
      if((heading >= 187.5)&&(heading < 202.5)){// LED 13
        indicator = 13;
        #ifdef debug
          Serial.println("LED 13");
        #endif 
      }
      if((heading >= 202.5)&&(heading < 217.5)){// LED 14
        indicator = 14;
        #ifdef debug
          Serial.println("LED 14");
        #endif 
      } 
      if((heading >= 217.5)&&(heading < 232.5)){// LED 15
        indicator = 15;
        #ifdef debug
          Serial.println("LED 15");
        #endif 
      }    
    }
    if((heading >= 232.5)&&(heading < 277.5)){//division par 8 (6/8)
      if((heading >= 232.5)&&(heading < 247.5)){// LED 16
        indicator = 16;
        #ifdef debug
          Serial.println("LED 16");
        #endif 
      }
      if((heading >= 247.5)&&(heading < 262.5)){// LED 17
        indicator = 17;
        #ifdef debug
          Serial.println("LED 17");
        #endif 
      } 
      if((heading >= 262.5)&&(heading < 277.5)){// LED 18
        indicator = 18;
        #ifdef debug
          Serial.println("LED 18");
        #endif 
      }    
    } 
  }
  if((heading >= 277.5)||(heading < 7.5)){//division par 4 (4/4)
    if((heading >= 277.5)&&(heading < 322.5)){//division par 8 (7/8)
      if((heading >= 277.5)&&(heading < 292.5)){// LED 19
        indicator = 19;
        #ifdef debug
          Serial.println("LED 19");
        #endif 
      }
      if((heading >= 292.5)&&(heading < 307.5)){// LED 20
        indicator = 20;
        #ifdef debug
          Serial.println("LED 20");
        #endif 
      } 
      if((heading >= 307.5)&&(heading < 322.5)){// LED 21
        indicator = 21;
        #ifdef debug
          Serial.println("LED 21");
        #endif 
      }     
    }
    if((heading >= 322.5)||(heading < 7.5)){//division par 8 (8/8)
      if((heading >= 322.5)&&(heading < 337.5)){// LED 22
        indicator = 22;
        #ifdef debug
          Serial.println("LED 22");
        #endif 
      }
      if((heading >= 337.5)&&(heading < 352.5)){// LED 23
        indicator = 23;
        #ifdef debug
          Serial.println("LED 23");
        #endif 
      } 
      if((heading >= 352.5)||(heading < 7.5)){// LED  0
        indicator = 0;
        #ifdef debug
          Serial.println("LED 0");
        #endif 
      }    
    } 
  }   
} //end setIndicator()

/**
 * Change L'état (On/Off, Couleur ...) de chaques LEDs
 * de façons à afficher la Bousole en l'état au moment
 */
void showIndicator(){

  int indicatorLeft = indicator - 1;
  int indicatorRight = indicator + 1;
  if (indicatorLeft < 0){
    indicatorLeft += 24; 
  }
  if (indicatorRight > 23){
    indicatorRight -= 24; 
  }

  colorWipe(neopixel.Color(0, 0, level), 0);             //set All Blue (background dial color)
  neopixel.setPixelColor(indicator, level, 0, 0);        // set indicator RED
  neopixel.setPixelColor(indicatorLeft, 0, level, 0);    // set indicator border GREEN
  neopixel.setPixelColor(indicatorRight, 0, level, 0);   // set indicator border GREEN

  neopixel.show(); // Push bits out!

}

void colorWipe(uint32_t c, uint8_t wait) {
  for(uint16_t i=0; i<neopixel.numPixels(); i++) {
    neopixel.setPixelColor(i, c);
    //neopixel.show();
    delay(wait);
  }
}

