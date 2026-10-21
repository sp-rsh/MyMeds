using CSP_Hardware;
using Iot.Device.ExplorerHat;

class Program
{
    readonly Fingerprint fpSensor;
    readonly Servo[] servos;
    readonly Config CONFIG;
    readonly WebAPI API;
    bool hasNextEvent = false;
    DispenseEvent nextEvent;
    DateTime lastServerUpdate;
    Program()
    {
        CONFIG = new("~/mymedz/config.xml");
        API = new(CONFIG);
        fpSensor = new(CONFIG.FINGERPRINT_PORT);
        servos = new Servo[CONFIG.SERVO_PINS.Length];
        for (int i = 0; i < CONFIG.SERVO_PINS.Length; i++)
        {
            servos[i] = new(CONFIG.SERVO_PINS[i]);
            servos[i].SetAngle(CONFIG.SERVO_LOCKEDANGLE);
        }
        Thread.Sleep(1000);
        for (int i = 0; i < servos.Length; i++)
            servos[i].Disable();
    }
    public async Task Start()
    {
        while(true)
        {
            await IdleLoop();
            Thread.Sleep(1000);
        }
    }
    async Task IdleLoop()
    {
        fpSensor.LED = false;
        if(!hasNextEvent)
        {
            await GetNextEvent();
            return;
        }
        if(nextEvent.hasDispensed)
        {
            if(API.PostDispenseEvent(nextEvent))
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
            await GetNextEvent();
    }
    async Task GetNextEvent()
    {
        DispenseEvent? res = await API.GetNextDispenseEvent();
        hasNextEvent = res != null;
        if(res == null)
            return;
        nextEvent = (DispenseEvent)res;
        lastServerUpdate = DateTime.UtcNow;
    }
    void AwaitDispense()
    {
        while(!fpSensor.IsFingerDetected())
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
        if(!fpSensor.VerifyFingerprint(ref cancel, out int fingerID))
            return;
        DispenseTowers(nextEvent.dispenseRow);
        nextEvent.dispensedTime = DateTime.UtcNow;
        nextEvent.hasDispensed = true;
    }
    void NoDispenseWithinTime()
    {
        if(API.PostDispenseEvent(nextEvent))
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
                servos[i].SetAngle(CONFIG.SERVO_RETRACTANGLE);
        }
        Console.WriteLine($"{nameof(Program)}.{nameof(DispenseTowers)}: {towers[..^3]}");
        Thread.Sleep(1000);
        for(int i = 0; i < length; i++)
            if(dispenseTower[i])
                servos[i].SetAngle(CONFIG.SERVO_LOCKEDANGLE);
        Thread.Sleep(1000);
        for (int i = 0; i < length; i++)
            if(dispenseTower[i])
                servos[i].Disable();
    }
    static async Task Main(string[] args)
    {
        await new Program().Start();
    }
}