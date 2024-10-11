package com.jlp.pmu.configuration;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;

import jakarta.jms.JMSException;

@Configuration
@EnableJms
public class JMSConfig {
	
	@Bean
	public MQConnectionFactory mqConnectionFactory() throws JMSException{
		System.out.println("mqConnectionFactory 1");
	  MQConnectionFactory connectionFactory = new MQConnectionFactory();
	  connectionFactory.setHostName("testmq.johnlewis.co.uk"); //mq host name
	  connectionFactory.setPort(1417); // mq port
	  connectionFactory.setQueueManager("MQT"); //mq queue manager
	  connectionFactory.setChannel("MQC_T_PM2CLI_MQT"); //mq channel name
	  connectionFactory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
	// connectionFactory.setSSLCipherSuite("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"); //tls cipher suite name
	  System.out.println("mqConnectionFactory 2");
	  return connectionFactory;
	}


	@Bean()
	public DefaultMessageListenerContainer myMessageEventContainer() throws JMSException{
		System.out.println("myMessageEventContainer 1");
	  DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
	  MQConnectionFactory mqConnectionFactory = mqConnectionFactory();
	  container.setAutoStartup(true);
	  container.setConnectionFactory(mqConnectionFactory);
	  container.setDestinationName("MQQ_ZLI_CENPMU_PRINTDATA_REP");
	  container.setMessageListener(new MyEventListener());
	  System.out.println("myMessageEventContainer 2");
	  return container;
	}}

