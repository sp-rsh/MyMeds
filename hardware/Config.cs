using System;
using System.Xml.Linq;

namespace CSP_Hardware;

public class Config
{
    public string API_DOMAIN {get; private set;} = "PLACEHOLDER";
    public string API_GETEVENT {get; private set;} = "cspi/getNextEvent";
    public string API_POSTEVENT {get;private set;} = "cspi/postEvent";
    public string API_TOKEN {get; private set; } = "NULL";
    public string FINGERPRINT_PORT  {get; private set;} = "/dev/ttyAMA2";
    public int[] SERVO_PINS {get;private set;} = [14];
    public int SERVO_LOCKEDANGLE {get; private set;} = 0;
    public int SERVO_RETRACTANGLE {get;private set;} = 140;
    public Config(string path)
    {
        if(!File.Exists(path))
            CreateDefault(path);
        LoadConfig(path);
    }
    void LoadConfig(string path)
    {
        XDocument config = XDocument.Load(path);
        XElement? api = config.Root?.Element("API");
        if(api != null)
        {
            API_DOMAIN = api.Element("Domain")?.Value??API_DOMAIN;
            API_GETEVENT = api.Element("getEvent")?.Value??API_GETEVENT;
            API_POSTEVENT = api.Element("postEvent")?.Value??API_POSTEVENT;
            API_TOKEN = api.Element("Token")?.Value??API_TOKEN;
            if(!API_DOMAIN.EndsWith('/'))
                API_DOMAIN += '/';
        }
        FINGERPRINT_PORT = config.Root?.Element("Fingerprint_Port")?.Value??FINGERPRINT_PORT;
        XElement? servos = config.Root?.Element("Servos");
        if(servos != null)
        {
            SERVO_LOCKEDANGLE = int.Parse(servos.Element("LockedAngle")?.Value??SERVO_LOCKEDANGLE.ToString());
            SERVO_RETRACTANGLE = int.Parse(servos.Element("RetractAngle")?.Value??SERVO_RETRACTANGLE.ToString());
            SERVO_PINS = servos.Elements("Pin")?.Select(i => int.Parse(i.Value)).ToArray()??SERVO_PINS;
        }
    }
    void CreateDefault(string path)
    {
        XDocument config = new(new XElement("Config"));
        config.Root?.Add(new XElement("API",
            new XElement("Domain", API_DOMAIN),
            new XElement("getEvent", API_GETEVENT),
            new XElement("postEvent", API_POSTEVENT),
            new XElement("Token", API_TOKEN)
        ));
        config.Root?.Add(new XElement("Fingerprint_Port", FINGERPRINT_PORT));
        config.Root?.Add(new XElement("Servos",
            new XElement("LockedAngle", SERVO_LOCKEDANGLE),
            new XElement("RetractAngle", SERVO_RETRACTANGLE),
            SERVO_PINS.Select(i => new XElement("Pin", i))
        ));
        config.Save(path);
    }
}
