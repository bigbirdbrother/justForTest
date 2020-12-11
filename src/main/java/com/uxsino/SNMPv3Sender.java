package com.uxsino;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPv3Sender {

	private static final OctetString NO_AUTH = new OctetString("noAuthUser");
	private static final OctetString NO_AUTH_CONTEXT_NAME = new OctetString("noAuth");

	private static final OctetString AUTH = new OctetString("authUser");
	private static final OctetString AUTH_CONTEXT_NAME = new OctetString("auth");

	private static final OctetString PRIV = new OctetString("privUser");
	private static final OctetString PRIV_CONTEXT_NAME = new OctetString("priv");

	private static final OID AUTH_PROTOCOL = AuthMD5.ID;
	private static final OctetString AUTH_PASS = new OctetString("authUser");
	private static final OID PRIV_PROTOCOL = PrivDES.ID;
	private static final OctetString PRIV_PASS = new OctetString("privUser");

	public static void main(String[] args) {
		OID oid = new OID("1.3.6.1.2.1.25.1.3.0");
		Address address = GenericAddress.parse("udp:192.71.1.35/10010");
//		getOIDValueV2(oid, address);
		getOIDValueV3(1, oid, address);
//		getOIDValueV3(2, oid, address);
//		getOIDValueV3(3, oid, address);
	}

	/**
	 * @param type==1, snmp v3, no authentication and no privacy
	 *                 type==2, snmp v3, authentication and no privacy
	 *                 type==3, snmp v3, authentication and privacy
	 */

	private static void getOIDValueV3(int type, OID oid, Address address) {
		try {
			long startTime = System.currentTimeMillis();
			Snmp snmp = null;
			OctetString securityName = null;
			OctetString contextName = null;
			int securityLevel = 0;
			switch (type) {
				case 1:
					securityName = NO_AUTH;
					contextName = NO_AUTH_CONTEXT_NAME;
					snmp = createSnmpSession(securityName, null, null, null, null);
					securityLevel = SecurityLevel.NOAUTH_NOPRIV;
					break;
				case 2:
					securityName = AUTH;
					contextName = AUTH_CONTEXT_NAME;
					snmp = createSnmpSession(securityName,
							AUTH_PROTOCOL, AUTH_PASS, null, null);
					securityLevel = SecurityLevel.AUTH_NOPRIV;
					break;
				case 3:
					securityName = PRIV;
					contextName = PRIV_CONTEXT_NAME;
					snmp = createSnmpSession(securityName,
							AUTH_PROTOCOL, AUTH_PASS, PRIV_PROTOCOL, PRIV_PASS);
					securityLevel = SecurityLevel.AUTH_PRIV;
					break;
				default:
					System.out.println("Valid type is 0~3.");
					break;
			}
			snmp.listen();
			UserTarget myTarget = new UserTarget();
			myTarget.setAddress(address);
			myTarget.setVersion(SnmpConstants.version3);// org.snmp4j.mp.*;
			myTarget.setSecurityLevel(securityLevel);
			myTarget.setSecurityName(securityName);
//			myTarget.setAuthoritativeEngineID();
			ScopedPDU pdu = new ScopedPDU();
			VariableBinding var = new VariableBinding(oid);
			pdu.add(var);
			pdu.setContextName(contextName);
			pdu.setType(PDU.GET);
			ResponseEvent response = snmp.send(pdu, myTarget);
			System.out.println(snmp.getUSM().getUserTable().getUser

					(securityName));
			System.out.println(response.getResponse());
			System.out.println(response.getError());
			System.out.println("The cost time for snmpv3:" +

					(System.currentTimeMillis() - startTime));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return;
		}
	}

	private static Snmp createSnmpSession(OctetString securityName,
	                                      OID authProtocol, OctetString authPass,
	                                      OID privacyProtocol, OctetString privacyPass) throws IOException {
		TransportMapping transport;
		transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);

		OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
		System.out.println("localEngineId="+localEngineId);
		USM usm = new USM(SecurityProtocols.getInstance(),localEngineId , 0);


		SecurityModels.getInstance().addSecurityModel(usm);
		UsmUser user = new UsmUser(securityName,
				authProtocol, authPass,
				privacyProtocol, privacyPass);
		snmp.getUSM().addUser(securityName, user);
		return snmp;
	}

	private static void getOIDValueV2(OID oid, Address address) {
		try {
			long startTime = System.currentTimeMillis();
			TransportMapping transport = new DefaultUdpTransportMapping();
			Snmp snmp = new Snmp(transport);
			snmp.listen();
			// pdu
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(oid));
			pdu.setType(PDU.GET);
			// target
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setAddress(address);
			target.setVersion(SnmpConstants.version2c);
			//
			ResponseEvent response = snmp.send(pdu, target);
			System.out.println(response.getResponse());
			System.out.println(response.getError());
			System.out.println("The cost time for snmpv2:" +

					(System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}