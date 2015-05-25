
//#define DEBUG DBG 
 
// Pin 13 has an LED connected on most Arduino boards.
// give it a name:
#define LED 13
#define LEFT_MOTOR_DIR_PIN 7
#define LEFT_MOTOR_PWM_PIN 9
#define RIGHT_MOTOR_DIR_PIN 8
#define RIGHT_MOTOR_PWM_PIN 10

String name = "Gelbe Lok";

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
  
<<<<<<< HEAD
  // testen
=======
>>>>>>> 22f7d7d755dd1599a6d86f52c31a6cff1e62e2d7
  Serial.print("AT+NAMEGELBE_LOK");
  
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
  digitalWrite(LED, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(50);               // wait 

  checkSerial();
  digitalWrite(LED, LOW);    // turn the LED off by making the voltage LOW
  delay(50);               // wait 
  
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
    case 'N': // Name
      Serial.print("N:");
      Serial.println(name);
      break;

/*      
    case 'L': // LED 
      if(buf.charAt(1) == '0') {
        digitalWrite(LED, LOW);
      } else {
        digitalWrite(LED, HIGH);
      } 
      Serial.print("L:");
      Serial.println(buf.charAt(1));
      break;
*/

    case 'D': // DIRECTION
      if(buf.charAt(1) == '0') {
        digitalWrite(LEFT_MOTOR_DIR_PIN, LOW ); 
      } else {
        digitalWrite(LEFT_MOTOR_DIR_PIN, HIGH ); 
      } 
      Serial.print("D:");
      Serial.println(buf.charAt(1));
      break;
        
    case 'S': // SET SPEED
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
      
    case 'B': // GET BATT
      {
        int bat = analogRead(7);
        Serial.print(":");
        Serial.println(bat);
      }
      break;
      
    case 'V': // GET VERSION
      Serial.print("AT+VERSION");
      break;
      
    case 'O': // AT Response OK
      if(buf.charAt(1) == 'K') {
        Serial.print("V:");
        Serial.println(buf);
      }
      break;
    
    default: 
      break;  
  }  
}

byte toByte(char c) {
  return (byte)c - 0x30;
}

