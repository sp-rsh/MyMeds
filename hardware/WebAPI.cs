using System;

namespace CSP_Hardware;

public static class WebAPI
{
    public static DispenseEvent? GetNextDispenseEvent()
    {
        Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} returned placeholder event!");
        return new()
        {
            hasDispensed = false,
            targetTime = DateTime.UtcNow.AddMinutes(0),
            targetTimeBuffer = new(0,0,30),
            dispenseRow = [true]
        };
    }
    public static bool PostDispenseEvent(DispenseEvent dEvent)
    {
        Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEvent)} used PLACEHOLDER for POST (hasDispensed: {dEvent.hasDispensed}; time: {dEvent.dispensedTime})");
        return true;
    }
}
