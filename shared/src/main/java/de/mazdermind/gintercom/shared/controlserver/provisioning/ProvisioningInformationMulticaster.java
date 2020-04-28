package de.mazdermind.gintercom.shared.controlserver.provisioning;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class ProvisioningInformationMulticaster {
	private static final Logger log = LoggerFactory.getLogger(ProvisioningInformationMulticaster.class);
	private final ListableBeanFactory beanFactory;

	public ProvisioningInformationMulticaster(
		ListableBeanFactory beanFactory
	) {
		this.beanFactory = beanFactory;
	}

	public void dispatch(ProvisioningInformation provisionInformation) {
		// Avoid cyclic dependency with ConnectionLifecycleManager
		Collection<ProvisioningInformationAware> provisioningInformationAwares = beanFactory
			.getBeansOfType(ProvisioningInformationAware.class).values();

		log.info("Found {} ProvisioningInformationAware Implementations: {}",
			provisioningInformationAwares.size(), classNamesList(provisioningInformationAwares));

		provisioningInformationAwares.forEach(provisioningInformationAware ->
			provisioningInformationAware.handleProvisioningInformation(provisionInformation));
	}
}