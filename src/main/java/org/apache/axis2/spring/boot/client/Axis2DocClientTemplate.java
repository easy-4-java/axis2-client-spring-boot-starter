/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.axis2.spring.boot.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;

/**
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
public class Axis2DocClientTemplate {
	
	
	protected Options overrideOptions;
	public Axis2DocClientTemplate() {}
	public Axis2DocClientTemplate(Options overrideOptions) {
		this.overrideOptions = overrideOptions;
	}
	
	/**
	 * 
	 * @param serviceURL	: 指定创建服务地址WebService的URL,注意不是WSDL的URL
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement sendReceive(String serviceURL, String namespaceURI, String method) throws AxisFault {  
		return this.sendReceive(serviceURL, namespaceURI + method, namespaceURI, method, new HashMap<>());
	}
	
	/**
	 * 
	 * @param serviceURL	: 指定创建服务地址WebService的URL,注意不是WSDL的URL
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement sendReceive(String serviceURL, String namespaceURI, String method, Map<String, String> args) throws AxisFault {  
		return this.sendReceive(serviceURL, namespaceURI + method, namespaceURI, method, args);
	}
	
	/**
	 * 
	 * @param serviceURL	: 指定创建服务地址WebService的URL,注意不是WSDL的URL
	 * @param action		: WS-Addressing Action / SOAP Action string.
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement sendReceive(String serviceURL, String action, String namespaceURI, String method) throws AxisFault {  
		return this.sendReceive(serviceURL, action, namespaceURI, method, new HashMap<>());
	}
	
	/**
	 * 
	 * @param serviceURL	: 指定创建服务地址WebService的URL,注意不是WSDL的URL
	 * @param action		: WS-Addressing Action / SOAP Action string.
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement sendReceive(String serviceURL, String action, String namespaceURI, String method, Map<String, String> args) throws AxisFault {  
		// 使用Doc方式调用WebService
		ServiceClient serviceClient = createServiceClient();
        // 指定调用WebService的URL  
        EndpointReference targetEpr = new EndpointReference(serviceURL);  
        // 确定目标服务地址
		serviceClient.setTargetEPR(targetEpr);
        // 确定调用方法（wsdl 命名空间地址 (wsdl文档中的targetNamespace) 和 方法名称 的组合）
		Options options = serviceClient.getOptions();
		
		serviceClient.setOverrideOptions(overrideOptions);
		
		options.setAction(action);
		
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		// 指定命名空间，参数： uri--即为wsdl文档的targetNamespace，命名空间 perfix--可不填
		OMNamespace omNs = fac.createOMNamespace(namespaceURI, "");
		// 指定方法
		OMElement omeMethod = fac.createOMElement(method, omNs);
		// 指定方法的参数
		Iterator<Entry<String, String>> ite = args.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String, String> entry = ite.next();
			OMElement omeParam = fac.createOMElement(entry.getKey(), omNs);
			omeParam.setText(entry.getValue());
			omeMethod.addChild(omeParam);
		}
		omeMethod.build();
		// 远程调用web服务
		OMElement result = serviceClient.sendReceive(omeMethod);
        
        return result;  
    }
	
	public void sendReceiveNonBlocking(String serviceURL, String action, String namespaceURI, String method, Map<String, String> args, AxisCallback callback) throws AxisFault {  
		// 使用Doc方式调用WebService
		ServiceClient serviceClient = createServiceClient();
        // 指定调用WebService的URL  
        EndpointReference targetEpr = new EndpointReference(serviceURL);  
        // 确定目标服务地址
		serviceClient.setTargetEPR(targetEpr);
        // 确定调用方法（wsdl 命名空间地址 (wsdl文档中的targetNamespace) 和 方法名称 的组合）
		Options options = serviceClient.getOptions();
		
		serviceClient.setOverrideOptions(overrideOptions);
		
		options.setAction(action);
		
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		// 指定命名空间，参数： uri--即为wsdl文档的targetNamespace，命名空间 perfix--可不填
		OMNamespace omNs = fac.createOMNamespace(namespaceURI, "");
		// 指定方法
		OMElement omeMethod = fac.createOMElement(method, omNs);
		// 指定方法的参数
		Iterator<Entry<String, String>> ite = args.entrySet().iterator();
		while (ite.hasNext()) {
			Map.Entry<String, String> entry = ite.next();
			OMElement omeParam = fac.createOMElement(entry.getKey(), omNs);
			omeParam.setText(entry.getValue());
			omeMethod.addChild(omeParam);
		}
		omeMethod.build();
		
		//serviceClient.addHeader(header);
		
		// 远程调用web服务
		serviceClient.sendReceiveNonBlocking(omeMethod, callback);
    }
	 
	

    protected ServiceClient createServiceClient() throws AxisFault {
        return new ServiceClient();
    }
}
