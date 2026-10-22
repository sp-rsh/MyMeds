using CSP_Hardware;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Company.TestProject1;

[TestClass]
public class ConfigTest
{
    [TestMethod]
    public void Test_LoadConfig()
    {
        string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), "mymedz/test_config.xml");
        if(File.Exists(path))
            File.Delete(path);
        Assert.IsFalse(File.Exists(path), $"{nameof(Test_LoadConfig)} failed to remove old Test Config");
        Config defaultConfig = new(path);
        Assert.IsTrue(File.Exists(path), $"{nameof(Config)} failed to generate a default config");
        Config loadedConfig = new(path);
        //Check that variables are loaded properly
        Assert.AreEqual(defaultConfig.API_DEVICENAME, loadedConfig.API_DEVICENAME);
        Assert.AreEqual(defaultConfig.API_DOMAIN, loadedConfig.API_DOMAIN);
        Assert.AreEqual(defaultConfig.API_TOKEN, loadedConfig.API_TOKEN);
        Assert.AreEqual(defaultConfig.FINGERPRINT_PORT, loadedConfig.FINGERPRINT_PORT);
        bool pinsEqual = defaultConfig.SERVO_PINS.Length == loadedConfig.SERVO_PINS.Length;
        for (int i = 0; i < defaultConfig.SERVO_PINS.Length; i++)
            pinsEqual &= defaultConfig.SERVO_PINS[i] == loadedConfig.SERVO_PINS[i];
        Assert.IsTrue(pinsEqual);
    }
}
