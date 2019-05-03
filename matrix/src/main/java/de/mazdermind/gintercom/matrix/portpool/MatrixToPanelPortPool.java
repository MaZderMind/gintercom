package de.mazdermind.gintercom.matrix.portpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mazdermind.gintercom.matrix.configuration.model.Config;

@Component
public class MatrixToPanelPortPool extends PortPool {
	public MatrixToPanelPortPool(@Autowired Config config) {
		super(config.getMatrixConfig().getPorts().getMatrixToPanel());
	}
}
