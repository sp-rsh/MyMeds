FROM mcr.microsoft.com/dotnet/sdk:10.0 AS build
WORKDIR /App

# Copy everything
COPY ./code ./
# Build and publish a release
RUN dotnet publish -c Release -r linux-arm64 -o out

# Build runtime image
FROM mcr.microsoft.com/dotnet/runtime:10.0-noble AS runtime
# Add Libgpiod
RUN apt-get update \
	&& apt-get install -y --install-recommends curl sudo \
	&& curl -SL --output setup-libgpiod.sh https://raw.githubusercontent.com/devdotnetorg/docker-libgpiod/HEAD/setup-libgpiod.sh \
	&& chmod +x setup-libgpiod.sh \
	&& ./setup-libgpiod.sh --type binary --version 1.6.4 --canselect no \
	&& rm setup-libgpiod.sh \
	&& apt-get -y --purge remove curl unzip


WORKDIR /App
COPY --from=build /App/out ./
ENTRYPOINT ["./CSP_Hardware"]