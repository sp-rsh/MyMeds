
using CSP_Hardware;

class Program
{
    static void Main(string[] args)
    {
        Fingerprint sensor = new("/dev/ttyAMA2");
        Servo servo = new(14);
        bool cancel = false;
        if(sensor.TryVerifyFingerprint(ref cancel, out int ID))
        {
            servo.SetAngle(180);
            Thread.Sleep(1000);
            servo.SetAngle(0);
            Thread.Sleep(1000);
            servo.Disable();
            Console.WriteLine("Finished Dispensing");
        }
        else
            Console.WriteLine("Invalid Fingerprint!");
        servo.Close();
    }
}