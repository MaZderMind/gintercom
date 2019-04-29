package de.mazdermind.gintercom.debugclient.gui;

import static de.mazdermind.gintercom.debugclient.gui.Constants.BORDER;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GroupButtonGrid extends JPanel {
	private final static int ROWS = 3;
	private final static int COLS = 2;
	private static Logger log = LoggerFactory.getLogger(GroupButtonGrid.class);

	@PostConstruct
	public void configure() {
		EventQueue.invokeLater(() -> {
			log.info("Configuring");
			setLayout(new GridLayout(ROWS, COLS, BORDER, BORDER));
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, BORDER));
			setPreferredSize(new Dimension(400, Integer.MAX_VALUE));

			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					int i = row * COLS + col;
					add(new JButton(Integer.toString(i)));
				}
			}
		});
	}

}
