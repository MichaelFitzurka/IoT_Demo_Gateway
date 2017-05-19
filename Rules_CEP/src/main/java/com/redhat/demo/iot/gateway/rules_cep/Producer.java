package com.redhat.demo.iot.gateway.rules_cep;

import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	
	 private ConnectionFactory factory;
	 private Connection connection;
	 private Session session;
	 private MessageProducer producer;
	 private String queueName	="";
	 private String brokerURL	="";
	 private String uid			= "";
	 private String passwd		= "";
	 
	 private JAXBContext dataSetContext;
	 private Marshaller dataSetMarshaller;
	 private StringWriter sw = new StringWriter();
	 private Message message;
 
	 public Producer(String queueName, String brokerURL, String uid, String passwd) throws JMSException, JAXBException
	 {
		this.queueName 	= queueName;
		this.brokerURL 	= brokerURL;
		this.uid		= uid;
		this.passwd		= passwd;
		 
		connect(queueName, brokerURL, uid, passwd);

	 	dataSetContext = JAXBContext.newInstance(DataSet.class);
	 	dataSetMarshaller = dataSetContext.createMarshaller();
	 }
	 
	 public void connect(String queueName, String brokerURL, String uid, String passwd) {
		// setup the connection to ActiveMQ
		    factory = new ActiveMQConnectionFactory(uid, passwd, brokerURL+"?connectionTimeout=0&keepAlive=true");
		    	
	        try {
				connection = factory.createConnection();
				connection.start();
		        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		        Destination destination = session.createQueue(queueName);
		        producer = session.createProducer(destination);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
 
	 public void run(DataSet data) {
		 // Convert message to XML
		 sw.getBuffer().setLength(0);
         
	 	 try {
	 	 	 dataSetMarshaller.marshal(data, sw);
	 	 } catch (JAXBException e) {
	 	 	 throw new RuntimeException(e);
	 	 }
         
	 	 run(sw.toString());

	 }
	 
	 public void run(String data)
	 {
		
		try {
			message = session.createTextMessage( data );
			System.out.println("Sending " + data);
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			System.out.println("Session to broker closed due to no traffic, need to reconnect");
			
			connect(queueName, brokerURL, uid, passwd);

			// re-send
			run(data);
			
			e.printStackTrace();
		}
   
	 }
 
	 
    public void close() throws JMSException
    {
        if (connection != null)
        {
            connection.close();
        }
    }
}
