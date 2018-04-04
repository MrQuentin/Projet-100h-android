#include <LSM303.h>
#include <Wire.h>
#include <Adafruit_NeoPixel.h>

//#define debug true

#define PIN 5

//Assign unique ID to the sensor
LSM303 mag;
compass.m_min = (LSM303::vector<int16_t>){-363, -647, -609};
compass.m_max = (LSM303::vector<int16_t>){+880, +1120, +638};

//initiate wheel
Adafruit_NeoPixel strip = Adafruit_NeoPixel(27, PIN, NEO_GRB + NEO_KHZ800);

// assign pixels to directions
const int[] N  = {23, 0, 1};
const int[] NE = { 2, 3, 4};
const int[] E  = { 5, 6, 7};
const int[] SE = { 8, 9,10};
const int[] S  = {11,12,13};
const int[] SW = {14,15,16};
const int[] W  = {17,18,19};
const int[] NW = {20,21,22};

// variable de la valeur initiale de "Nord"
int[] indicators = {23, 0, 1};
//luminosité
const byte level = 10;

float heading;

void setup() {

  strip.begin();
  strip.show();

  #ifdef debug
    Serial.begin(9600);
    Serial.println("Magnetometer Test");
    Serial.println("");

    if(!mag.begin()){
      Serial.println("Ooops, pas de LSM303 détecté ... Vérifiez vos cablages!");
      while(1);
    }
  #endif

  if(mag.begin()){
    colorWipe(strip.Color(55,0,0),50);
  }  
}

void loop() {

  sensors_event_t event;
  mag.getEvent(&event);

  const float Pi = 3.14159;

  heading = (atan2(event.magnetic.y, event.magnetic.x) * 180) / Pi;

  if (heading < 0) {
    heading = 360 + heading;
  }

  #ifdef debug
    Serial.print("Compass Heading : ");
    Serial.println(heading);
  #endif

  setIndicator();
  showIndicator();
  delay(50);    
}

void setIndicator(){
  if ((heading >= 22.5)&&(heading < 112.5)){ // NE et E
    if ((heading >= 22.5)&&(heading < 67.5)){ //NE
      indicators = NE;
    } // end NE
    if ((heading >= 67.5)&&(heading < 112.5)){//E
      indicators = E;
    } // end E
  } // end NE et E
  if ((heading >= 112.5) && (heading < 202.5)){ // SE et S
    if ((heading >= 112.5) && (heading < 157.5)){//SE
      indicators = SE;
    } // end SW
    if ((heading >= 157.5) && (heading < 202.5)){//S
      indicators = S;
    } // end S
  } // end SE et S
  if ((heading < 202.5) && (heading > 292.5)){ // SW et W
    if ((heading < 202.5) && (heading > 247.5){//SW
      indicators = SW;
    } // end SW
    if ((heading < 247.5) && (heading > 292.5){//W
      indicators = W;
    } // end W
  } // end SW et W
  if ((heading >= 292.5) || (heading < 22.5)){ // NW et N
    if ((heading >= 292.5) && (heading < 337.5)){//NW
      indicators = NW;
    } // end NW
    if ((heading >= 337.5) || (heading < 22.5)){//N
      indicators = N;
    } // end N
  } // end NW et N
} //end setIndicator()


