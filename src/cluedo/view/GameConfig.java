package cluedo.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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

/**
 * GameConfig allows the user to configure the game before it is started.
 * 
 * @author Simon Pinfold, James Greenwood-Thessman
 *
 */
public class GameConfig extends JFrame{

	private static final long serialVersionUID = 1L;

	/**
	 * The minimum number of players in a game
	 */
	private static final int MINPLAYERS = 2;
	
	/**
	 * The maximum number of players in a game
	 */
	private static final int MAXPLAYERS = 6;
	
	/**
	 * The default port for servers/clients
	 */
	private static final int DEFAULT_PORT = 5362;

	/**
	 * The listener for when configuration is complete
	 */
	private ConfigListener configListener;
	
	/**
	 * The list of name inputs
	 */
	private ArrayList<JTextField> nameBoxes;
	
	/**
	 * The list of radio buttons for choosing whether to be local, remote or AI player
	 */
	private ArrayList<JRadioButton> radioButtons;

	/**
	 * The spinner to choose the number of players
	 */
	private JSpinner numberOfPlayers;
	
	/**
	 * The number of players who are playing over the network
	 */
	private int networkCount;

	/**
	 * The tabs for client and server setup
	 */
	private JTabbedPane tabs;
	
	/**
	 * The input for entering the remote host
	 */
	private JTextField remoteHost;

	/**
	 * The input for selecting the remote port
	 */
	private JSpinner remotePort;

	/**
	 * The input for entering the local host
	 */
	private JTextField localHost;

	/**
	 * The input for selecting the local port
	 */
	private JSpinner localPort;

	/**
	 * The input for entering the client's player's name
	 */
	private JTextField singleName;
	
