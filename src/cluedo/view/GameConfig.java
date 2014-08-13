package cluedo.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cluedo.controller.player.HumanPlayer;

public class GameConfig extends JDialog{

	private ConfigListener configListener;
	private ArrayList<JTextField> nameBoxes;
	private ArrayList<JCheckBox> ais;

	private int numberOfPlayers;
	private int networkCount;
	private int maxPlayers;

	public void setConfigListener(ConfigListener l) {
		this.configListener = l;
	}

	public GameConfig(CluedoFrame cluedoFrame, int min, int max) {
		super(cluedoFrame);

		setTitle("Create Game");
		setMinimumSize(new Dimension(600, 400));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLayout(new GridLayout(1, 5));

		numberOfPlayers = min;
		this.maxPlayers = max;
		createNumberSpinner(min, min, max);

		createPlayerList(min, max);

		createReadyButton();

		setVisible(true);

	}

	private void createReadyButton() {
		JButton submit = new JButton("Done");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				configListener.onConfigured();
				dispose();
			}
		});

		this.add(submit);
	}

	private void revalidate_inputs(){
		for (int i=0;i<maxPlayers;i++){
			if (i < numberOfPlayers - networkCount){
				nameBoxes.get(i).setEditable(true);
			} else {
				nameBoxes.get(i).setEditable(false);
			}
		}
	}

	private void createPlayerList(int def, int max) {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(max+1, 4));

		nameBoxes = new ArrayList<JTextField>();
		ais = new ArrayList<JCheckBox>();

		for (int i = 0; i < max; ++i){
			ButtonGroup group = new ButtonGroup();

			JRadioButton human = new JRadioButton("Human");
			group.add(human);
			human.setVisible(true);
			jp.add(human);

			JRadioButton network = new JRadioButton("Network");
			group.add(network);
			jp.add(network);

			JRadioButton ai = new JRadioButton("AI");
			group.add(ai);
			jp.add(ai);


			final JTextField name = new JTextField();
			if (i < def){
				name.setEditable(true);
			} else {
				name.setEditable(false);
			}
			jp.add(name);

		}

		jp.add(new JLabel(""));

		add(jp);
	}

	private void createNumberSpinner(final int def, final int min, final int max) {
		JPanel jp = new JPanel(new BorderLayout());

		SpinnerNumberModel numberOfPlayersModel = new SpinnerNumberModel(min, min, max, 1);
		final JSpinner spinner = new JSpinner(numberOfPlayersModel);

		jp.add(spinner, BorderLayout.EAST);
		spinner.setValue(def);
		jp.add(new JLabel("How many players?"), BorderLayout.WEST);

		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				numberOfPlayers = (int) spinner.getValue();
				revalidate_inputs();
			}
		});

		this.add(jp);
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public List<String> getHumanNames() {
		ArrayList<String> humanNames = new ArrayList<String>();
		for (JTextField n : nameBoxes){
			if (n.isEnabled() && n.isEditable()) humanNames.add(n.getText());
		}
		return humanNames;
	}

	public int getNetworkCount() {
		return networkCount;
	}

}
