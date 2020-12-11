package com.uxsino;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

class SNMPSender {


	private static final long TIMEOUT = 1000 * 30;
	private static Scanner scanner = new Scanner(System.in);
	private Snmp snmp = null;
	private int version;
	//	private static final String AGENT_IP = "192.73.0.231";
	//	private static final String AGENT_IP = "192.73.0.232";
//	private static final int AGENT_PORT = 10010;
	private static final ResponseListener listener;

	private static Toolkit toolkit;
	private static Dimension screen;

	static {
		toolkit = Toolkit.getDefaultToolkit();
		screen = toolkit.getScreenSize();
		listener = new ResponseListener() {

			public void onResponse(ResponseEvent event) {
//				if (bro.equals(false)) {
				// Always cancel async request when response has been received
				// otherwise a memory leak is created! Not canceling a request
				// immediately can be useful when sending a request to a broadcast
				// address.
				((Snmp) event.getSource()).cancel(event.getRequest(), this);
//				}
				// 处理响应
				PDU request = event.getRequest();
				PDU response = event.getResponse();
				System.out.println("Asynchronise message from(来自) "
						+ event.getPeerAddress() + "\r\n" + "request(发送的请求):");
				if (null != request) {
					request.getVariableBindings().forEach(System.out::println);
				}
				System.out.println("-----------------------------------------------------");
				System.out.println("*****************************************************");
				System.out.println("-----------------------------------------------------");
				System.out.println("response(返回的响应):");
				if (null != response) {
					Vector<? extends VariableBinding> variableBindings = response.getVariableBindings();
					if (variableBindings != null) {

						variableBindings.forEach(System.out::println);
					}

				}
			}

		};
	}


