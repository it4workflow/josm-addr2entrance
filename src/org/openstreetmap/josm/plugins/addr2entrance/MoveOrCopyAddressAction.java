package org.openstreetmap.josm.plugins.addr2entrance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.tools.I18n;

public class MoveOrCopyAddressAction implements ActionListener {

	OsmPrimitive source;
	OsmPrimitive destination;
	boolean copy;

	public MoveOrCopyAddressAction(OsmPrimitive source,
			OsmPrimitive destination, boolean copy) {

		this.source = source;
		this.destination = destination;
		this.copy = copy;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton button = (JButton) e.getSource();
		button.setEnabled(false);

		Collection<Command> commands = new ArrayList<Command>();

		for (String key : source.getKeys().keySet()) {
			if (key.startsWith("addr:") && !"addr:flats".equals(key)) {
				commands.add(new ChangePropertyCommand(destination, key, source
						.get(key)));
				if (!copy) {
					commands.add(new ChangePropertyCommand(source, key, null));
				}
			}
		}

		if (commands.size() > 0) {
			SequenceCommand sequenceCommand = new SequenceCommand(I18n.trn(
					"Updating properties of up to {0} object",
					"Updating properties of up to {0} objects",
					commands.size(), commands.size()), commands);

			// executes the commands and adds them to the undo/redo chains
			Main.main.undoRedo.add(sequenceCommand);
		}

	}

}
