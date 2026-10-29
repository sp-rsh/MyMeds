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
# Add Libgpiod
RUN apk update \
	&& export FILENAME_BIN=libgpiod-bin-1.6.4-alpine-3.19-aarch64 \
	&& apk add --no-cache wget unzip \
	&& wget -O libgpiod-bin.zip "https://raw.githubusercontent.com/devdotnetorg/docker-libgpiod/dev/out/${FILENAME_BIN}.zip" \
	&& unzip -o libgpiod-bin.zip -d /usr/ \
	&& rm libgpiod-bin.zip \
	&& apk del wget unzip
ENTRYPOINT ["./CSP_Hardware"]