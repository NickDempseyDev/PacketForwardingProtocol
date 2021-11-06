# PacketForwardingProtocol

## University Assignment - Computer Networks (CS33031)

### Build Instructions

This project uses Docker so first you must install Docker Desktop.

All of the routers, endpoints, and dashboard can be built by simply typing:
```
docker-compose build
```
However, the controller and the networks must be configured manually (due to errors when trying to configure wireshark on the controller)

Creating the controller:
```
docker create --name controller --cap-add=ALL -ti -v /dev/cs33031 java /bin/bash
```
Connect the network:
```
docker network connect network4 controller
```
Run the following command to copy the files over:
```
docker cp -a . controller:/dev/cs33031/assignment1
```
Run and open the container and run the following commands:
```
docker network connect network4 controller
apt-get update
apt-get install wireshark
apt-get install make
```
