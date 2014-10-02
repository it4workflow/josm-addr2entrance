package org.openstreetmap.josm.plugins.addr2entrance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.TaggingPresets;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.ImageProvider;

/**
 * @author Harald Hartmann
 */
public class TagDialog extends ExtendedDialog {

	private static final long serialVersionUID = -4813073152394319113L;

	private static final String REMOVE_BUILDING_ENTRANCE = I18n
			.tr("Node already tagged as building=entrance, remove it?");

	public static final String KEY_BUILDING = "building";
	public static final String KEY_ENTRANCE = "entrance";
	public static final String KEY_NAME = "name";
	public static final String KEY_REF = "ref";
	public static final String KEY_WHEELCHAIR = "wheelcair";

	private static final Logger LOGGER = Logger.getLogger(TagDialog.class
			.getName());

	private Node selection;
	private Way referrer;
	private Collection<Node> nodes;
	private TaggingPreset tpAddresses;
	private TaggingPreset tpEntrance;

	private JCheckBox buildingEntranceEnabled;
	private JRadioButton preferBuildingRadio;
	private JRadioButton preferEntranceRadio;

	public static void main(String[] args) {

		Main.initApplicationPreferences();
		Main.pref.enableSaveOnPut(false);
		I18n.init();
		// initialize the plaform hook, and
		Main.determinePlatformHook();
		// call the really early hook before we anything else
		Main.platform.preStartupHook();
		Main.pref.init(false);
		I18n.set(Main.pref.get("language", "de"));
		Collection<Node> nodes = new ArrayList<Node>(1);
		nodes.add(new Node());
		TagDialog dialog = new TagDialog(new Node(), new Way(), nodes);
		dialog.showDialog();

	}

	public TagDialog(Node p_selection, Way p_referrer, Collection<Node> p_nodes) {

		super(Main.parent, I18n.tr("Move addr:* to entrance node"),
				new String[] { I18n.tr("OK"), I18n.tr("Cancel") }, true);

		selection = p_selection;
		referrer = p_referrer;
		nodes = p_nodes;

		for (TaggingPreset preset : TaggingPresets.getTaggingPresets()) {

			if ("Man Made/Man Made/Entrance".equals(preset.getRawName())) {
				tpEntrance = preset;
			}

			if ("Annotation/Addresses".equals(preset.getRawName())) {
				tpAddresses = preset;
			}
		}

		// Collection<OsmPrimitive> osmPrimitives = new ArrayList<OsmPrimitive>(
		// Arrays.asList(selection));
		// tpEntrance.showDialog(osmPrimitives, false);
		tpEntrance.actionPerformed(null);

		JPanel contentPanel = createContentPanel();
		setContent(contentPanel);
		setButtonIcons(new String[] { "ok.png", "cancel.png" });
		setDefaultButton(1);
		setupDialog();
		getRootPane().setDefaultButton(defaultButton);
		setLocationRelativeTo(null);
	}

	// ######################################################################

	@Override
	protected void buttonAction(int buttonIndex, ActionEvent evt) {

		if (buttonIndex == 0) {
			if ((buildingEntranceEnabled != null)
					&& buildingEntranceEnabled.isSelected()) {
				Main.main.undoRedo.add(new ChangePropertyCommand(selection,
						KEY_BUILDING, null));
			}
		}

		setVisible(false);
	}

	// ######################################################################

	private JPanel createContentPanel() {

		JPanel contentPanel = new JPanel(new BorderLayout());

		// Entrance-Panel
		Box entrancePanel = new Box(BoxLayout.Y_AXIS);
		entrancePanel.setBorder(BorderFactory.createTitledBorder(I18n
				.tr("Entrance")));

		if (existsBuildingEntranceTag()) {
			entrancePanel.add(getBuildingEntranceWarningPanel());
			entrancePanel.add(getBuildingEntranceWarningPanelOld());
		}

		if (existsAnotherMainEntrance()) {
			entrancePanel.add(getMainEntranceWarningPanel());
		}

		if (entrancePanel.getComponents().length > 0) {
			contentPanel.add(entrancePanel, BorderLayout.CENTER);
		}

		// Address-Panel
		Box addressPanel = new Box(BoxLayout.Y_AXIS);
		addressPanel.setBorder(BorderFactory.createTitledBorder(I18n
				.tr("Address")));

		if (!existsAnyAddress()) {
			addressPanel.add(getNeitherEntranceNorBuildingHasAddress());
		}
		if (existsSelectionAddress()) {
			addressPanel.add(getEntranceHasAddress());
		}
		if (existsReferrerAddress()) {
			addressPanel.add(getBuildingHasAddress());
		}
		if (existsOneAnotherEntranceWithAddress()) {
			addressPanel.add(getOneAnotherEntranceAddress());
		}
		if (existsSomeEntrancesWithAddressSomeWithout()) {
			addressPanel.add(getSomeEntrancesWithAddressSomeWithout());
		} else if (existsMoreEntrancesWithAddress()) {
			addressPanel.add(getMoreEntrancesAddress());
		}

		if (addressPanel.getComponents().length > 0) {
			contentPanel.add(addressPanel, BorderLayout.SOUTH);
		}

		if ((addressPanel.getComponents().length == 0)
				&& (entrancePanel.getComponents().length == 0)) {
			contentPanel.add(new JLabel(I18n.tr("Everything seems fine."),
					ImageProvider.get("misc/green_check.png"),
					SwingConstants.LEADING));
		}

		return contentPanel;
	}

