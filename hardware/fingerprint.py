import serial
import time
import threading
from libraries import WS_fingerprintF as Fin

ERR_Code = Fin.ERR_Code()
Baudrate = 115200

def validate():
    while Fin.Check_Finger():
        time.sleep(1)
    ret = Fin.Get_FP_Image()
    if ret == ERR_Code.ERR_SUCCESS:
        ret = Fin.Generate_Template(BufID=0)
        if ret != ERR_Code.ERR_SUCCESS:
            print("Gen_Temp failed, ret = ", ret)
            return False;
        ret, ID, score= Fin.Search_Template(BufID = 0)
        if ret == ERR_Code.ERR_SUCCESS:
            print(f"Verify user successfully, User ID = {ID}, Score ={score}")
            return True
        else:
            print("Verify user failed, ret = ", ret)
            return False
    else:
        print("Get_FP_Image failed, ret = ", ret)
        return False
def enroll():
    ret, ID = Fin.Get_Empty_ID();
    if ret != ERR_Code.ERR_SUCCESS:
        print("Failed getting FingerprintID: ", ret);
        return False;
    if Fin.Get_Status(ID)[1] != 0:
        print("Failed enrolling Fingerprint (ID-Collision)");
        return False;
    for step in range(3):
        print("Please place Finger on Sensor");
        while Fin.Check_Finger():
            time.sleep(1);
        ret = Fin.Get_FP_Image();
        if ret == ERR_Code.ERR_SUCCESS:
            ret = Fin.Generate_Template(step);
            if ret != ERR_Code.ERR_SUCCESS:
                print("Failed generating Fingerprint: ", ret);
                return False;
        print("Please lift your Finger");
        while not Fin.Check_Finger():
            time.sleep(1);
    ret = Fin.Merge_Template(BufID=0, num = 3);
    if ret != ERR_Code.ERR_SUCCESS:
        print("Failed merging Fingerprints: ", ret);
        return False;
    ret = Fin.Store_Template(ID, BufID=0);
    if ret != ERR_Code.ERR_SUCCESS:
        print("Failed to store Fingerprint: ", ret);
        return False;
    print("Successfully added Fingerprint with ID: ", ID);
    return True;