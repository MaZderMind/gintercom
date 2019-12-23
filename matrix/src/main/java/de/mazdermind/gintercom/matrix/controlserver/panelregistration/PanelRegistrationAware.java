package de.mazdermind.gintercom.matrix.controlserver.panelregistration;

public interface PanelRegistrationAware {
	void handlePanelRegistration(PanelRegistrationEvent panelRegistrationEvent);

	void handlePanelDeRegistration(PanelDeRegistrationEvent panelDeRegistrationEvent);
}
