
//#define DEBUG DBG 
 
// Pin 13 has an LED connected on most Arduino boards.
// give it a name:
#define LED 13
#define LEFT_MOTOR_DIR_PIN 7
#define LEFT_MOTOR_PWM_PIN 9
#define RIGHT_MOTOR_DIR_PIN 8
#define RIGHT_MOTOR_PWM_PIN 10

String name = "Horizon Express";

String buf = "";

// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an outputµ.
  pinMode(LED, OUTPUT);
  pinMode( LEFT_MOTOR_DIR_PIN, OUTPUT );
  pinMode( LEFT_MOTOR_PWM_PIN, OUTPUT );
  pinMode( RIGHT_MOTOR_DIR_PIN, OUTPUT );
  pinMode( RIGHT_MOTOR_PWM_PIN, OUTPUT );

  Serial.begin(9600);

  digitalWrite(LED, HIGH);
  
  // testen
  Serial.print("AT+NAMEHORIZON_EXPRESS");
  
//  Timer1.initialize(10*1000000);
//  Timer1.attachInterrupt(checkBatt);
}

/*
void checkBatt() {
   
}
*/

// the loop routine runs over and over again forever:
void loop() {
  checkSerial();
  
  /*}
  digitalWrite(LED, HIGH);   // turn the LED on (HIGH is the voltage level)
  digitalWrite(LEFT_MOTOR_DIR_PIN, HIGH );    
  for(int i=0; i<250; i+=10) {
    analogWrite( LEFT_MOTOR_PWM_PIN, i );
    delay(200);
  }
  for(int i=200; i>0; i-=10) {
    analogWrite( LEFT_MOTOR_PWM_PIN, i );
    delay(200);
  }
  digitalWrite(LED, LOW);    // turn the LED off by making the voltage LOW
  delay(2000);               // wait for a second
  */
}

void checkSerial() {
  while (Serial.available()) {
    char c = Serial.read();
    if(c == '\n') {
      handleMessage();
      buf = "";
    } else {
      buf.concat(c); 
#ifdef DEBUG
      Serial.print("Buffer: ");
      Serial.println(buf);
#endif
    }
  }
}

void handleMessage() {
#ifdef DEBUG
  Serial.print("Message: ");
  Serial.println(buf);
#endif

  switch(buf.charAt(0)) {
    case 'N': 
      Serial.print("N:");
      Serial.println(name);
      break;
      
    case 'L':
      if(buf.charAt(1) == '0') {
        digitalWrite(LED, LOW);
      } else {
        digitalWrite(LED, HIGH);
      } 
      Serial.print("L:");
      Serial.println(buf.charAt(1));
      break;

    case 'D':
      if(buf.charAt(1) == '0') {
        digitalWrite(LEFT_MOTOR_DIR_PIN, LOW ); 
      } else {
        digitalWrite(LEFT_MOTOR_DIR_PIN, HIGH ); 
      } 
      Serial.print("D:");
      Serial.println(buf.charAt(1));
      break;
        
    case 'S':    
      {
        char spdar[3];
        spdar[0] = buf.charAt(1); 
        spdar[1] = buf.charAt(2); 
        spdar[2] = buf.charAt(3);
        byte spd = atoi(spdar);
        analogWrite( LEFT_MOTOR_PWM_PIN, spd );
        Serial.print("S:");
        Serial.println(spd);
      } 
      break;
      
    case 'B':
      {
        int bat = analogRead(7);
        Serial.print("S:");
        Serial.println(bat);
      }
      break;
    
    default: 
      break;  
  }  
}

byte toByte(char c) {
  return (byte)c - 0x30;
}

