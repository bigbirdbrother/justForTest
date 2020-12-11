package com.uxsino;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;

public class Snmp4jManager {
	public static void main(String[] args) throws IOException {
//		初始化snmp,设置snmp版本,绑定ip端口开启监听
		final String IP = "192.71.1.35";
		final String PORT = "10011";
		UdpAddress parse = (UdpAddress) GenericAddress.parse("udp:" + IP + "/" + PORT);
		TransportMapping transport = new DefaultUdpTransportMapping(parse);
		Snmp snmp = new Snmp(transport);
		transport.listen();              //开启snmp监听
//		设置oid、VariableBinding
		PDU pdu = new PDU();    //创建pdu

		ScopedPDU scopedPDU = new ScopedPDU();
//		scopedPDU.setContextEngineID();
//		scopedPDU.setContextName();
//		scopedPDU.setType();
//		scopedPDU.setNonRepeaters();

		UserTarget liangteng = new UserTarget(
				GenericAddress.parse("udp:" + IP + "/" + 10010),
				new OctetString("liangteng"),
				"myAEId".getBytes()
		);
//		new CertifiedTarget();


		OID oid = new OID("1.3.6.1.4.1.45378.1.1.1.0");     //设置需要获取的监控项的oid
		OctetString ConnectionInfo = new OctetString("{username:\"uxdb\",password:\"1\",database:\"test_db\",port:\"5432\",table:\"test_table\"}");                                            //设置需要获取的数据库信息
		pdu.add(new VariableBinding(oid, ConnectionInfo)); //将oid和Variable添加到pdu

//		发送snmp请求
		final String AGENT_IP = "192.71.1.35";     //设置为部署了webAgent的机器的ip
		final String AGENT_SNMP_PORT = "10010";     //设置webAgent的snmp_port,配置文件中可以修改，默认为10010
		String addr = "udp:" + AGENT_IP + "/" + AGENT_SNMP_PORT;
		Address targetAddress = GenericAddress.parse(addr); //设置要连接的snmp-agent地址
		pdu.setType(PDU.GET);          //设置pdu类型
		Target target = new CommunityTarget();       //创建目标对象
		target.setVersion(SnmpConstants.version1);    //设置目标版本为v1

		((CommunityTarget) target).setCommunity(new OctetString("public"));   //设置community明文口令为public

		target.setAddress(targetAddress);      //设置目标地址

//      发送报文 并且接受响应
		ResponseEvent response = snmp.send(pdu, target);
//      处理响应
		System.out.println("Synchronize(同步) message(消息) from(来自) "
				+ response.getPeerAddress() + "\r\n" + "request(发送的请求):"
				+ response.getRequest() + "\r\n" + "response(返回的响应):"
				+ response.getResponse());

//		实际测试结果：
//		Synchronize(同步) message(消息) from(来自) 192.73.0.231/10010
//		request(发送的请求):GET[requestID=1464951431, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.4.1.45378.1.1.1.0 = {username:"uxdb",password:"1",database:"testdb",port:"5432",table:"testtable"}]]
//		response(返回的响应):RESPONSE[requestID=1464951431, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.4.1.45378.1.1.1.0 = 6]]

	}
}
