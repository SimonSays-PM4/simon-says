# The Dockerfile is not directly in the printer-server directory, but in the root directory of the printer-system.
# This is because the printer-server needs to be able to access the printer-api-lib dependency which in docker
# means you need to be at the same level as the printer-api-lib and printer-server directories to be able to copy
# them into the docker image.

FROM node:20.12-bookworm

# Expose the port
EXPOSE 3000

# Copy over the printer-api-lib
WORKDIR /app

# Copy the printer-api-lib
COPY printer-api-lib ./printer-api-lib

# Copy the printer-server
COPY printer-server ./printer-server

WORKDIR /app/printer-server
RUN npm ci

# Start the server
CMD ["npm", "start"]