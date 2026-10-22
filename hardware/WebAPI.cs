using System.Net.Http.Headers;

namespace CSP_Hardware;

public class WebAPI
{
    readonly Config CONFIG;
    readonly HttpClient client;
    public WebAPI(Config config)
    {
        CONFIG = config;
        client = new();
        client.DefaultRequestHeaders.Clear();
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", CONFIG.API_TOKEN);
    }
    public async Task<DispenseEvent?> GetNextDispenseEvent()
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER/" || CONFIG.API_GETEVENT == "PLACEHOLDER")
            return GetNextEvent_PLACEHOLDER();
        try
        {
            string result = await client.GetStringAsync(CONFIG.API_DOMAIN + CONFIG.API_GETEVENT);
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)}: Request returend: {result}");
            return null;
        }
        catch (Exception e)
        {
            Console.WriteLine($"[ERROR] {nameof(WebAPI)}.{nameof(GetNextDispenseEvent)}: {e.Message} at {e.StackTrace}");
        }
        throw new NotImplementedException($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} not implemented for Non-Placeholders");
    }
    public bool PostDispenseEvent(DispenseEvent dEvent)
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER/" || CONFIG.API_POSTEVENT == "PLACEHOLDER")
        {
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEvent)} used PLACEHOLDER for POST (hasDispensed: {dEvent.hasDispensed}; time: {dEvent.dispensedTime})");
            return true;
        }
        throw new NotImplementedException($"{nameof(WebAPI)}.{nameof(PostDispenseEvent)} not implemented for Non-Placeholders");
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
}
