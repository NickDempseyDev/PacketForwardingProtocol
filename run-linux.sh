cd src
javac Applications/RouterImplementation/RouterImplementation.java -d ../bin
javac Applications/EndpointSenderTest/EndpointSenderTest.java -d ../bin
javac Applications/EndpointReceiverTest/EndpointReceiverTest.java -d ../bin
javac Applications/ControllerTest/ControllerTest.java -d ../bin
cd .. 
sudo docker-compose up