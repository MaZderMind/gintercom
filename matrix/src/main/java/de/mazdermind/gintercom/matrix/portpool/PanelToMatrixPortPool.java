package de.mazdermind.gintercom.matrix.portpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
public class PanelToMatrixPortPool extends PortPool {
	public PanelToMatrixPortPool(@Autowired Config config) {
		super(config.getMatrixConfig().getPorts().getPanelToMatrix());
	}
}
