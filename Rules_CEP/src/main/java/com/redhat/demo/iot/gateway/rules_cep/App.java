package com.redhat.demo.iot.gateway.rules_cep;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class App 
{
    private static final Logger log = Logger.getLogger(CepServer.class.getName());
			 
    public static void main( String[] args ) throws InterruptedException, JMSException, JAXBException
    {
    	String 	messageFromQueue;
    	
    	String 	sourceAMQBroker = System.getenv("SOURCE_AMQ_BROKER");
    	String 	targetAMQBroker = System.getenv("TARGET_AMQ_BROKER");
    	String 	sourceQueue 	= System.getenv("SOURCE_QUEUE");
    	String 	targetQueue 	= System.getenv("TARGET_QUEUE");
    	String  brokerUID		= System.getenv("BROKER_ADMIN_UID");
    	String  brokerPassword  = System.getenv("BROKER_ADMIN_PASSWD");
    	
    	// Variables introduced for countint incoming and outgoing messages
    	int numberIncomingMessages = 0;
    	int numberOutgoingMessage = 0;

        System.out.println("TARGET_AMQ_BROKER = " + targetAMQBroker);
    
	
    	System.out.println(" Check if remote AMQ-Broker are already available");
    	AMQTester tester = new AMQTester(); 
    	
    	tester.waitForBroker(sourceAMQBroker);
    	tester.waitForBroker(targetAMQBroker);
    	
		Consumer consumer = new Consumer(sourceQueue, sourceAMQBroker, brokerUID, brokerPassword);
		Producer producer = new Producer(targetQueue, targetAMQBroker, brokerUID, brokerPassword);
	
		CepServer cepServer = new CepServer();
		
		// Convert TextMessage to DataSet via jaxb unmarshalling
		JAXBContext jaxbContext = JAXBContext.newInstance(DataSet.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		StringReader reader = null;
		DataSet event = null;

		while ( true ) {
			messageFromQueue = consumer.run(20000);		
			
			numberIncomingMessages++;
			
			if ( messageFromQueue != null ) {
				
	            reader = new StringReader( messageFromQueue );
	            event = (DataSet) unmarshaller.unmarshal(reader);
		
	            event.setRequired(0);	    
	         
            	event = cepServer.insert( event);
      	      	
//	            System.out.println("Rules Event-DeviceType <"+event.getDeviceType()+">");
	                     
	            if ( event.getRequired() == 1 ) {
	            	
//	            	System.out.println("Have to send the message " + event.toCSV());
	            	
	            	producer.run(event);
	            	
	            	numberOutgoingMessage++;
	            		
	            }
	            
	            System.out.println("# of Messages in <"+numberIncomingMessages+"> & out <"+numberOutgoingMessage+">");
	            	            
			}
            
		}
		
    }
}
