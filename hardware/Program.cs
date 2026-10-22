using CSP_Hardware;

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
            if(await API.PostDispenseEventAsync(nextEvent))
                hasNextEvent = false;
            return;
        }
        if(nextEvent.targetTime.Add(nextEvent.targetTimeBuffer).CompareTo(DateTime.UtcNow) < 0)
        {
            await NoDispenseWithinTime();
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
        if(!DispenseTower(nextEvent.dispenseRow))
            return;
        nextEvent.dispensedTime = DateTime.UtcNow;
        nextEvent.hasDispensed = true;
    }
    async Task NoDispenseWithinTime()
    {
        if(await API.PostDispenseEventAsync(nextEvent))
            hasNextEvent = false;
    }
    bool DispenseTower(int dispenseTower)
    {
        if(dispenseTower > servos.Length)
        {
            Console.WriteLine($"{nameof(DispenseTower)} [ERROR] Received Invalid TowerID of {dispenseTower}/{servos.Length}");
            return false;
        }
        dispenseTower -= 1;
        servos[dispenseTower].SetAngle(CONFIG.SERVO_RETRACTANGLE);
        Thread.Sleep(500);
        servos[dispenseTower].SetAngle(CONFIG.SERVO_LOCKEDANGLE);
        Thread.Sleep(500);
        servos[dispenseTower].Disable();
        Console.WriteLine($"{nameof(DispenseTower)}: Dispensed Medicine from Tower{dispenseTower+1}");
        return true;
    }
    static async Task Main(string[] args)
    {
        await new Program().Start();
    }
}