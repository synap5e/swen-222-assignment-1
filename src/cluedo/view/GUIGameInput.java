package cluedo.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import cluedo.controller.interaction.GameInput;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;
import cluedo.view.CardListPanel.CardListener;

/**
 * Provides the GameMaster with input from GUI. 
 * The input is retrieved from the user by blocking until the user did the required action.
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 * 
 */
public class GUIGameInput implements GameInput, FrameListener, CardListener{

	/**
	 * Whether to wait for input
	 */
	private boolean wait = false;

	/**
	 * Whether to wait for the user to roll the dice
	 */
	private boolean waitForDiceRoll = false;
	
	/**
	 * Whether the user has chose to end their turn
	 */
	private boolean endingTurn = false;
	
	/**
	 * Whether the user has chose to accuse
	 */
	private boolean accusing = false;
	
	/**
	 * Whether the user has chose to make a suggestion
	 */
	private boolean suggesting = false;
	
	/**
	 * The main window of the GUI 
	 */
	private CluedoFrame frame;

	/**
	 * The currently selected location
	 */
	private Location selectedLocation = null;
	
	/**
	 * The currently selected token
	 */
	private Token selectedToken = null;
	
	/**
	 * The currently selected card
	 */
	private Card selectedCard = null;

	/**
	 * The window for configuring the game
	 */
	private GameConfig gameConfig;

	/**
	 * Create the GUI input
	 * 
	 * @param frame the frame that is the main window in the GUI
	 * @param gc the configuration window
	 */
	public GUIGameInput(CluedoFrame frame, GameConfig gc){
		this.frame = frame;
		this.gameConfig = gc;
		frame.addFrameListener(this);
	}

	@Override
	public int getNumberOfPlayers(int min, int max) {
		return gameConfig.getNumberOfPlayers();
	}

	@Override
	public List<String> getHumanNames() {
		return gameConfig.getHumanNames();
	}

	@Override
	public int getNetworkPlayerCount() {
		return gameConfig.getNetworkCount();
	}

