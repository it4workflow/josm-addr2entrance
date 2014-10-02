package org.openstreetmap.josm.plugins.addr2entrance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.SelectCommand;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.tagging.TaggingPreset;

public class CreateBuildingAddressAction implements ActionListener {

	TaggingPreset tpAddresses = null;
	Way selection = null;

	public CreateBuildingAddressAction(TaggingPreset addressPreset, Way building) {

		tpAddresses = addressPreset;
		selection = building;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton button = (JButton) e.getSource();
		button.setEnabled(false);

		Collection<OsmPrimitive> osmPrimitives = new ArrayList<OsmPrimitive>(
				Arrays.asList(selection));
		Main.main.undoRedo.add(new SelectCommand(osmPrimitives));

		tpAddresses.actionPerformed(null);

	}

}
