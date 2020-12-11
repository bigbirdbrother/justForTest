package com.uxsino;

import org.snmp4j.*;
import org.snmp4j.mp.*;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

public class SNMPReceiver {
    private static Snmp snmp;
    private static final int VERSION = 1;
    //改成本机IP
    private static final String IP = "192.71.1.35";
    private static final int PORT = 10010;

    public static void main(String[] args) {


        UdpAddress parse = (UdpAddress) GenericAddress.parse("udp:"+IP+"/"+PORT);
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

            if (VERSION==3){
                final OctetString NO_AUTH = new OctetString("noAuthUser");

                USM usm = new USM(SecurityProtocols.getInstance(), new
                        OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);

                UsmUser user = new UsmUser(NO_AUTH,
                        null, null,
                        null, null);
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

        //获取安全等级
        int securityLevel = event.getSecurityLevel();

        //获取安全模型
        int securityModel = event.getSecurityModel();

        //获取安全名称
        byte[] securityName = event.getSecurityName();

        //
        StateReference stateReference = event.getStateReference();

        //返回接收触发此事件的PDU的传输映射。
        TransportMapping transportMapping = event.getTransportMapping();

        String eventString = event.toString();

        PDU pdu = event.getPDU();

        System.out.println("peerAddress="+peerAddress);
        System.out.println("tmStateReference="+tmStateReference);
        System.out.println("maxSizeResponsePDU="+maxSizeResponsePDU);
        System.out.println("messageDispatcher="+messageDispatcher);
        System.out.println("messageProcessingModel="+messageProcessingModel);
        System.out.println("pduHandle="+pduHandle);
        System.out.println("securityLevel="+securityLevel);
        System.out.println("securityModel="+securityModel);
        System.out.println("securityName="+ Arrays.toString(securityName));
        System.out.println("stateReference="+stateReference);
        System.out.println("transportMapping="+transportMapping);
        System.out.println("eventString="+eventString);

        if (PDU.GET ==pdu.getType()) {

        }
        Vector<? extends VariableBinding> vbVector = pdu.getVariableBindings();
        Enumeration<? extends VariableBinding> vbElements = vbVector.elements();
        PDU resultPdu = new PDU();
        resultPdu.setType(PDU.RESPONSE);
        resultPdu.setRequestID(pdu.getRequestID());

        while (vbElements.hasMoreElements()){
            VariableBinding vb = vbElements.nextElement();
            VariableBinding nvb = new VariableBinding();
            switch (vb.getOid().toString()) {
                case "1.1":

                    nvb.setOid(new OID("1.1"));
                    nvb.setVariable(new OctetString("yousillyb"));
//                    nvb.setVariable(new Integer32(55));
//                    nvb.setVariable(new Counter64());
                    System.out.println("I got the parameter:"+vb.getVariable().toString());

                    resultPdu.add(nvb);
                    break;
                case "1.2":
                    System.out.println("I got the parameter:"+vb.getVariable().toString());
                    nvb.setOid(new OID("1.2"));
                    nvb.setVariable(new OctetString("someOfBitch"));
                    resultPdu.add(nvb);

                    break;
            }


        }
        Address address = event.getPeerAddress();
        //        Address address = GenericAddress.parse("udp:192.73.1.54/10010");
        System.out.println("event.getPeerAddress()="+event.getPeerAddress());
        OctetString octetString = new OctetString("public");
//        UserTarget userTarget = new UserTarget();
//        userTarget.setSecurityName();
        CommunityTarget target = new CommunityTarget(address,octetString);
        target.setVersion(SnmpConstants.version1);
        target.setTimeout(0);
        target.setRetries(0);

        try {
            snmp.send(resultPdu,target);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
