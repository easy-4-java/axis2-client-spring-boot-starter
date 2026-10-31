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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

/**
 * TODO
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */

public class Axis2RpcClientTemplate {

	protected Options overrideOptions;
	public Axis2RpcClientTemplate() {}
	public Axis2RpcClientTemplate(Options overrideOptions) {
		this.overrideOptions = overrideOptions;
	}
	
	/**
	 * 
	 * @param wsdlURL		: 指定创建WSDL的URL，注意不是服务地址
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement invoke(String wsdlURL, String namespaceURI, String method, Object[] args) throws AxisFault {  
     	return this.invoke(wsdlURL, namespaceURI + method, namespaceURI, method, args);  
    }
	
	/**
	 * 
	 * @param wsdlURL		: 指定创建WSDL的URL，注意不是服务地址
	 * @param action		: WS-Addressing Action / SOAP Action string.
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public OMElement invoke(String wsdlURL, String action, String namespaceURI, String method, Object[] args) throws AxisFault {  
		// 使用RPC方式调用WebService
		RPCServiceClient serviceClient = createRPCServiceClient();
        Options options = serviceClient.getOptions();  
        // 指定调用WebService的URL  
        EndpointReference targetEPR = new EndpointReference(wsdlURL);  
        // 确定目标服务地址
        options.setTo(targetEPR);
        // 确定调用方法（wsdl 命名空间地址 (wsdl文档中的targetNamespace) 和 方法名称 的组合）
		options.setAction(action);
		
		serviceClient.setOverrideOptions(overrideOptions);
        // 指定要调用的方法及wsdl文件的命名空间
		// 1.namespaceURI - 命名空间地址 (wsdl文档中的targetNamespace)
		// 2.localPart - 服务视图名 (wsdl文档中operation的方法名称，例如<wsdl:operation name="getMobileCodeInfo">)
        QName qname = new QName(namespaceURI, method);  
        // 调用方法一 传递参数，调用服务，获取服务返回结果集
        // 调用方法并输出该方法的返回值  
        /* 这里有三个参数的说明：
         * 1，是QName对象，表示要调用的方法名；
         * 2，webservice的参数值，参数类型是Object[]；
         * 3，返回值class对象，参数类型是Class[],
         */  
        return serviceClient.invokeBlocking(qname, args);  
  
    }
	
	/**
	 * 
	 * @param wsdlURL		: 指定创建WSDL的URL，注意不是服务地址
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @param returnTypes	: 指定调用方法返回值的数据类型的class对象  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public Object[] invoke(String wsdlURL, String namespaceURI, String method, Object[] args, Class<?>[] returnTypes) throws AxisFault {  
     	return this.invoke(wsdlURL, namespaceURI + method, namespaceURI, method, args, returnTypes);  
    }
	
	/**
	 * 
	 * @param wsdlURL		: 指定创建WSDL的URL，注意不是服务地址
	 * @param action		: WS-Addressing Action / SOAP Action string.
	 * @param namespaceURI	: 指定要调用的方法wsdl文件的命名空间;通过访问http://localhost:8080/ws/XXXService?wsdl 就可以看见 元素的targetNamespace属性值
	 * @param method		: 指定要调用的方法
	 * @param args			: 指定调用方法的参数值  
	 * @param returnTypes	: 指定调用方法返回值的数据类型的class对象  
	 * @return OMElement对象
	 * @throws AxisFault 异常信息
	 */
	public Object[] invoke(String wsdlURL, String action, String namespaceURI, String method, Object[] args, Class<?>[] returnTypes) throws AxisFault {  
		// 使用RPC方式调用WebService
		RPCServiceClient serviceClient = createRPCServiceClient();
        Options options = serviceClient.getOptions();  
        // 指定调用WebService的URL  
        EndpointReference targetEPR = new EndpointReference(wsdlURL);  
        // 确定目标服务地址
        options.setTo(targetEPR);
        // 确定调用方法（wsdl 命名空间地址 (wsdl文档中的targetNamespace) 和 方法名称 的组合）
		options.setAction(action);
        serviceClient.setOverrideOptions(overrideOptions);
        // 指定要调用的方法及wsdl文件的命名空间
		// 1.namespaceURI - 命名空间地址 (wsdl文档中的targetNamespace)
		// 2.localPart - 服务视图名 (wsdl文档中operation的方法名称，例如<wsdl:operation name="getMobileCodeInfo">)
        QName qname = new QName(namespaceURI, method);  
        // 调用方法一 传递参数，调用服务，获取服务返回结果集
        // 调用方法并输出该方法的返回值  
        /* 这里有三个参数的说明：
         * 1，是QName对象，表示要调用的方法名；
         * 2，webservice的参数值，参数类型是Object[]；
         * 3，返回值class对象，参数类型是Class[],
         */  
        return serviceClient.invokeBlocking(qname, args, returnTypes);  
  
    }
	
	

    protected RPCServiceClient createRPCServiceClient() throws AxisFault {
        return new RPCServiceClient();
    }
}
