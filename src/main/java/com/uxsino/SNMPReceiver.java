package com.uxsino;

import org.snmp4j.*;
import org.snmp4j.mp.*;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class SNMPReceiver {
	private static Snmp snmp;
	private static final int VERSION = 3;
	//改成本机IP
	private static final String IP = "192.71.1.35";
	private static final int PORT = 10010;

	public static void main(String[] args) {


		UdpAddress parse = (UdpAddress) GenericAddress.parse("udp:" + IP + "/" + PORT);
		DefaultUdpTransportMapping transportMapping = null;
//        VariableBinding[] vb = {new VariableBinding(new OID("1.3.6.1.4.1.45378.1.1.0"))};
		try {
			//创建接收SnmpTrap的线程池，参数： 线程名称及线程数
			ThreadPool threadPool = ThreadPool.create("serverName", 3);
//            MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
//                    new MessageDispatcherImpl());

//            System.out.println(pdu);
//            snmp.setMessageDispatcher(dispatcher);

			transportMapping = new DefaultUdpTransportMapping((UdpAddress) parse);
			snmp = new Snmp(transportMapping);

			snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
			snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
			snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());

			if (VERSION == 3) {
				final OctetString NO_AUTH = new OctetString("noAuthUser");
				final OctetString NO_AUTH_CONTEXT_NAME = new OctetString("noAuth");

				final OctetString AUTH = new OctetString("authUser");
				final OctetString AUTH_CONTEXT_NAME = new OctetString("auth");

				final OctetString PRIV = new OctetString("privUser");
				final OctetString PRIV_CONTEXT_NAME = new OctetString("priv");

				final OID AUTH_PROTOCOL = AuthMD5.ID;
				final OctetString AUTH_PASS = new OctetString("authUser");
				final OID PRIV_PROTOCOL = PrivDES.ID;
				final OctetString PRIV_PASS = new OctetString("privUser");

				OctetString EngineId = new OctetString(MPv3.createLocalEngineID());

				System.out.println(EngineId);

				USM usm = new USM(SecurityProtocols.getInstance(), EngineId
						, 0);
				SecurityModels.getInstance().addSecurityModel(usm);

//                UsmUser user = new UsmUser(NO_AUTH,
//                        null, null,
//                        null, null);
//
                UsmUser user = new UsmUser(PRIV,
                        AUTH_PROTOCOL,AUTH_PASS,
                        PRIV_PROTOCOL,PRIV_PASS);


                snmp.getUSM().addUser(NO_AUTH, user);

			}


			snmp.addCommandResponder(SNMPReceiver::processPdu);
			snmp.listen();

//
//            ResponseEvent responseEvent = snmp.send(pdu, target);
//            PDU request = responseEvent.getRequest();
//            PDU response = responseEvent.getResponse();
//            Object userObject = responseEvent.getUserObject();
//            System.out.println("request="+request);
//            System.out.println("response="+response);
//            System.out.println("userObject="+userObject);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private static void processPdu(CommandResponderEvent event) {
//        System.out.println(event.getPDU());
		Address peerAddress = event.getPeerAddress();       //获取客户端ip地址

		//获取RFC5590定义的传输模型状态引用。
		TransportStateReference tmStateReference = event.getTmStateReference();

		//获取pdu最大长度
		int maxSizeResponsePDU = event.getMaxSizeResponsePDU();

		//获取接收命令(请求PDU)或未确认PDU的消息分派器实例，如报表、陷阱或通知。。
		MessageDispatcher messageDispatcher = event.getMessageDispatcher();

		//消息过程模型
		int messageProcessingModel = event.getMessageProcessingModel();

		//
		PduHandle pduHandle = event.getPduHandle();
		/**
		 *         SecurityLevel.NOAUTH_NOPRIV = 1
		 *         SecurityLevel.AUTH_NOPRIV   = 2
		 *         SecurityLevel.AUTH_PRIV     = 3
		 */

		//获取安全等级,即是否认证(n/y A?)是否加密(n/y P?),1:nAnP; 2:yAnP; 3:yAyP   ps:nAyP是无意义的
		int securityLevel = event.getSecurityLevel();

		//获取安全模型, 即snmp版本号
		int securityModel = event.getSecurityModel();

		//username
		byte[] securityName = event.getSecurityName();

		//
		StateReference stateReference = event.getStateReference();

		//返回接收触发此事件的PDU的传输映射。
		TransportMapping transportMapping = event.getTransportMapping();

		String eventString = event.toString();

		PDU pdu = event.getPDU();

		System.out.println("peerAddress=" + peerAddress);
		System.out.println("tmStateReference=" + tmStateReference);
		System.out.println("maxSizeResponsePDU=" + maxSizeResponsePDU);
		System.out.println("messageDispatcher=" + messageDispatcher);
		System.out.println("messageProcessingModel=" + messageProcessingModel);
		System.out.println("pduHandle=" + pduHandle);
		System.out.println("securityLevel=" + securityLevel);
		System.out.println("securityModel=" + securityModel);
		System.out.println("securityName=" + Arrays.toString(securityName));
		System.out.println("stateReference=" + stateReference);
		System.out.println("transportMapping=" + transportMapping);
		System.out.println("eventString=" + eventString);

		if (PDU.GET == pdu.getType()) {

		}
		Vector<? extends VariableBinding> vbVector = pdu.getVariableBindings();
		Enumeration<? extends VariableBinding> vbElements = vbVector.elements();
		PDU resultPdu = new PDU();
		resultPdu.setType(PDU.RESPONSE);
		resultPdu.setRequestID(pdu.getRequestID());

		while (vbElements.hasMoreElements()) {
			VariableBinding vb = vbElements.nextElement();
			VariableBinding nvb = new VariableBinding();
			switch (vb.getOid().toString()) {
				case "1.1":

					nvb.setOid(new OID("1.1"));
					nvb.setVariable(new OctetString("yousillyb"));
//                    nvb.setVariable(new Integer32(55));
//                    nvb.setVariable(new Counter64());
					System.out.println("I got the parameter:" + vb.getVariable().toString());

					resultPdu.add(nvb);
					break;
				case "1.2":
					System.out.println("I got the parameter:" + vb.getVariable().toString());
					nvb.setOid(new OID("1.2"));
					nvb.setVariable(new OctetString("someOfBitch"));
					resultPdu.add(nvb);

					break;
			}


		}
		Address address = event.getPeerAddress();
		//        Address address = GenericAddress.parse("udp:192.73.1.54/10010");
		System.out.println("event.getPeerAddress()=" + event.getPeerAddress());
		OctetString octetString = new OctetString("public");
//        UserTarget userTarget = new UserTarget();
//        userTarget.setSecurityName();
		CommunityTarget target = new CommunityTarget(address, octetString);
		target.setVersion(SnmpConstants.version1);
		target.setTimeout(0);
		target.setRetries(0);

		try {
			snmp.send(resultPdu, target);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
