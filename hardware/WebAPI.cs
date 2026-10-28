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
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(CONFIG.API_TOKEN);
    }
    public async Task<DispenseEvent?> GetNextDispenseEvent()
    {
        if(CONFIG.API_DOMAIN == "PLACEHOLDER/" || CONFIG.API_GETEVENT == "PLACEHOLDER")
            return GetNextEvent_PLACEHOLDER();
        try
        {
            string result = await client.GetStringAsync(CONFIG.API_DOMAIN + CONFIG.API_GETEVENT);
            JsonObject? json = JsonNode.Parse(result)?.AsObject();
            if(json == null)
            {
                Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)}: Expected JSON but received: {result}");
                return null;
            }
            DispenseEvent dEvent = new();
            if(!json.TryGetPropertyValue("id", out JsonNode? id) || id == null || id.GetValueKind() != System.Text.Json.JsonValueKind.Number)
            {
                Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} Request returned no eventID (id)");
                return null;
            }
            dEvent.eventID = id.GetValue<int>();
            if(!json.TryGetPropertyValue("scheduledTime", out JsonNode? time) || time == null)
            {
                Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} Request returned no scheduledTime");
                return null;
            }
            string[] timeComponents = time.GetValue<string>().Split(':');
            int[] timeInts = new int[3];
            for (int i = 0; i < timeComponents.Length; i++)
                if(!int.TryParse(timeComponents[i], out timeInts[i]))
                {
                    Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} Invalid Time compontent \"{timeComponents[i]}\" should be integer");
                    return null;
                }
            TimeSpan timeOnly = new TimeOnly(timeInts[0], timeInts[1], timeInts[2]).ToTimeSpan();
            dEvent.targetTime = DateTime.UtcNow.Date.Add(timeOnly);
            dEvent.targetTimeBuffer = new(1, 0, 0);
            if(!json.TryGetPropertyValue("towerToDispense", out JsonNode? tower) || tower == null || tower.GetValueKind() != System.Text.Json.JsonValueKind.Number)
            {
                Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextDispenseEvent)} Request returned no valid towerToDispense");
                return null;
            }
            dEvent.dispenseRow = tower.GetValue<int>();
            return dEvent;
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
            HttpResponseMessage res = await client.PostAsync(CONFIG.API_DOMAIN + CONFIG.API_POSTEVENT.Replace("{id}", dEvent.eventID.ToString()), new StringContent(""));
            if(res.IsSuccessStatusCode)
                return true;
            Console.WriteLine($"{nameof(WebAPI)}.{nameof(PostDispenseEventAsync)}: Post returend: [{res.StatusCode}] {res.Content}");
            return false;
        }
        catch (Exception e)
        {
            Console.WriteLine($"[ERROR] {nameof(WebAPI)}.{nameof(PostDispenseEventAsync)}: {e.Message} at {e.StackTrace}");
            return false;
        }
    }
    DispenseEvent GetNextEvent_PLACEHOLDER()
    {
        Console.WriteLine($"{nameof(WebAPI)}.{nameof(GetNextEvent_PLACEHOLDER)} returned placeholder event!");
        return new()
        {
            eventID = -1,
            hasDispensed = false,
            targetTime = DateTime.UtcNow.AddSeconds(30),
            targetTimeBuffer = new(0,0,30),
            dispenseRow = 1
        };
    }
}
