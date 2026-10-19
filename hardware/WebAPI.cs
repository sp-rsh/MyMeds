using System;

namespace CSP_Hardware;

public class WebAPI(Config config)
{
    readonly Config CONFIG = config;
    public DispenseEvent? GetNextDispenseEvent()
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER" || CONFIG.API_GETEVENT == "PLACEHOLDER")
            return GetNextEvent_PLACEHOLDER();
        throw new NotImplementedException($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} not implemented for Non-Placeholders");
    }
    DispenseEvent GetNextEvent_PLACEHOLDER()
    {
        Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextEvent_PLACEHOLDER)} returned placeholder event!");
        return new()
        {
            hasDispensed = false,
            targetTime = DateTime.UtcNow.AddSeconds(30),
            targetTimeBuffer = new(0,0,30),
            dispenseRow = [true]
        };
    }
    public bool PostDispenseEvent(DispenseEvent dEvent)
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER" || CONFIG.API_POSTEVENT == "PLACEHOLDER")
        {
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEvent)} used PLACEHOLDER for POST (hasDispensed: {dEvent.hasDispensed}; time: {dEvent.dispensedTime})");
            return true;
        }
        throw new NotImplementedException($"{nameof(WebAPI)}.{nameof(PostDispenseEvent)} not implemented for Non-Placeholders");
    }
}
