package de.mazdermind.gintercom.shared.controlserver.connection;

import static de.mazdermind.gintercom.shared.utils.ObjectListClassNameUtil.classNamesList;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class ControlServerSessionTransportErrorMulticaster {
	private static Logger log = LoggerFactory.getLogger(ControlServerSessionTransportErrorMulticaster.class);
	private final ListableBeanFactory beanFactory;

	public ControlServerSessionTransportErrorMulticaster(
		@Autowired ListableBeanFactory beanFactory
	) {
		this.beanFactory = beanFactory;
	}

	public void dispatch(ControlServerSessionTransportErrorEvent transportErrorEvent) {
		// Avoid cyclic dependency with ConnectionLifecycleManager
		Collection<ControlServerSessionTransportErrorAware> transportErrorAwares = beanFactory
			.getBeansOfType(ControlServerSessionTransportErrorAware.class).values();

		log.info("Found {} ControlServerSessionTransportErrorAware Implementations: {}", transportErrorAwares
			.size(), classNamesList(transportErrorAwares));

		transportErrorAwares.forEach(transportErrorAware -> {
			transportErrorAware.handleTransportErrorEvent(transportErrorEvent);
		});
	}
}
