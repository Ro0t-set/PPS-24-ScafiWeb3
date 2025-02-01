FROM sbtscala/scala-sbt:eclipse-temurin-21.0.5_11_1.10.5_3.3.4 AS builder
RUN apt-get update && apt-get install -y \
  curl \
  gnupg \
  apt-transport-https \
  wget \
  tar
RUN  apt-get update && apt-get install -y nodejs npm
WORKDIR /app
COPY . .
RUN cd js && npm install
RUN npm install
RUN sbt -J-Xmx4G fastLinkJS
RUN npm run build


####################################

FROM node:23-alpine AS runtime
WORKDIR /app
COPY --from=builder /app .
EXPOSE 4173
CMD ["npm", "run", "preview"]
