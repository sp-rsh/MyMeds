using System;
using System.Device.Gpio;
using System.Device.Pwm.Drivers;

namespace CSP_Hardware;

public class Servo(int pin)
{
    int gpioPin = pin;
    static GpioController gpio = new();
    SoftwarePwmChannel pwm = new(pin, 50, 0, false, gpio, false);

    public void SetAngle(int angle)
    {
        pwm.DutyCycle = angle/1800f + .02;
        Console.WriteLine("Set DutyCycle to: " + pwm.DutyCycle);
        pwm.Start();
    }
    public void Disable()
    {
        pwm.Stop();
        if(!gpio.IsPinOpen(gpioPin))
            gpio.OpenPin(gpioPin, PinMode.Output);
        gpio.Write(gpioPin, PinValue.Low);
    }
    public void Close()
    {
        pwm.Dispose();
    }
}