	@Override
	public Character chooseCharacter(String playerName,
			List<Character> characters, List<Character> availableCharacters) {
		//Create the dialog box
		JDialog dialog = new JDialog(frame);
		dialog.setMinimumSize(new Dimension(200, 300));
		dialog.setLayout(new GridLayout(characters.size()+2, 1));

		//Add the message asking the player to pick a character
		dialog.add(new JLabel(playerName + ", who would you like to be?"));

		//Add all the characters as radio buttons
		ButtonGroup group = new ButtonGroup();
		for (Character ch : characters){
			JRadioButton rb = new JRadioButton(ch.getName());
		    rb.setActionCommand(ch.getName());
		    //Only enable and select characters who are available characters
		    rb.setEnabled(availableCharacters.contains(ch));
		    rb.setSelected(availableCharacters.contains(ch));
		    dialog.add(rb);
		    group.add(rb);
		}

		//Add the button to submit the chosen character
		JButton submit = new JButton("Done");
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wait = false;
			}
		});
		dialog.add(submit);
		
		//Display the dialog and wait until the button is pressed
		dialog.setVisible(true);
		wait = true;
		while (wait){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		dialog.dispose();
		
		//Find the character selected
		String name = group.getSelection().getActionCommand();
		for (Character ch : availableCharacters){
			if (ch.getName().equals(name)){
				//Return the selected character
				return ch;
			}
		}
		//A character must be selected so this code should never be reached
		assert false: "Code should not be reached";
		return null;
	}

	@Override
	public void startTurn(Hand h) {
		//Show the turn has started and allow the dice to be rolled
		frame.getCanvas().setCurrentAction("Roll the Dice");
		frame.displayRollDice(true);
		frame.setHand(h);
		
		//Wait for the user to roll the dice
		waitForDiceRoll = true;
		while (waitForDiceRoll){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		//Hide the option to roll the dice
		frame.displayRollDice(false);
		//Clear what the last user did
		suggesting = false;
		accusing = false;
		endingTurn = false;
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		//Show the user that should choose a destination
		frame.getCanvas().setCurrentAction("Choose Move");
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setPossibleLocations(possibleLocations);
		frame.getCanvas().repaint();
		
		//Wait for a valid destination to be chosen
		selectedLocation = null;
		while (selectedLocation == null || !possibleLocations.contains(selectedLocation)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		//Unselect the destination
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setPossibleLocations(null);
		
		//Show the buttons for making accusations and ending the players turn
		frame.displayTurnButtons(true);
		
		//If the destination is a room
		if (selectedLocation instanceof Room){
			//Allow a suggestion to be made
			frame.getCanvas().setCurrentAction("Make a Suggestion?");
			frame.displaySuggestion(true);
		} else {
			//Otherwise prepare for the user to possibly make an accusation
			frame.getCanvas().setCurrentAction("Make an Accusation?");
		}
		//Return the chosen destination
		return selectedLocation;
	}

	@Override
	public boolean hasSuggestion() {
		//Wait until the user has pressed any of the buttons
		while (!(endingTurn || suggesting || accusing)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		//The user can no longer make a suggestion
		frame.displaySuggestion(false);
		
		//Return whether the user wanted to make a suggestion
		return suggesting;
	}

	@Override
	public boolean hasAccusation() {
		//Wait until the user has either ended their turn or started an accusation
		while (!(endingTurn || accusing)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		//Hide the buttons
		frame.displayTurnButtons(false);
		
		//If ending the turn
		if (endingTurn){
			frame.getCanvas().setCurrentAction("Waiting for player");
		}
		//Return whether the user chose to accuse
		return accusing;
	}

	@Override
	public Weapon pickWeapon() {
		//Ask the user to pick a weapon
		frame.getCanvas().setCurrentAction("Pick the Weapon");
		frame.getCanvas().focusWeapons(true);
		
		//Wait until a weapon token is selected
		selectedToken = null;
		while (selectedToken == null || selectedToken instanceof Character){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().focusWeapons(false);
		
		//Return the weapon
		return (Weapon) selectedToken;
	}

	@Override
	public Character pickCharacter() {
		//Ask the user to pick a character
		frame.getCanvas().setCurrentAction("Pick the Murderer");
		frame.getCanvas().focusCharacters(true);
		frame.getCanvas().repaint();
		
		//Wait until a character token is selected
		selectedToken = null;
		while (selectedToken == null || selectedToken instanceof Weapon){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().focusCharacters(false);
		frame.getCanvas().setCurrentAction("Waiting for player");
		
		//Return the character
		return (Character) selectedToken;
	}

	@Override
	public Room pickRoom() {
		//Ask the user to pick a room
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setCurrentAction("Pick the Room");
		frame.getCanvas().focusRooms(true);
		frame.getCanvas().repaint();
		
		//Wait until a room was selected
		selectedLocation = null;
		while (selectedLocation == null || selectedLocation instanceof Tile){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().focusRooms(false);
		frame.getCanvas().setCurrentAction("Waiting for player");
		
		//Return the room
		return (Room) selectedLocation;
	}

	@Override
	public Card selectDisprovingCardToShow(Character character, Character suggester, List<Card> possibleShow) {
		//Create the dialog
		JDialog dialog = new JDialog(frame);
		dialog.setSize(400, 300);
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		dialog.setLayout(new GridBagLayout());
		
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.BOTH;
		
		//Add the message asking a player to disprove the suggestion
		setConstraints(con, 1, 0, 1, false, false);
		dialog.add(new JLabel(character.getName() + ", choose a card to disprove the suggestion"), con);
		
		//Add the card display for choosing the card
		setConstraints(con, 0, 1, 3, true, true);
		CardListPanel disprovePanel = new CardListPanel(frame.getCardImages());
		disprovePanel.setCards(possibleShow);
		disprovePanel.addListener(this);
		dialog.add(disprovePanel, con);
		
		//Show the dialog
		dialog.setVisible(true);
		
		//Wait until the user selects a card
		selectedCard = null;
		while (selectedCard == null){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		dialog.dispose();
		
		return selectedCard;
	}

	@Override
	public void suggestionDisproved(Character characterDisproved, Card disprovingCard) {
		//Show the suggestion was disproved
		frame.getCanvas().setCurrentAction("Suggestion Disproved");

		//Create a dialog box
		JDialog dialog = new JDialog(frame);
		dialog.setSize(250, 300);
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.anchor = GridBagConstraints.CENTER;
		
		//Add who disproved the suggestion to dialog
		setConstraints(con, 1, 0, 1, false, false);
		dialog.add(new JLabel(characterDisproved.getName() + " disproved your suggestion"), con);
		
		//Add the card display to show which card disproved the suggestion
		setConstraints(con, 0, 1, 3, true, true);
		CardListPanel disprovePanel = new CardListPanel(frame.getCardImages());
		List<Card> card = new ArrayList<Card>();
		card.add(disprovingCard);
		disprovePanel.setCards(card);
		disprovePanel.addListener(this);
		con.fill = GridBagConstraints.BOTH;
		dialog.add(disprovePanel, con);
		
		//Add the button to close the box
		con.fill = GridBagConstraints.NONE;
		JButton close = new JButton("Return");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wait = false;
			}
		});
		setConstraints(con, 1, 3, 1, false, false);
		dialog.add(close, con);
		
		//Show the dialog
		dialog.setVisible(true);
		
		//Wait for the user to close the dialog before continuing
		wait = true;
		while (wait){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		dialog.dispose();
	}

	@Override
	public String getSingleName() {
		//Get the name from the config
		return gameConfig.getSingleName();
	}

	@Override
	public void onLocationSelect(Location loc) {
		//Remember the location selected in case it is being waited for
		selectedLocation = loc;
	}

	@Override
	public void onTokenSelect(Token token) {
		//Remember the token selected in case it is being waited for
		selectedToken = token;
	}

	@Override
	public void onRollDice() {
		//Mark the dice having been rolled
		waitForDiceRoll = false;
	}

	@Override
	public void onSuggest() {
		//Mark the fact the user chose to make a suggestion
		suggesting = true;
	}

	@Override
	public void onAccuse() {
		//Mark the fact the user chose to make an accusation
		accusing = true;
	}

	@Override
	public void onEndTurn() {
		//Mark the fact the user chose to end their turn
		endingTurn = true;
	}

	@Override
	public void onCardSelected(Card c) {
		//Remember the card selected in case it is being waited for
		selectedCard = c;
	}
	
	/**
	 * Sets a given constraints with the given values.
	 *
	 * @param con - the constraints to set
	 * @param row - the row
	 * @param col - the column
	 * @param width - how many positions in the grid to span horizontally
	 * @param expandX - whether components should try fill as much horizontal space
	 * @param expandY - whether components should try fill as much vertical space
	 */
	private void setConstraints(GridBagConstraints con, int x, int y, int width, boolean expandX, boolean expandY){
		con.gridy = y;
		con.gridx = x;
		con.gridwidth = width;
		con.insets = new Insets(0, 0, 0, 0);
		con.weightx = (expandX) ? 1 : 0;
		con.weighty = (expandY) ? 1 : 0;
	}

}
