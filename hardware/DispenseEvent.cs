namespace CSP_Hardware;

public struct DispenseEvent
{
    public int EventID;
    public bool hasDispensed;
    public DateTime targetTime;
    public TimeSpan targetTimeBuffer;
    public DateTime? dispensedTime;
    public bool[] dispenseRow;
}
