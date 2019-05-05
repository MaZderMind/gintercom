package de.mazdermind.gintercom.shared.controlserver.messagehandler;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.shared.controlserver.messages.provision.ProvisionMessage;
import de.mazdermind.gintercom.shared.controlserver.provisioning.ProvisioningInformationMulticaster;

@Component
@Lazy
public
class ProvisionMessageHandler implements MatrixMessageHandler {
	private static final Logger log = LoggerFactory.getLogger(ProvisionMessageHandler.class);
	private final ProvisioningInformationMulticaster provisioningInformationMulticaster;

	public ProvisionMessageHandler(
		@Autowired ProvisioningInformationMulticaster provisioningInformationMulticaster
	) {
		this.provisioningInformationMulticaster = provisioningInformationMulticaster;
	}

	@Override
	@NonNull
	public Type getPayloadType(@NonNull StompHeaders stompHeaders) {
		return ProvisionMessage.class;
	}

	@Override
	public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
		ProvisionMessage provisionMessage = (ProvisionMessage) o;
		log.info("Received ProvisionMessage with Display-Name {}", provisionMessage.getProvisioningInformation().getDisplay());

		provisioningInformationMulticaster.dispatch(provisionMessage.getProvisioningInformation());
	}

	@Override
	public String getDestination() {
		return "/provision";
	}
}
