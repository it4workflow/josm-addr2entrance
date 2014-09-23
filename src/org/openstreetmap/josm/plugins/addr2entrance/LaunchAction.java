package org.openstreetmap.josm.plugins.addr2entrance;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.SelectionChangedListener;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Shortcut;

public class LaunchAction extends JosmAction implements
		SelectionChangedListener {

	private static final long serialVersionUID = -3508864293222033185L;
	private OsmPrimitive selection = null;
	private OsmPrimitive referrer = null;

	public LaunchAction(String pluginDir) {
		super("Addr2Entrance", "addr2entrance",
				"Launches the Addr2Entrance dialog", Shortcut.registerShortcut(
						"tools:Addr2Entrance", "Addr2Entrance", KeyEvent.VK_K,
						Shortcut.SHIFT), true);

		DataSet.addSelectionListener(this);
		setEnabled(false);

	}

	/**
	 * launch the editor
	 */
	protected void launchEditor() {
		if (!isEnabled()) {
			return;
		}

		TagDialog dialog = new TagDialog(selection, referrer);
		dialog.showDialog();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		launchEditor();
	}

	@Override
	public void selectionChanged(Collection<? extends OsmPrimitive> newSelection) {

		OsmPrimitive primitive = null;

		if ((newSelection != null && newSelection.size() == 1)
				&& (primitive = newSelection.iterator().next()) instanceof Node) {

			selection = primitive;

			if (selection.getReferrers() != null
					&& selection.getReferrers().size() > 0
					&& (primitive = selection.getReferrers().iterator().next()) instanceof Way) {
				referrer = primitive;
				setEnabled(true);
			} else {

				setEnabled(false);
				selection = null;
				referrer = null;
			}

		} else {

			setEnabled(false);
			selection = null;
		}

	}
}
