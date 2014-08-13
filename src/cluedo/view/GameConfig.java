package cluedo.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cluedo.controller.player.HumanPlayer;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class GameConfig extends JFrame{

	private ConfigListener configListener;
	
	private ArrayList<JTextField> nameBoxes;
	private ArrayList<JRadioButton> radioButtons;

	private JSpinner numberOfPlayers;
	private int networkCount;

	private JTabbedPane tabs;

	private static final int MINPLAYERS = 2;
	private static final int MAXPLAYERS = 6;
	private static final int DEFAULT_PORT = 5362;

	public void setConfigListener(ConfigListener l) {
		this.configListener = l;
	}

	public GameConfig() {
		super();

		setTitle("Create Game");
		setMinimumSize(new Dimension(600, 350));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BorderLayout());
		this.tabs = new JTabbedPane();
		
		
		JPanel serverTab = new JPanel(new BorderLayout(10, 20));
		createPlayerCountSpinner(serverTab, MINPLAYERS, MINPLAYERS, MAXPLAYERS);
		createPlayerList(serverTab, MINPLAYERS, MAXPLAYERS);
		createServerHostBox(serverTab, "0.0.0.0", DEFAULT_PORT);
		
		JPanel clientTab = new JPanel(new GridLayout(7, 1));
		// TODO create name box, which GUIGameInput will read the value of for get single name
		createHostBox(clientTab, "127.0.0.1", DEFAULT_PORT);
		
		
		tabs.add("Run as server", serverTab);
		tabs.add("Run as client", clientTab);

		add(tabs, BorderLayout.CENTER);
		createReadyButton();
		
		revalidate_inputs();
		setVisible(true);

	}

	private void revalidate_inputs(){
		for (int i=0;i<MAXPLAYERS;i++){
			boolean s;
			if (i < getNumberOfPlayers() - networkCount){
				s = true;
			} else {
				s = false;
			}
			radioButtons.get((i*3)).setEnabled(s);
			radioButtons.get((i*3)+1).setEnabled(s);
			radioButtons.get((i*3)+2).setEnabled(s);
			
			nameBoxes.get(i).setEditable(s && radioButtons.get((i*3)).isSelected());
		}
		localHost.setEnabled(getNetworkCount() > 0);
		localPort.setEnabled(getNetworkCount() > 0);
	}

	private ChangeListener reval = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			revalidate_inputs();
		}
	};

	private JTextField remoteHost;

	private JSpinner remotePort;

	private JTextField localHost;

	private JSpinner localPort;
	
	private void createPlayerCountSpinner(JPanel serverTab, final int def, final int min, final int max) {
		JPanel jp = new JPanel(new BorderLayout());

		SpinnerNumberModel numberOfPlayersModel = new SpinnerNumberModel(min, min, max, 1);
		this.numberOfPlayers = new JSpinner(numberOfPlayersModel);

		jp.add(numberOfPlayers, BorderLayout.EAST);
		numberOfPlayers.setValue(def);
		jp.add(new JLabel("How many players?"), BorderLayout.WEST);

		numberOfPlayers.addChangeListener(reval);

		serverTab.add(jp, BorderLayout.NORTH);
	}
	
	private void createPlayerList(JPanel serverTab, int def, int max) {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(max+1, 2));

		nameBoxes = new ArrayList<JTextField>();
		radioButtons = new ArrayList<JRadioButton>();

		for (int i = 0; i < max; ++i){
			final JTextField name = new JTextField();
			if (i < def){
				name.setEditable(true);
			} else {
				name.setEditable(false);
			}
			jp.add(name);
			nameBoxes.add(name);
			
			
			JPanel sjp = new JPanel();
			ButtonGroup group = new ButtonGroup();

			JRadioButton human = new JRadioButton("Human");
			group.add(human);
			sjp.add(human);
			radioButtons.add(human);
			human.setSelected(true);

			JRadioButton network = new JRadioButton("Network");
			group.add(network);
			sjp.add(network);
			radioButtons.add(network);

			JRadioButton ai = new JRadioButton("AI");
			group.add(ai);
			sjp.add(ai);
			radioButtons.add(ai);
			

			human.addChangeListener(reval);
			network.addChangeListener(reval);
			ai.addChangeListener(reval);
			
			
			jp.add(sjp);
		}

		jp.add(new JLabel(""));

		serverTab.add(jp, BorderLayout.CENTER);
	}
	
	private void createReadyButton() {
		JButton submit = new JButton("Start");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				configListener.onConfigured();
				dispose();
			}
		});
		
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jp.add(submit);
		add(jp, BorderLayout.SOUTH);
	}
	
	private void createHostBox(JPanel clientTab, String def, int defport) {
		JPanel jp = new JPanel(new BorderLayout());
		
		this.remoteHost = new JTextField();
		Font bigfont = new Font(remoteHost.getFont().getName(), remoteHost.getFont().getStyle(), remoteHost.getFont().getSize()*2);
		remoteHost.setFont(bigfont);
		remoteHost.setText(def);
		jp.add(remoteHost, BorderLayout.CENTER);
		
		SpinnerNumberModel portModel = new SpinnerNumberModel(defport, 1, 0xffff, 1);
		this.remotePort = new JSpinner(portModel);
		remotePort.setFont(bigfont);

		jp.add(remotePort, BorderLayout.EAST);
		
		clientTab.add(new JPanel());
		clientTab.add(jp);
	}
	
	private void createServerHostBox(JPanel serverTab, String def, int defport) {
		
		JPanel jp = new JPanel(new BorderLayout());
		
		this.localHost = new JTextField();
		Font bigfont = new Font(localHost.getFont().getName(), localHost.getFont().getStyle(), localHost.getFont().getSize()*2);
		localHost.setFont(bigfont);
		localHost.setText(def);
		jp.add(localHost, BorderLayout.CENTER);
		
		SpinnerNumberModel portModel = new SpinnerNumberModel(defport, 1, 0xffff, 1);
		this.localPort = new JSpinner(portModel);
		localPort.setFont(bigfont);

		jp.add(localPort, BorderLayout.EAST);
		
		serverTab.add(jp, BorderLayout.SOUTH);
	}
	

	public int getNumberOfPlayers() {
		return (int) numberOfPlayers.getValue();
	}

	public List<String> getHumanNames() {
		ArrayList<String> humanNames = new ArrayList<String>();
		for (JTextField n : nameBoxes){
			if (n.isEnabled() && n.isEditable()) humanNames.add(n.getText());
		}
		return humanNames;
	}

	public int getNetworkCount() {
		int count = 0;
		for (int i=1; i< MAXPLAYERS*3; i+=3){
			if (radioButtons.get(i).isSelected()){
				++count;
			}
		}
		return count;
	}

	public boolean isServerGame() {
		return tabs.getSelectedIndex() == 0;
	}

	public String getRemoteHost() {
		return remoteHost.getText();
	}

	public int getRemotePort() {
		return (int) remotePort.getValue();
	}

	public String getLocalHost() {
		return localHost.getText();
	}

	public int getLocalPort() {
		return (int) localPort.getValue();
	}

}
