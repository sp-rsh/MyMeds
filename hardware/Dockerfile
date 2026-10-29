FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /App

# Copy everything
COPY ./code ./
# Build and publish a release
RUN dotnet publish -c Release -r linux-arm64 -o out

# Build runtime image
FROM mcr.microsoft.com/dotnet/runtime:10.0 AS runtime
WORKDIR /App
COPY --from=build /App/out ./
ENTRYPOINT ["./CSP_Hardware"]