	/**
	 * Listener for validating inputs when state changes
	 */
	private ChangeListener reval = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			revalidate_inputs();
		}
	};
	
	/**
	 * Create the window for configuring the game
	 */
	public GameConfig() {
		super();

		//Setup the frame
		setTitle("Create Game");
		setMinimumSize(new Dimension(600, 380));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setLayout(new BorderLayout());
		
		this.tabs = new JTabbedPane();
		
		//Setup the server tab
		JPanel serverTab = new JPanel(new BorderLayout(10, 20));
		createPlayerCountSpinner(serverTab, MINPLAYERS, MINPLAYERS, MAXPLAYERS);
		createPlayerList(serverTab, MINPLAYERS, MAXPLAYERS);
		createServerHostBox(serverTab, "0.0.0.0", DEFAULT_PORT);
		
		//Setup the client tab
		JPanel clientTab = new JPanel(new GridLayout(7, 1));
		createNameBox(clientTab);
		createHostBox(clientTab, "127.0.0.1", DEFAULT_PORT);
		
		//Add the tabs
		tabs.add("Run as server", serverTab);
		tabs.add("Run as client", clientTab);
		add(tabs, BorderLayout.CENTER);
		
		createReadyButton();
		
		revalidate_inputs();
		
		//Display the window
		setVisible(true);
	}

	/**
	 * Enables and disables inputs depending on other settings
	 */
	private void revalidate_inputs(){
		//Only enable radio buttons and name boxes for the current number of players 
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
		//Only enable choosing local server settings if there are networked players
		localHost.setEnabled(getNetworkCount() > 0);
		localPort.setEnabled(getNetworkCount() > 0);
	}
	
	/**
	 * Create the spinner for choosing how many players
	 * 
	 * @param serverTab the configuration tab for the server
	 * @param def initial value of the spinner
	 * @param min the minimum value of the spinner
	 * @param max the maximum value of the spinner
	 */
	private void createPlayerCountSpinner(JPanel serverTab, final int def, final int min, final int max) {
		JPanel jp = new JPanel(new BorderLayout());

		//Add spinner
		SpinnerNumberModel numberOfPlayersModel = new SpinnerNumberModel(min, min, max, 1);
		this.numberOfPlayers = new JSpinner(numberOfPlayersModel);
		jp.add(numberOfPlayers, BorderLayout.EAST);
		numberOfPlayers.setValue(def);
		
		jp.add(new JLabel("How many players?"), BorderLayout.WEST);

		//Update other inputs depending on how many players
		numberOfPlayers.addChangeListener(reval);

		//Add the panel
		serverTab.add(jp, BorderLayout.NORTH);
	}
	
	/**
	 * Creates the player list
	 * 
	 * @param serverTab the configuration tab for servers
	 * @param def the initial number of players
	 * @param max the maximum number of players
	 */
	private void createPlayerList(JPanel serverTab, int def, int max) {
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(max+1, 2));

		nameBoxes = new ArrayList<JTextField>();
		radioButtons = new ArrayList<JRadioButton>();

		//For each possible player
		for (int i = 0; i < max; ++i){
			//Add the input for choosing the player's name
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

			//Add the human option
			JRadioButton human = new JRadioButton("Human");
			group.add(human);
			sjp.add(human);
			radioButtons.add(human);
			human.setSelected(true);

			//Add the network option
			JRadioButton network = new JRadioButton("Network");
			group.add(network);
			sjp.add(network);
			radioButtons.add(network);
			
			//Add the AI option
			JRadioButton ai = new JRadioButton("AI");
			group.add(ai);
			sjp.add(ai);
			radioButtons.add(ai);
			
			//Make sure the inputs take into account any changes in which option is chosen
			human.addChangeListener(reval);
			network.addChangeListener(reval);
			ai.addChangeListener(reval);
			
			jp.add(sjp);
		}

		//Add a gap before adding it to the tab
		jp.add(new JLabel(""));
		serverTab.add(jp, BorderLayout.CENTER);
	}
	
	/**
	 * Creates the button that finalises the configuration
	 */
	private void createReadyButton() {
		//Create the button
		JButton submit = new JButton("Start");
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Alert configuration is finished and dispose the window
				configListener.onConfigured();
				dispose();
			}
		});
		//Add the button
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jp.add(submit);
		add(jp, BorderLayout.SOUTH);
	}
	
	/**
	 * Create box for choosing the IP and port to connect to as the client
	 * 
	 * @param clientTab the tab for configuring the client
	 * @param def the default IP address
	 * @param defport the default port
	 */
	private void createHostBox(JPanel clientTab, String def, int defport) {
		//Create the panel
		JPanel jp = new JPanel(new BorderLayout());
		
		//Create the field for entering the IP
		this.remoteHost = new JTextField();
		Font bigfont = new Font(remoteHost.getFont().getName(), remoteHost.getFont().getStyle(), remoteHost.getFont().getSize()*2);
		remoteHost.setFont(bigfont);
		remoteHost.setText(def);
		jp.add(remoteHost, BorderLayout.CENTER);
		
		//Create the spinner for choosing the port
		SpinnerNumberModel portModel = new SpinnerNumberModel(defport, 1, 0xffff, 1);
		this.remotePort = new JSpinner(portModel);
		remotePort.setFont(bigfont);
		jp.add(remotePort, BorderLayout.EAST);
		
		//Add some spacing
		clientTab.add(new JPanel());
		clientTab.add(new JPanel());
		
		clientTab.add(new JLabel("Remote host"));
		clientTab.add(jp);
	}
	
	/**
	 * Create the box for choosing the IP address and port for hosting the local server
	 * 
	 * @param serverTab the tab for configuring the server
	 * @param def the default host IP address
	 * @param defport the default port
	 */
	private void createServerHostBox(JPanel serverTab, String def, int defport) {
		//Create the panel
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel("Bind address"), BorderLayout.NORTH);
		
		//Create the field for entering the IP
		this.localHost = new JTextField();
		Font bigfont = new Font(localHost.getFont().getName(), localHost.getFont().getStyle(), localHost.getFont().getSize()*2);
		localHost.setFont(bigfont);
		localHost.setText(def);
		jp.add(localHost, BorderLayout.CENTER);
		
		//Create the spinner for choosing the port
		SpinnerNumberModel portModel = new SpinnerNumberModel(defport, 1, 0xffff, 1);
		this.localPort = new JSpinner(portModel);
		localPort.setFont(bigfont);
		jp.add(localPort, BorderLayout.EAST);
		
		serverTab.add(jp, BorderLayout.SOUTH);
	}
	
	/**
	 * Create and add the name input for clients
	 * 
	 * @param clientTab the tab for configuring clients
	 */
	private void createNameBox(JPanel clientTab) {
		singleName = new JTextField();
		
		clientTab.add(new JLabel("Name"));
		clientTab.add(singleName);
	}
	
	/**
	 * Sets the listener for when the configuration is complete
	 * @param l the listener
	 */
	public void setConfigListener(ConfigListener l) {
		this.configListener = l;
	}

	/**
	 * Get the number of players including local, remote and AI players
	 * 
	 * @return the number of players
	 */
	public int getNumberOfPlayers() {
		return (int) numberOfPlayers.getValue();
	}

	/**
	 * Get the names of all the locally hosted human players
	 * 
	 * @return the names of local human players
	 */
	public List<String> getHumanNames() {
		ArrayList<String> humanNames = new ArrayList<String>();
		for (JTextField n : nameBoxes){
			if (n.isEnabled() && n.isEditable()) humanNames.add(n.getText());
		}
		return humanNames;
	}

	/**
	 * Get the number of network players
	 * 
	 * @return the number of network players
	 */
	public int getNetworkCount() {
		int count = 0;
		for (int i=1; i< MAXPLAYERS*3; i+=3){
			if (radioButtons.get(i).isSelected()){
				++count;
			}
		}
		return count;
	}

	/**
	 * Whether the game will be the hosted locally
	 * 
	 * @return whether the game is a server
	 */
	public boolean isServerGame() {
		return tabs.getSelectedIndex() == 0;
	}

	/**
	 * Get the remote host
	 * 
	 * @return the remote host
	 */
	public String getRemoteHost() {
		return remoteHost.getText();
	}

	/**
	 * Get the remote port number
	 * 
	 * @return the remote port
	 */
	public int getRemotePort() {
		return (int) remotePort.getValue();
	}

	/**
	 * Get the local host
	 * 
	 * @return the local host
	 */
	public String getLocalHost() {
		return localHost.getText();
	}

	/**
	 * Get the local port number
	 * 
	 * @return the local port
	 */
	public int getLocalPort() {
		return (int) localPort.getValue();
	}

	/**
	 * Get the name the client chose
	 * @return the name
	 */
	public String getSingleName() {
		return singleName.getText();
	}

}
