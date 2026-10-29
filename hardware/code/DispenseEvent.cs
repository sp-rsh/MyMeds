namespace CSP_Hardware;

public struct DispenseEvent
{
    public int eventID;
    public bool hasDispensed;
    public DateTime targetTime;
    public TimeSpan targetTimeBuffer;
    public DateTime? dispensedTime;
    public int dispenseRow;
}
