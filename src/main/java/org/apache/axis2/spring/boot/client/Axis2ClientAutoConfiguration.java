package org.apache.axis2.spring.boot.client;

import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.kernel.TransportListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
 

@Configuration
@ConditionalOnProperty(prefix = Axis2ClientProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ Axis2ClientProperties.class })
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 8)
public class Axis2ClientAutoConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;
 
	@Bean
	public ConfigurationContext configContext() throws Exception {
		return ConfigurationContextFactory.createDefaultConfigurationContext();
	}
	
	@Bean
	public Options overrideOptions(Axis2ClientProperties properties,
			ObjectProvider<TransportListener> transportListener) throws Exception {
		
		Options overrideOptions = new Options();
		Axis2ClientOptions options = properties.getOptions();
		
		PropertyMapper map = PropertyMapper.get();
		map.from(options.getAction()).to(overrideOptions::setAction);
		map.from(options.getPassword()).to(overrideOptions::setPassword);
		map.from(options.getProperties()).to(overrideOptions::setProperties);
		map.from(options.getTimeOutInMilliSeconds()).to(overrideOptions::setTimeOutInMilliSeconds);
		map.from(options.getTransportInProtocol()).to(overrideOptions::setTransportInProtocol);
		map.from(options.getUserName()).to(overrideOptions::setUserName);
		map.from(options.isCallTransportCleanup()).to(overrideOptions::setCallTransportCleanup);
		map.from(options.isExceptionToBeThrownOnSoapFault()).to(overrideOptions::setExceptionToBeThrownOnSOAPFault);
		map.from(options.isManageSession()).to(overrideOptions::setManageSession);
		map.from(options.isUseSeparateListener()).to(overrideOptions::setUseSeparateListener);
		map.from(transportListener.getIfAvailable()).to(overrideOptions::setListener);
		
		return overrideOptions;
	}
	
	@Bean
	public Axis2DocClientTemplate axis2DocClientTemplate(Options overrideOptions) throws Exception {
		return new Axis2DocClientTemplate(overrideOptions);
	}
	
	@Bean
	public Axis2RpcClientTemplate axis2RpcClientTemplate(Options overrideOptions) throws Exception {
		return new Axis2RpcClientTemplate(overrideOptions);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
