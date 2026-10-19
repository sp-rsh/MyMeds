dotnet publish -c Release -r linux-arm64 --self-contained true
scp ./bin/Release/net10.0/linux-arm64/publish/* mymedz:~/CSP/build/