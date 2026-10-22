git pull --ff-only origin main
docker compose build
rm -rf ./hardware/bin/Release/net10.0/linux-arm64
dotnet publish hardware -c Release -r linux-arm64 --self-contained true