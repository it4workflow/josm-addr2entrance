package org.openstreetmap.josm.plugins.addr2entrance;

import static org.openstreetmap.josm.tools.I18n.tr;
import static org.openstreetmap.josm.tools.I18n.trn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletingComboBox;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletionListItem;
import org.openstreetmap.josm.gui.tagging.ac.AutoCompletionManager;

/**
 * @author Harald Hartmann
 */
public class TagDialog extends ExtendedDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4813073152394319113L;

	private static final String APPLY_CHANGES = tr("Apply Changes");

	public static final String TAG_ADDR_COUNTRY = "addr:country";
	public static final String TAG_ADDR_STATE = "addr:state";
	public static final String TAG_ADDR_CITY = "addr:city";
	public static final String TAG_ADDR_POSTCODE = "addr:postcode";
	public static final String TAG_ADDR_HOUSENUMBER = "addr:housenumber";
	public static final String TAG_ADDR_STREET = "addr:street";
	public static final String TAG_ADDR_PLACE = "addr:place";
	public static final String TAG_ENTRANCE = "entrance";
	public static final String TAG_BUILDING = "building";

	private static final String REMOVE_BUILDING_ENTRANCE = tr("Node already tagged as building=entrance, remove this?");

	public static final String[] entranceStrings = { "yes", "main", "service",
			"exit", "emergency" };

	static private final Logger logger = Logger.getLogger(TagDialog.class
			.getName());

	private OsmPrimitive selection;
	private OsmPrimitive referrer = null;

	private JTextField country;
	private JTextField state;
	private JTextField city;
	private JTextField postcode;
	private JTextField street;
	private JTextField place;
	private JTextField housenumber;
	private JComboBox<String> entrance;
	private JCheckBox countryEnabled;
	private JCheckBox stateEnabled;
	private JCheckBox cityEnabled;
	private JCheckBox postcodeEnabled;
	private JCheckBox streetEnabled;
	private JCheckBox placeEnabled;
	private JCheckBox housenumberEnabled;
	private JCheckBox entranceEnabled;
	private JCheckBox buildingEntranceEnabled;

	public TagDialog(OsmPrimitive p_selection, OsmPrimitive p_referrer) {
		super(Main.parent, tr("Move addr:* to entrance node"), new String[] {
				tr("OK"), tr("Cancel") }, true);

		this.selection = p_selection;
		this.referrer = p_referrer;

		JPanel editPanel = createContentPanel();
		setContent(editPanel);
		setButtonIcons(new String[] { "ok.png", "cancel.png" });
		setDefaultButton(1);
		setupDialog();
		getRootPane().setDefaultButton(defaultButton);

		// middle of the screen
		setLocationRelativeTo(null);
	}

	private JPanel createContentPanel() {

		JPanel editPanel = new JPanel(new BorderLayout());
		GridBagConstraints c = new GridBagConstraints();

		JPanel entrancePanel = new JPanel(new GridBagLayout());
		entrancePanel.setBorder(BorderFactory
				.createTitledBorder(tr("Entrance")));

		entranceEnabled = new JCheckBox(TAG_ENTRANCE);
		entranceEnabled.setFocusable(false);
		entranceEnabled.setSelected(true);
		entranceEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		entrancePanel.add(entranceEnabled, c);

		entrance = new JComboBox<String>(entranceStrings);
		if (selection.hasKey(TAG_ENTRANCE)) {
			entrance.setEditable(true);
			entrance.setSelectedItem(selection.get(TAG_ENTRANCE));
		}
		entrance.setMaximumRowCount(20);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 1;
		entrancePanel.add(entrance, c);

		// remove building=entrance
		buildingEntranceEnabled = new JCheckBox(REMOVE_BUILDING_ENTRANCE);
		buildingEntranceEnabled.setEnabled(hasBuildingEntranceTag());
		buildingEntranceEnabled.setFocusable(false);
		buildingEntranceEnabled.setSelected(hasBuildingEntranceTag());
		buildingEntranceEnabled.setToolTipText(REMOVE_BUILDING_ENTRANCE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 2;
		entrancePanel.add(buildingEntranceEnabled, c);

		class OpenUrlAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop()
								.browse(new URI(
										"https://wiki.openstreetmap.org/wiki/Key:entrance"));
					} catch (IOException ex) { /* TODO: error handling */
					} catch (URISyntaxException e1) {
						// TODO: error handling */
					}
				}
			}
		}

		JButton button = new JButton();
		button.setText("<HTML><U>Wiki for entrance</U></HTML>");
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setToolTipText(tr("Goto wiki"));
		button.setBackground(Color.WHITE);
		button.addActionListener(new OpenUrlAction());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.gridwidth = 2;
		entrancePanel.add(button, c);

		editPanel.add(entrancePanel, BorderLayout.NORTH);

		JPanel addrPanel = new JPanel(new GridBagLayout());
		addrPanel.setBorder(BorderFactory.createTitledBorder(tr("Address")));

		// country
		countryEnabled = new JCheckBox(TAG_ADDR_COUNTRY);
		countryEnabled.setFocusable(false);
		countryEnabled.setSelected(referrer.hasKey(TAG_ADDR_COUNTRY));
		countryEnabled.setToolTipText(APPLY_CHANGES);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 3;
		addrPanel.add(countryEnabled, c);

		country = new JTextField();
		country.setPreferredSize(new Dimension(200, 24));
		country.setEditable(true);
		country.setText(getAddr(TAG_ADDR_COUNTRY));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(country, c);

		// state
		stateEnabled = new JCheckBox(TAG_ADDR_STATE);
		stateEnabled.setFocusable(false);
		stateEnabled.setSelected(referrer.hasKey(TAG_ADDR_STATE));
		stateEnabled.setToolTipText(APPLY_CHANGES);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.gridwidth = 3;
		addrPanel.add(stateEnabled, c);

		state = new JTextField();
		state.setPreferredSize(new Dimension(200, 24));
		state.setEditable(true);
		state.setText(getAddr(TAG_ADDR_STATE));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(state, c);

		// city
		cityEnabled = new JCheckBox(TAG_ADDR_CITY);
		cityEnabled.setFocusable(false);
		cityEnabled.setSelected(referrer.hasKey(TAG_ADDR_CITY));
		cityEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.gridwidth = 3;
		addrPanel.add(cityEnabled, c);

		city = new JTextField();
		city.setPreferredSize(new Dimension(200, 24));
		city.setEditable(true);
		city.setText(getAddr(TAG_ADDR_CITY));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 3;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(city, c);

		// postcode
		postcodeEnabled = new JCheckBox(TAG_ADDR_POSTCODE);
		postcodeEnabled.setFocusable(false);
		postcodeEnabled.setSelected(referrer.hasKey(TAG_ADDR_POSTCODE));
		postcodeEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.gridwidth = 3;
		addrPanel.add(postcodeEnabled, c);

		postcode = new JTextField();
		postcode.setPreferredSize(new Dimension(200, 24));
		postcode.setEditable(true);
		postcode.setText(getAddr(TAG_ADDR_POSTCODE));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 4;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(postcode, c);

		// street
		streetEnabled = new JCheckBox(TAG_ADDR_STREET);
		streetEnabled.setFocusable(false);
		streetEnabled.setSelected(referrer.hasKey(TAG_ADDR_STREET));
		streetEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0;
		c.gridwidth = 1;
		addrPanel.add(streetEnabled, c);

		street = new JTextField();
		street.setPreferredSize(new Dimension(200, 24));
		street.setText(getAddr(TAG_ADDR_STREET));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 5;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(street, c);

		// place
		placeEnabled = new JCheckBox(TAG_ADDR_PLACE);
		placeEnabled.setFocusable(false);
		placeEnabled.setSelected(referrer.hasKey(TAG_ADDR_PLACE));
		placeEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0;
		c.gridwidth = 1;
		addrPanel.add(placeEnabled, c);

		place = new JTextField();
		place.setPreferredSize(new Dimension(200, 24));
		place.setText(getAddr(TAG_ADDR_PLACE));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 6;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(place, c);

		// housenumber
		housenumberEnabled = new JCheckBox(TAG_ADDR_HOUSENUMBER);
		housenumberEnabled.setFocusable(false);
		housenumberEnabled.setSelected(referrer.hasKey(TAG_ADDR_HOUSENUMBER));
		housenumberEnabled.setToolTipText(APPLY_CHANGES);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 7;
		c.weightx = 0;
		c.gridwidth = 3;
		addrPanel.add(housenumberEnabled, c);

		housenumber = new JTextField();
		housenumber.setPreferredSize(new Dimension(200, 24));
		housenumber.setText(getAddr(TAG_ADDR_HOUSENUMBER));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = 7;
		c.weightx = 1;
		c.gridwidth = 1;
		addrPanel.add(housenumber, c);
		editPanel.add(addrPanel, BorderLayout.CENTER);
		return editPanel;
	}

	@Override
	protected void buttonAction(int buttonIndex, ActionEvent evt) {
		if (buttonIndex == 0) {
			updateJOSMSelection();
		}
		setVisible(false);
	}

	protected void updateJOSMSelection() {
		ArrayList<Command> commands = new ArrayList<Command>();

		if (this.entranceEnabled.isSelected()) {
			commands.add(new ChangePropertyCommand(selection, TAG_ENTRANCE,
					this.entrance.getSelectedItem().toString()));
		}

		if (this.cityEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_CITY, city.getText());
		}

		if (this.countryEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_COUNTRY, country.getText());
		}

		if (this.housenumberEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_HOUSENUMBER, housenumber.getText());
		}

		if (this.postcodeEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_POSTCODE, postcode.getText());
		}

		if (this.streetEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_STREET, street.getText());
		}

		if (this.stateEnabled.isSelected()) {
			addCommand(commands, TAG_ADDR_STATE, state.getText());
		}

		if (this.buildingEntranceEnabled.isSelected()
				&& hasBuildingEntranceTag()) {
			commands.add(new ChangePropertyCommand(selection, TAG_BUILDING,
					null));
		}

		if (commands.size() > 0) {
			SequenceCommand sequenceCommand = new SequenceCommand(trn(
					"Updating properties of up to {0} object",
					"Updating properties of up to {0} objects",
					commands.size(), commands.size()), commands);

			// executes the commands and adds them to the undo/redo chains
			Main.main.undoRedo.add(sequenceCommand);
		}
	}

	private boolean hasBuildingEntranceTag() {
		return selection.hasKey(TAG_BUILDING)
				&& selection.get(TAG_BUILDING).equalsIgnoreCase(TAG_ENTRANCE);
	}

	private void addCommand(ArrayList<Command> commands, String key,
			String value) {
		// add key and value to selected node
		ChangePropertyCommand command = new ChangePropertyCommand(selection,
				key, value);
		commands.add(command);
		// remove key and value from referrer
		command = new ChangePropertyCommand(referrer, key, null);
		commands.add(command);
	}

	private String getAddr(String key) {
		String value = null;

		if (referrer.hasKey(key)) {
			value = referrer.get(key);
		}

		return value;
	}

}
