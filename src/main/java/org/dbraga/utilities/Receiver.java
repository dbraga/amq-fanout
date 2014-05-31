package org.dbraga.utilities;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;

/**
 * User: dbraga - Date: 5/30/14
 */
public class Receiver implements MessageListener {

  private PooledConnectionFactory activeMQConnectionFactory;
  private Connection connection;
  private Session session;
  private String input;
  private static final Logger LOG = Logger.getLogger(Fanout.class);
  private Fanout fanout;


  public void init() throws JMSException {
    this.connection = activeMQConnectionFactory.createConnection();
    connection.start();
    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    Destination inputDestination = this.session.createQueue(input);
    MessageConsumer consumer = this.session.createConsumer(inputDestination);
    consumer.setMessageListener(this);
  }

  @Override
  public void onMessage(Message message) {
    try {
      fanout.distribute(message);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  public void setActiveMQConnectionFactory(PooledConnectionFactory activeMQConnectionFactory) {
    this.activeMQConnectionFactory = activeMQConnectionFactory;
  }

  public PooledConnectionFactory getActiveMQConnectionFactory() {
    return activeMQConnectionFactory;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public String getInput() {
    return input;
  }

  public void setFanout(Fanout fanout) {
    this.fanout = fanout;
  }

  public Fanout getFanout() {
    return fanout;
  }
}
