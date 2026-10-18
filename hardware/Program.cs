
using CSP_Hardware;

class Program
{
    readonly int[] servoPins = [14];
    readonly Fingerprint fpSensor;
    readonly Servo[] servos;
    bool hasNextEvent = false;
    DispenseEvent nextEvent;
    DateTime lastServerUpdate;
    Program()
    {
        fpSensor = new("/dev/ttyAMA2");
        servos = new Servo[servoPins.Length];
        for (int i = 0; i < servoPins.Length; i++)
        {
            servos[i] = new(servoPins[i]);
            servos[i].SetAngle(0);
        }
        Thread.Sleep(1000);
        for (int i = 0; i < servos.Length; i++)
            servos[i].Disable();
        while(true)
        {
            IdleLoop();
            Thread.Sleep(1000);
        }
    }
    void IdleLoop()
    {
        if(!hasNextEvent)
        {
            GetNextEvent();
            return;
        }
        if(nextEvent.hasDispensed)
        {
            if(WebAPI.PostDispenseEvent(nextEvent))
                hasNextEvent = false;
            return;
        }
        if(nextEvent.targetTime.Add(nextEvent.targetTimeBuffer).CompareTo(DateTime.UtcNow) < 0)
        {
            NoDispenseWithinTime();
            return;
        }
        if(nextEvent.targetTime.CompareTo(DateTime.UtcNow) < 0)
        {
            AwaitDispense();
            return;
        }
        if(DateTime.UtcNow.Subtract(lastServerUpdate).Minutes > 0)
            GetNextEvent();
    }
    void GetNextEvent()
    {
        DispenseEvent? res = WebAPI.GetNextDispenseEvent();
        hasNextEvent = res != null;
        if(res == null)
            return;
        nextEvent = (DispenseEvent)res;
        lastServerUpdate = DateTime.UtcNow;
    }
    void AwaitDispense()
    {
        while(!fpSensor.isFingerDetected())
        {
            fpSensor.LED = !fpSensor.LED;
            Thread.Sleep(500);
            if(nextEvent.targetTime.Add(nextEvent.targetTimeBuffer).CompareTo(DateTime.UtcNow) < 0)
                return;
        }
        bool cancel = false;
        new Thread(()=>{
            Thread.Sleep(10000);
            cancel = true;
        }).Start();
        if(!fpSensor.TryVerifyFingerprint(ref cancel, out int fingerID))
            return;
        DispenseTowers(nextEvent.dispenseRow);
        nextEvent.dispensedTime = DateTime.UtcNow;
        nextEvent.hasDispensed = true;
    }
    void NoDispenseWithinTime()
    {
        if(WebAPI.PostDispenseEvent(nextEvent))
            hasNextEvent = false;
    }
    void DispenseTowers(bool[] dispenseTower)
    {
        int length = Math.Min(dispenseTower.Length, servos.Length);
        string towers = "";
        for (int i = 0; i < length; i++)
        {
            towers += $"{dispenseTower[i]} | ";
            if(dispenseTower[i])
                servos[i].SetAngle(180);
        }
        Console.WriteLine($"{nameof(Program)}.{nameof(DispenseTowers)}: {towers[..^3]}");
        Thread.Sleep(1000);
        for(int i = 0; i < length; i++)
            if(dispenseTower[i])
                servos[i].SetAngle(0);
        Thread.Sleep(1000);
        for (int i = 0; i < length; i++)
            if(dispenseTower[i])
                servos[i].Disable();
    }
    static void Main(string[] args)
    {
        new Program();
    }
}