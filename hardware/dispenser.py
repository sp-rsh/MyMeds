import RPi.GPIO as GPIO;
from time import sleep;
PWM_PIN = 36;
EN_PIN = 38;
def start():
    GPIO.setmode(GPIO.BOARD);
    GPIO.setup(8, GPIO.OUT);
    GPIO.output(8, GPIO.LOW);
    global pwm;
    pwm = GPIO.PWM(8, 50);
    pwm.start(0);
    setAngle(0);
    pass;
def setAngle(angle):
    duty = angle/18 + 2;
    GPIO.output(8, GPIO.HIGH);
    global pwm;
    pwm.ChangeDutyCycle(duty);
    sleep(.5);
    pwm.ChangeDutyCycle(0);
def dispense(dispenserID):
    setAngle(140);
    sleep(1);
    setAngle(0);
    pass;