	public SNMPSender(int version) {
		try {
			this.version = version;
//            TransportMapping transport = new DefaultUdpTransportMapping();
			FileInputStream snmpConf = new FileInputStream("." + File.separator + "src/snmp.conf");
			Properties properties = new Properties();
			properties.load(snmpConf);
			String IP = properties.getProperty("ip");
			String PORT = properties.getProperty("port");
			UdpAddress parse = (UdpAddress) GenericAddress.parse("udp:" + IP + "/" + PORT);
//            UdpAddress parse = (UdpAddress) GenericAddress.parse("udp:localhost/10010");

			TransportMapping transport = new DefaultUdpTransportMapping(parse);
			snmp = new Snmp(transport);
			if (version == SnmpConstants.version3) {
				// 设置安全模式
				USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
				SecurityModels.getInstance().addSecurityModel(usm);
			}
			// 开始监听消息
			transport.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(Boolean syn, final Boolean bro, PDU pdu, String addr)
			throws IOException {
		System.out.println("sending message。。。");

		// 生成目标地址对象
		Address targetAddress = GenericAddress.parse(addr);
		Target target = null;
		if (version == SnmpConstants.version3) {
			// 添加用户
			System.out.println(snmp);
			System.out.println(snmp.getUSM());
			snmp.getUSM().addUser(new OctetString("MD5DES"),
					new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
							new OctetString("MD5DESUserAuthPassword"),
							PrivDES.ID,
							new OctetString("MD5DESUserPrivPassword")));
			target = new UserTarget();
			// 设置安全级别
			((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
			((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
			target.setVersion(SnmpConstants.version3);
		} else {
			target = new CommunityTarget();
			if (version == SnmpConstants.version1) {
				target.setVersion(SnmpConstants.version1);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			} else {
				target.setVersion(SnmpConstants.version2c);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			}

		}
		// 目标对象相关设置
		target.setAddress(targetAddress);
		target.setRetries(0);
		target.setTimeout(TIMEOUT);

		if (!syn) {
			// 发送报文 并且接受响应
			System.out.println("send over,waiting for response...");
			ResponseEvent event = snmp.send(pdu, target);
//			// 处理响应
//			System.out.println("Synchronize message from "
//					+ response.getPeerAddress() + "\r\n" + "request:"
//					+ response.getRequest() + "\r\n" + "response:"
//					+ response.getResponse());
//			PDU responsePDU = response.getResponse();
//			if (responsePDU != null) {
//				Vector<? extends VariableBinding> variableBindings = responsePDU.getVariableBindings();
//				Iterator<? extends VariableBinding> it = variableBindings.iterator();
//				while (it.hasNext()) {
//					System.out.println(it.next());
//				}
//			}
			PDU request = event.getRequest();
			PDU response = event.getResponse();
			System.out.println("Asynchronise message from(来自) "
					+ event.getPeerAddress() + "\r\n" + "request(发送的请求):");
			if (null != request) {
				request.getVariableBindings().forEach(System.out::println);
			}

//			makeAline(screen);
			System.out.println("---------------------------------------------------------");
			System.out.println("ErrorStatus="+request.getErrorStatusText());
			System.out.println("ErrorIndex="+request.getErrorIndex());
			System.out.println("RequestID="+request.getRequestID());
			System.out.println("---------------------------------------------------------");
			System.out.println("response(返回的响应):");
			if (null != response) {
				Vector<? extends VariableBinding> variableBindings = response.getVariableBindings();
				if (variableBindings != null) {

					variableBindings.forEach(System.out::println);
				}

			}

			System.out.println("---------------------------------------------------------");
			System.out.println("ErrorStatus="+response.getErrorStatusText());
			System.out.println("ErrorIndex="+response.getErrorIndex());
			System.out.println("RequestID="+response.getRequestID());
			System.out.println("---------------------------------------------------------");

			/**
			 * 输出结果：
			 * Synchronize(同步) message(消息) from(来自) 192.168.1.233/161
			 request(发送的请求):GET[requestID=632977521, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.2.1.1.5.0 = Null]]
			 response(返回的响应):RESPONSE[requestID=632977521, errorStatus=Success(0), errorIndex=0, VBS[1.3.6.1.2.1.1.5.0 = WIN-667H6TS3U37]]

			 */
		} else {
			// 设置监听对象

			// 发送报文
			snmp.send(pdu, target, null, listener);
			System.out.println("send over,waiting for response...");
		}
	}

	private void makeAline(Dimension screen) {
		for (int i = 0; i < screen.width; i++) {
			System.out.print("-");
		}
		System.out.println();

		for (int i = 0; i < screen.width; i++) {
			System.out.print("*");
		}
		System.out.println();

		for (int i = 0; i < screen.width; i++) {
			System.out.print("-");
		}
		System.out.println();
	}

	public static void main(String[] args) throws IOException {
		//Snmp的三个版本号
//		int ver = SnmpConstants.version3;
        int ver = SnmpConstants.version2c;
//		int ver = SnmpConstants.version1;
		SNMPSender manager = new SNMPSender(ver);

		// 构造报文
		//1.3.6.1.4.1.45378.1.1.1.0
		//{username:"uxdb",password:"1",database:"test_db",port:"5432",table:"test_table"}
		PDU pdu = ver == SnmpConstants.version3 ? new ScopedPDU() : new PDU();
//		ScopedPDU pdu = new ScopedPDU();


		// 设置报文类型
		pdu.setType(PDU.GET);
		if (ver == SnmpConstants.version3) {
			ScopedPDU pdu3 = (ScopedPDU) pdu;
			pdu3.setContextName(new OctetString("priv"));
			pdu3.setContextEngineID(new OctetString("sss"));
			pdu = pdu3;
		}
//		((ScopedPDU) pdu).setContextName(new OctetString("priv"));
		try {
			// 发送消息 其中最后一个是想要发送的目标地址
			//manager.sendMessage(false, true, pdu, "udp:192.168.1.229/161");//192.168.1.229 Linux服务器

			//true是异步,false是同步
			breakMark:
			while (true) {
				pdu.clear();
				System.out.println("请输入oid，如：1.3.6.1.4.1.45378.1.1.1.0（若输入空则从配置文件读取）");
				System.out.println("前缀1.3.6.1.4.1.45378可省略.");
				String input = scanner.nextLine();
				if (input != null && !input.equals("")) {
					if (!input.contains("1.3.6.1.4.1.45378")){
						input = "1.3.6.1.4.1.45378." + input;
					}
					System.out.println("请输入数据库连接信息，如：{username:\"uxdb\",password:\"1\",database:\"test_db\",port:\"5432\",table:\"test_table\"}");
					String input_message = scanner.nextLine();
					VariableBinding data = new VariableBinding();
					char[] chars = input.toCharArray();
					for (int i = 0; i < chars.length; i++) {
						if(chars[i]>'0'||chars[i]<'9'||chars[i]=='.'){
							data.setOid(new OID(input));
						}else {
							System.out.println("oid应该以如下形式输入：1.3.6.1.4.1.45378.x.x.x.x");
							break breakMark;
						}
					}
					if (null != input_message && !input_message.equals("")) {
						data.setVariable(new OctetString(input_message));
					} else {
						data.setVariable(new OctetString("{username:\"uxdb\",password:\"1\",database:\"test_db\",port:\"5432\",table:\"test_table\"}"));
					}
					pdu.add(data);
				} else {         //发送全部
					//读取配置文件
					List<VariableBinding> vbList = new ArrayList<>();
//					InputStream inputStream = SNMPSender.class.getClassLoader().getResourceAsStream("oid.conf");
					InputStream inputStream = new FileInputStream("." + File.separator + "src/oid.conf");
					BufferedReader reader = new BufferedReader(new FileReader("." + File.separator + "src/oid.conf"));
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.startsWith("#")||line.startsWith("//")||"".equals(line)){
							continue;
						}
						String[] configs = line.split("=");
						vbList.add(new VariableBinding(new OID(configs[0]),new OctetString(configs[1])));
					}

//					Properties requests = new Properties();
//					requests.load(inputStream);
					inputStream.close();
//					Set<Object> keys = requests.keySet();
//					for (Object key : keys
//					) {
//						String oid = key.toString();
//						String valiable = requests.getProperty(oid);
//						OID oids = new OID(oid);
//						OctetString connectionInfo = new OctetString(valiable);
//						vbList.add(new VariableBinding(oids, connectionInfo));
//					}
					for (VariableBinding vb : vbList
					) {
						pdu.add(vb);
					}

				}

//				InputStream snmpConf = SNMPSender.class.getClassLoader().getResourceAsStream("snmp.conf");
				FileInputStream snmpConf = new FileInputStream("." + File.separator + "src/snmp.conf");
				Properties properties = new Properties();
				properties.load(snmpConf);
				String AGENT_IP = properties.getProperty("webAgent_ip");
				String AGENT_PORT = properties.getProperty("webAgent_snmp_port");
				manager.sendMessage(false, true, pdu, "udp:" + AGENT_IP + "/" + AGENT_PORT);//192.168.1.233 WinServer2008服务器
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