	private Component getMainEntranceWarningPanel() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		panel.add(new JLabel(ImageProvider.get("data/warning.png")));
		panel.add(new JLabel(
				I18n.tr("There is already another main entrance, please doublecheck")));
		return panel;
	}

	private Component getBuildingEntranceWarningPanel() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		panel.add(new JLabel(ImageProvider.get("data/warning.png")));
		panel.add(new JLabel(I18n
				.tr("Node is already tagged with building=entrance, ")));
		JButton button = new JButton(I18n.tr("remove it?"));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JButton button = (JButton) e.getSource();
				button.setEnabled(false);
				Main.main.undoRedo.add(new ChangePropertyCommand(selection,
						KEY_BUILDING, null));
			}
		});
		panel.add(button);

		return panel;
	}

	private Component getBuildingEntranceWarningPanelOld() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		panel.add(new JLabel(ImageProvider.get("data/warning.png")));

		buildingEntranceEnabled = new JCheckBox(REMOVE_BUILDING_ENTRANCE);
		buildingEntranceEnabled.setEnabled(existsBuildingEntranceTag());
		buildingEntranceEnabled.setFocusable(false);
		buildingEntranceEnabled.setSelected(existsBuildingEntranceTag());
		buildingEntranceEnabled.setToolTipText(REMOVE_BUILDING_ENTRANCE);
		panel.add(buildingEntranceEnabled);

		return panel;
	}

	private Component getNeitherEntranceNorBuildingHasAddress() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		panel.add(new JLabel(ImageProvider.get("data/warning.png")));

		panel.add(new JLabel(I18n.tr("Neither")));

		JButton button = new JButton(I18n.tr("Entrance"));
		button.addActionListener(new CreateEntranceAddressAction(tpAddresses,
				selection));
		panel.add(button);

		panel.add(new JLabel(I18n.tr("nor")));

		button = new JButton(I18n.tr("Building"));
		button.addActionListener(new CreateBuildingAddressAction(tpAddresses,
				referrer));
		panel.add(button);

		panel.add(new JLabel(
				I18n.tr("has an address yet, click the button you prefer to tag it.")));

		return panel;
	}

	private Component getEntranceHasAddress() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		// panel.add(new JLabel(ImageProvider.get("dialogs/valid.png")));
		panel.add(new JLabel(ImageProvider.get("misc/green_check.png")));

		panel.add(new JLabel(I18n
				.tr("The entrance is already tagged with an address,")));

		JButton button = new JButton(I18n.tr("move"));
		button.addActionListener(new MoveOrCopyAddressAction(selection,
				referrer, false));
		panel.add(button);

		panel.add(new JLabel(I18n.tr("or")));

		button = new JButton(I18n.tr("copy"));
		button.addActionListener(new MoveOrCopyAddressAction(selection,
				referrer, true));
		panel.add(button);

		panel.add(new JLabel(I18n.tr("it to building.")));

		return panel;
	}

	private Component getBuildingHasAddress() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		// panel.add(new JLabel(ImageProvider.get("dialogs/valid.png")));
		panel.add(new JLabel(ImageProvider.get("misc/green_check.png")));

		panel.add(new JLabel(I18n
				.tr("The building is already tagged with an address,")));

		JButton button = new JButton(I18n.tr("move"));
		button.addActionListener(new MoveOrCopyAddressAction(referrer,
				selection, false));
		panel.add(button);

		panel.add(new JLabel(I18n.tr("or")));

		button = new JButton(I18n.tr("copy"));
		button.addActionListener(new MoveOrCopyAddressAction(referrer,
				selection, true));
		panel.add(button);

		panel.add(new JLabel(I18n.tr("it to entrance.")));

		return panel;
	}

	private Component getOneAnotherEntranceAddress() {

		JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEADING));

		firstLine.add(new JLabel(ImageProvider.get("data/warning.png")));
		firstLine.add(new JLabel(I18n
				.tr("There is another entrance tagged with address,")));

		JPanel secondLine = new JPanel(new FlowLayout(FlowLayout.LEADING));
		Address address = getAllAddresses(true).iterator().next();
		secondLine.add(new JLabel(address.toString()));

		JPanel thirdLine = new JPanel(new FlowLayout(FlowLayout.LEADING));
		thirdLine.add(new JLabel(I18n.tr("Same address?")));

		JButton button = new JButton(I18n.tr("Move"));
		button.addActionListener(new MoveOrCopyAddressAction(address
				.getPrimitive(), referrer, false));
		thirdLine.add(button);

		thirdLine.add(new JLabel(I18n.tr("it to building, or")));

		button = new JButton(I18n.tr("create"));
		button.addActionListener(new CreateEntranceAddressAction(tpAddresses,
				selection));
		thirdLine.add(button);

		thirdLine.add(new JLabel(I18n.tr("an address for this entrance.")));

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(firstLine);
		box.add(secondLine);
		box.add(thirdLine);

		return box;
	}

	private Component getSomeEntrancesWithAddressSomeWithout() {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

		panel.add(new JLabel(ImageProvider.get("data/warning.png")));
		panel.add(new JLabel(
				I18n.tr("There are some entrances with an address, some without one. Please check!")));

		return panel;
	}

	private Component getMoreEntrancesAddress() {

		JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEADING));

		firstLine.add(new JLabel(ImageProvider.get("data/warning.png")));
		firstLine.add(new JLabel(I18n
				.tr("There are several other entrances tagged with address,")));

		JPanel secondLine = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JButton button = new JButton(I18n.tr("create"));
		button.addActionListener(new CreateEntranceAddressAction(tpAddresses,
				selection));
		secondLine.add(button);
		secondLine.add(new JLabel(I18n.tr("an address for this entrance.")));

		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(firstLine);
		box.add(secondLine);

		return box;
	}

	// ######################################################################

	private boolean existsBuildingEntranceTag() {

		return selection.hasKey(KEY_BUILDING)
				&& selection.get(KEY_BUILDING).equalsIgnoreCase(KEY_ENTRANCE);
	}

	private boolean existsAnotherMainEntrance() {

		int count = 0;
		for (int index = 0; index < referrer.getRealNodesCount(); index++) {
			Node node = referrer.getNode(index);
			if (node == selection) {
				continue;
			}
			if (referrer.getNode(index).hasKey(KEY_ENTRANCE)
					&& "main".equalsIgnoreCase(referrer.getNode(index).get(
							KEY_ENTRANCE))) {
				count++;
			}
		}
		return count > 1;
	}

	private boolean existsAnyAddress() {

		return getAllAddresses(false).size() > 0;
	}

	private boolean existsSelectionAddress() {

		return new Address(selection).hasAddress()
				&& (getAllAddresses(false).size() == 1);
	}

	private boolean existsReferrerAddress() {

		return new Address(referrer).hasAddress()
				&& (getAllAddresses(false).size() == 1)
				&& (getAllOtherEntrances().size() == 0);
	}

	private boolean existsOneAnotherEntranceWithAddress() {

		return (getAllAddresses(true).size() == 1)
				&& (getAllOtherEntrances().size() == 1);
	}

	private boolean existsSomeEntrancesWithAddressSomeWithout() {

		return (getAllOtherEntrances().size() > 1)
				&& (getAllAddresses(true).size() > 0)
				&& (getAllAddresses(true).size() != getAllOtherEntrances()
						.size());
	}

	private boolean existsMoreEntrancesWithAddress() {

		return getAllAddresses(true).size() > 1;
	}

	private Collection<Node> getAllOtherEntrances() {

		Collection<Node> entrances = new ArrayList<>(referrer.getNodesCount());
		for (int index = 0; index < referrer.getRealNodesCount(); index++) {
			Node node = referrer.getNode(index);
			if (node == selection) {
				continue;
			}
			if (node.hasKey(KEY_ENTRANCE)) {
				entrances.add(node);
			}
		}
		return entrances;
	}

	private Collection<Address> getAllAddresses(boolean entrancesOnly) {

		Collection<Address> addresses = new ArrayList<>(
				referrer.getNodesCount() + 1);
		Address address = null;

		if (!entrancesOnly) {
			address = new Address(referrer);
			if (address.hasAddress()) {
				addresses.add(address);
			}
		}
		for (int index = 0; index < referrer.getRealNodesCount(); index++) {
			Node node = referrer.getNode(index);
			if (!node.hasKey(KEY_ENTRANCE)) {
				continue;
			}
			address = new Address(node);
			if (address.hasAddress()) {
				addresses.add(address);
			}
		}
		return addresses;
	}
}
