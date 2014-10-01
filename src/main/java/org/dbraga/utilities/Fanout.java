package org.dbraga.utilities;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.ArrayList;

/**
 * User: dbraga - Date: 5/30/14
 */
public class Fanout {

  private static final Logger LOG = Logger.getLogger(Fanout.class);

  private PooledConnectionFactory activeMQConnectionFactory;
  private String output;
  private String queuesOutput;
  private String topicsOutput;
  private Connection connection;
  private Session session;
  private ArrayList<MessageProducer> producers;

  public void setActiveMQConnectionFactory(PooledConnectionFactory activeMQConnectionFactory) {
    this.activeMQConnectionFactory = activeMQConnectionFactory;
  }

  public PooledConnectionFactory getActiveMQConnectionFactory() {
    return activeMQConnectionFactory;
  }


  public void init() throws JMSException {
    this.connection = activeMQConnectionFactory.createConnection();
    connection.start();
    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    producers = new ArrayList<MessageProducer>();

    for (String queue: queuesOutput.split(",")){
      if (!"".equals(queue)) producers.add(session.createProducer(session.createQueue(queue)));
    }
    for (String topic: topicsOutput.split(",")){
      if (!"".equals(topic)) producers.add(session.createProducer(session.createTopic(topic)));
    }
  }

  public void setOutput(String output) {
    this.output = output;
  }

  public String getOutput() {
    return output;
  }

  public void setOutputQueues(String outputQueues) {
    this.queuesOutput = outputQueues;
  }

  public String getOutputQueues() {
    return queuesOutput;
  }

  public void setOutputTopics(String outputTopics) {
    this.topicsOutput = outputTopics;
  }

  public String getOutputTopics() {
    return topicsOutput;
  }

  public void distribute(Message msg) throws JMSException {
    for (MessageProducer producer: producers){
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      LOG.info("Forwarding message(" + msg.getJMSMessageID() + ") to destination: " + producer.getDestination().toString());
      producer.send(msg);
    }
  }
}
