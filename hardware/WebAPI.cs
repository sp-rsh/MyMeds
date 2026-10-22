using System.Buffers.Text;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json.Nodes;

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
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(
            "Authorization",
            "Basic " + Convert.ToBase64String(Encoding.UTF8.GetBytes($"{CONFIG.API_DEVICENAME}:{CONFIG.API_TOKEN}")));
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
    public async Task<bool> PostDispenseEventAsync(DispenseEvent dEvent)
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER/" || CONFIG.API_POSTEVENT == "PLACEHOLDER")
        {
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEventAsync)} used PLACEHOLDER for POST (hasDispensed: {dEvent.hasDispensed}; time: {dEvent.dispensedTime})");
            return true;
        }
        try
        {
            JsonObject json = new()
            {
                { "eventID", dEvent.eventID },
                { "hasDispensed", dEvent.hasDispensed },
                { "dispenseTime", dEvent.dispensedTime }
            };
            HttpResponseMessage res = await client.PostAsync(CONFIG.API_DOMAIN + CONFIG.API_POSTEVENT, new StringContent(json.ToString(), Encoding.UTF8, "application/json"));
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEventAsync)}: Post returend: [{res.StatusCode}] {res.Content}");
        }
        catch (Exception e)
        {
            Console.WriteLine($"[ERROR] {nameof(WebAPI)}.{nameof(PostDispenseEventAsync)}: {e.Message} at {e.StackTrace}");
        }
        throw new NotImplementedException($"{nameof(WebAPI)}.{nameof(PostDispenseEventAsync)} not implemented for Non-Placeholders");
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
