FROM hypriot/rpi-node

RUN apt-get update
RUN apt-get install libraspberrypi-bin

COPY . /app

ENTRYPOINT ["node", "/app/index.js"]