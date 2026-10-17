using System.IO.Ports;
namespace CSP_Hardware;

public class Fingerprint
{
    SerialPort serial;
    bool isLedEnabled = false;
    public Fingerprint(string port)
    {
        serial = new(port, 115200, Parity.None, 8, StopBits.One)
        {
            ReadTimeout = 10000,
            WriteTimeout = 10000
        };
        serial.Open();
        if(TestConnection() != Status.SUCCESS)
            throw new Exception($"Unable to open Serial Port at {port}");
    }
    public Status TestConnection() => SendCommandNN(CommandType.TEST_CONNECTION).status;
    public bool isFingerDetected()
    {
        Response res = SendCommandNN(CommandType.FINGER_DETECT);
        if(!res.isValid || res.data == null || res.data.Length < 1)
            return false;
        if(res.status == Status.SUCCESS)
            return res.data[0] == 1;
        Console.WriteLine($"[ERROR] {nameof(Fingerprint)}.{nameof(isFingerDetected)} Unexpected Return Status: {res.status}");
        return false;
    }
    public bool WaitForFinger(ref bool cancel)
    {
        while(!isFingerDetected() && !cancel)
            Thread.Sleep(100);
        return !cancel;
    }
    public bool LED
    {
        get
        {
            return isLedEnabled;
        }
        set
        {
            if(value == isLedEnabled)
                return;
            Response res = SendCommandNN(CommandType.LED_CONTROL, [(byte)(value?1:0), 0]);
            if(res.isValid && res.status == Status.SUCCESS)
                isLedEnabled = value;
            else
                Console.WriteLine($"[ERROR] {nameof(Fingerprint)}.{nameof(LED)}.set Unexpected Error: isValid={res.isValid} status={res.status}");
        }
    }
    public bool TryVerifyFingerprint(ref bool cancel, out int fingerID)
    {
        fingerID = -1;
        while(!cancel)
        {
            LED = true;
            if(!WaitForFinger(ref cancel))
                continue;
            if(CaptureImage() != Status.SUCCESS)
                continue;
            LED = false;
            if(GenerateTemplate(0) != Status.SUCCESS)
                continue;
            (Status, int) res = SearchTemplates(0);
            if(res.Item1 == Status.SUCCESS)
            {
                fingerID = res.Item2;
                return true;
            }
            if(res.Item1 == Status.ERROR_TEMPLATE_ALLEMPTY || res.Item1 == Status.ERROR_IDENTIFY)
                return false;
        }
        LED = false;
        return false;
    }
    Status CaptureImage() => SendCommandNN(CommandType.CAPTURE_IMAGE).status;
    Status GenerateTemplate(byte bufferID){
        if(bufferID > 2)
            return Status.ERROR_BUFFERID_INVALID;
        return SendCommandNN(CommandType.GENERATE_TEMPLATE, [bufferID, 0]).status;
    }
    (Status, int) SearchTemplates(byte bufferID)
    {
        if(bufferID > 2)
            return (Status.ERROR_BUFFERID_INVALID, -1);
        Response res = SendCommandNN(CommandType.SEARCH_TEMPLATE, [bufferID, 0, 1, 0, 0xFF, 0]);
        if(res.status != Status.SUCCESS)
            return (res.status, -1);
        if(res.data == null || res.data.Length < 2)
            return (Status.ERROR_COM, -1);
        return (Status.SUCCESS, res.data[0] | res.data[1] << 8);
    }
    Response SendCommandNN(CommandType type, byte[]? data = null) => DeconstructResponse(SendCommand(ConstructCommand(type, data)));
    byte[] SendCommand(byte[] cmd)
    {
        lock(this)
        {
            serial.Write(cmd, 0, cmd.Length);
            int waitCounter = 100;
            while(serial.BytesToRead < 26 && waitCounter > 0)
            {
                Thread.Sleep(10);
                waitCounter--;
            }
            if(serial.BytesToRead < 26)
                throw new Exception("FPS Response Timed out!");
            byte[] res = new byte[26];
            int length = serial.Read(res, 0, res.Length);
            if(length != res.Length)
                throw new Exception("Invalid Response Length!");
            return res;
        }
    }
    ushort ComputeChecksum(byte[] data, int length = 0)
    {
        uint sum = 0;
        if(length < 1)
            length = data.Length;
        for (int i = 0; i < length; i++)
            sum += data[i];
        return (ushort)(sum&0xFFFF);
    }
    byte[] ConstructCommand(CommandType type, byte[]? data = null)
    {
        if(data != null && data.Length > 16)
            throw new ArgumentOutOfRangeException("Data is longer than 16 Bytes!");
        byte[] res = new byte[26];
        res[0] = 0x55;
        res[1] = 0xAA;
        res[4] = (byte)((ushort)type&0xFF);
        res[5] = (byte)((ushort)type>>8);
        res[6] = (byte)((ushort)(data?.Length??0)&0xFF);
        res[7] = (byte)((ushort)(data?.Length??0)>>8);
        if(data != null && data.Length > 0)
            Buffer.BlockCopy(data, 0, res, 8, data.Length);
        ushort checksum = ComputeChecksum(res);
        res[24] = (byte)(checksum&0xFF);
        res[25] = (byte)(checksum>>8);
        return res;
    }
    Response DeconstructResponse(byte[] rawResponse)
    {
        Response res = new()
        {
            isValid = rawResponse.Length == 26 && ComputeChecksum(rawResponse, 24) == (rawResponse[24] | rawResponse[25] << 8) && rawResponse[0] == 0xAA && rawResponse[1] == 0x55,
            status = Status.ERROR_COM
        };
        if (!res.isValid)
            return res;
        res.type = (CommandType)(rawResponse[4] | rawResponse[5] << 8);
        if(rawResponse[6] == 0 && rawResponse[7] == 0)
        res.data = null;
        else
        {
            res.data = new byte[rawResponse[6] | rawResponse[7] << 8];
            res.isValid &= res.data.Length < 15;
            if(!res.isValid)
                return res;
            Buffer.BlockCopy(rawResponse, 10, res.data, 0, res.data.Length);
        }
        res.status = (Status)(rawResponse[8] | rawResponse[9] << 8);
        return res;
    }
    struct Response
    {
        public bool isValid;
        public CommandType type;
        public Status status;
        public byte[]? data;
    }
    public enum Status : ushort {
        SUCCESS = 0x00,
        ERROR_FAIL = 0x01,
        ERROR_COM = 0x02,
        ERROR_VERIFY = 0x10,
        ERROR_IDENTIFY = 0x11,
        ERROR_ID_EMPTY = 0x12,
        ERROR_ID_EXISTS = 0x13,
        ERROR_TEMPLATE_ALLEMPTY = 0x14,
        ERROR_ID_ALLUSED = 0x15,
        ERROR_ID_BROKEN = 0x16,
        ERROR_TEMPLATE_INVALID = 0x17,
        ERROR_ID_DUPLICATE = 0x18,
        ERROR_BAD_QUALITY = 0x19,
        ERROR_MERGE_FAIL = 0x1A,
        ERRPR_TEMPLATEID_INVALID = 0x1D,
        ERROR_PARAM_INVALID = 0x22,
        ERROR_MERGE_COUNT = 0x25,
        ERROR_BUFFERID_INVALID = 0x26,
        ERROR_FINGER_NOT_DETECTED = 0x28,
        ERROR_FINGER_CANCLED = 0x41
    }
    enum CommandType : ushort {
        TEST_CONNECTION = 0x0001,
        FINGER_DETECT = 0x0021,
        LED_CONTROL = 0x0024,
        CAPTURE_IMAGE = 0x0020,
        GENERATE_TEMPLATE = 0x0060,
        SEARCH_TEMPLATE = 0x0063
    }
}