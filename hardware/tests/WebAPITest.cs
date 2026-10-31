using CSP_Hardware;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Company.TestProject1;

[TestClass]
public class WebAPITest
{
    [TestMethod]
    public async Task Test_GetNextDispenseEvent()
    {
        Config config = new("~/mymedz/config.xml");
        WebAPI api = new(config);
        DispenseEvent? dEvent = await api.GetNextDispenseEvent();
        Assert.IsNotNull(dEvent, $"{nameof(api.GetNextDispenseEvent)} shouldn't return null");
        DispenseEvent nEvent = (DispenseEvent)dEvent;
        Assert.AreNotEqual(-1, nEvent.eventID, $"{nameof(api.GetNextDispenseEvent)} returned placeholder Event");
    }
    [TestMethod]
    public async Task Test_PostDispenseEventAsync()
    {
        Config config = new("~/mymedz/config.xml");
        WebAPI api = new(config);
        DispenseEvent dEvent = new()
        {
            eventID = -1,
            hasDispensed = true,
            dispensedTime = DateTime.UtcNow,
        };
        Assert.AreNotEqual("PLACEHOLDER/", config.API_DOMAIN, "Requests shouldnt use PLACEHOLDER for return");
        Assert.AreNotEqual("PLACEHOLDER", config.API_POSTEVENT, "Requests shouldnt use PLACEHOLDER for return");
        Assert.IsTrue(await api.PostDispenseEventAsync(dEvent), $"{nameof(api.PostDispenseEventAsync)} failed Posting event");
    }
}
