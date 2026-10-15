import dispenser
import fingerprint
from time import sleep
def start():
    dispenser.start();
def dispenseOnFingerprint():
    while True:
        if fingerprint.validate():
            dispenser.dispense(0);
            return;
        sleep(1);
start();
dispenseOnFingerprint();