package cluedo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/**
 * CluedoFrame is the main window in the GUI. 
 * It contains displays for the current hand and the board, 
 * while also providing buttons to make turn related decisions (for example, making a suggestion). 
 * It also acts a subject, alerting listeners of different events related to GUI interaction.
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class CluedoFrame extends JFrame implements GameListener {

	private static final long serialVersionUID = 1L;
	
	private JMenuBar menu; //TODO: Document  and use
	
	/**
	 * The board display
	 */
	private BoardCanvas canvas;

	/**
	 * The list of listeners listening for events from the frame
	 */
	private List<FrameListener> listeners;

	/**
	 * The images for all the cards
	 */
	private Map<String, Image> cardImages;
	
	/**
	 * The images for the tokens
	 */
	private Map<String, Image> tokenImages;
	
	/**
	 * The display for the current hand
	 */
	private CardListPanel hand;
	
	/**
	 * The display that shows the card last hovered over in the board display
	 */
	private CardListPanel cardDisplay;
	
	/**
	 * The display showing the last dice roll
	 */
	private DiceCanvas dice;
	
	/**
	 * The shared log that shows when things happened
	 */
	private JTextArea log;
	
	/**
	 * The button for making a suggestion
	 */
	private JButton suggestion;
	
	/**
	 * The button for making an accusation
	 */
	private JButton accusation;
	
	/**
	 * The button for rolling the dice
	 */
	private JButton rollDice;
	
	/**
	 * The button to end the turn
	 */
	private JButton endTurn;

	/**
	 * Creates the frame representing the board.
	 * 
	 * @param board - the board the frame's interaction is based around
	 * @param def - the JSON definition which includes how parts of the model should be represented
	 */
	public CluedoFrame(Board board, JsonObject def){
		listeners = new ArrayList<FrameListener>();

		//Setup the frame itself
		setTitle("Cluedo");
		setMinimumSize(new Dimension(950, 850));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Setup the menu bar
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));
		setJMenuBar(menu);

		//Load images
		loadImages(def);
		
		//Get the pane
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints con = createConstraints();

		//Setup card display
		cardDisplay = new CardListPanel(cardImages);
		setConstraints(con, 0, 0, false, false);
		cardDisplay.setPreferredSize(new Dimension(110, 175));
		pane.add(cardDisplay, con);
		
		//Setup the dice canvas
		dice = new DiceCanvas();
		setConstraints(con, 0, 1, false, true);
		pane.add(dice, con);

		//Setup the button panel
		JPanel buttonPanel = createButtonPanel();
		setConstraints(con, 0, 2, false, false);
		pane.add(buttonPanel, con);
		
		//Setup the board canvas
		canvas = new BoardCanvas(this, board, def, tokenImages);
		setConstraints(con, 1, 0, true, true);
		con.gridheight = 2;
		pane.add(canvas, con);
		
		//Setup the hand panel
		hand = new CardListPanel(cardImages);
		hand.setPreferredSize(new Dimension(200, 200));
		setConstraints(con, 1, 2, true, false);
		con.gridheight = 1;
		pane.add(hand, con);
		
		//Setup log inside of a scroll pane
		log = new JTextArea();
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		log.setEditable(false);
		JScrollPane logBox = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logBox.setPreferredSize(new Dimension(250, 10));
		setConstraints(con, 2, 0, false, false);
		con.gridheight = 3;
		pane.add(logBox, con);
		
		//Finish off by making the frame visible
		setVisible(true);
	}
	
	/**
	 * Loads the card and token images from a JSON definition.
	 * 
	 * @param def - the JSON defining where the images are
	 */
	private void loadImages(JsonObject def){
		cardImages = new HashMap<String, Image>();
		tokenImages = new HashMap<String, Image>();
		try {
			//Load card back
			cardImages.put("back", ImageIO.read(new File(((JsonString) def.get("card back")).value())));

			//Load Weapon images (Tokens and Cards)
			JsonObject weapon = (JsonObject) def.get("weapons");
			for (String key : weapon.keys()){
				cardImages.put(key, ImageIO.read(
						new File(((JsonString)((JsonObject) weapon.get(key)).get("card")).value())));
				tokenImages.put(key, ImageIO.read(
						new File(((JsonString)((JsonObject) weapon.get(key)).get("token")).value())));
			}
			
			//Load Character images (Cards only)
			JsonObject character = (JsonObject) def.get("characters");
			for (String key : character.keys()){
				cardImages.put(key, ImageIO.read(
						new File(((JsonString)((JsonObject) character.get(key)).get("card")).value())));
			}
			
			//Load Room images (Cards only)
			JsonObject rooms = (JsonObject) def.get("rooms");
			for (String key : rooms.keys()){
				cardImages.put(key, ImageIO.read(
						new File(((JsonString)((JsonObject) rooms.get(key)).get("card")).value())));
			}
		} catch (IOException e) {
		}
	}
	
	/**
	 * Create the panel that holds the buttons for rolling the dice, 
	 * starting suggestions or accusations, and ending the current players turn
	 * 
	 * @return the panel created
	 */
	private JPanel createButtonPanel(){
		//Setup the panel
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBackground(new Color(212,196,173));
		buttonPanel.setPreferredSize(new Dimension(110, 200));
		GridBagConstraints con = createConstraints();
		con.insets = new Insets(5, 0, 0, 0);
		
		//Create the button for starting suggestions
		suggestion = createButton(buttonPanel, "Suggest", 0, 0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onSuggest();
				}
			}
		}, con);
		//Create the button for making accusations
		accusation = createButton(buttonPanel, "Accuse", 0, 1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onAccuse();
				}
			}
		}, con);
		//Create the button for ending the current player's turn
		endTurn = createButton(buttonPanel, "End Turn", 0, 2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onEndTurn();
				}
			}
		}, con);
		//Create the button for rolling the dice
		rollDice = createButton(buttonPanel, "Roll Dice", 0, 3, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onRollDice();
				}
			}
		}, con);
		//Add spacing to make sure the panel keeps it's size
		setConstraints(con, 0, 4, false, true);
		JLabel spacing = new JLabel("");
		spacing.setMinimumSize(new Dimension(110, 5));
		buttonPanel.add(spacing, con);
		
		//By default, hide the buttons
		displayTurnButtons(false);
		suggestion.setVisible(false);
		displayRollDice(false);
		
		return buttonPanel;
	}

	/**
	 * Creates a button and adds it to the given pane.
	 *
	 * @param pane - the pane to add the button to
	 * @param text - the text of the button
	 * @param x - the column to add at
	 * @param y - the row to add at
	 * @param listener - the listener for the button
	 * @param con - the constraints to use
	 *
	 * @return the created button
	 */
	private JButton createButton(Container pane, String text, int x, int y, ActionListener listener, GridBagConstraints con){
		JButton button = new JButton(text);
		button.setOpaque(false);
		button.addActionListener(listener);
		setConstraints(con, x, y, false, false);
		pane.add(button, con);
		return button;
	}
	
	/**
	 * Create the constraints with a predefined insets and set to fill both directions.
	 *
	 * @return the created constraints
	 */
	private GridBagConstraints createConstraints(){
		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(0, 0, 0, 0);
		con.fill = GridBagConstraints.BOTH;
		return con;
	}

	/**
	 * Sets a given constraints with the given values.
	 *
	 * @param con - the constraints to set
	 * @param row - the row
	 * @param col - the column
	 * @param expandX - whether components should try fill as much horizontal space
	 * @param expandY - whether components should try fill as much vertical space
	 */
	private void setConstraints(GridBagConstraints con, int x, int y, boolean expandX, boolean expandY){
		con.gridy = y;
		con.gridx = x;
		con.weightx = (expandX) ? 1 : 0;
		con.weighty = (expandY) ? 1 : 0;
	}

	/**
	 * Set the card to be displayed
	 * @param card the card to be displayed
	 */
	public void setDisplayedCard(Card card){
		List<Card> cards = new ArrayList<Card>();
		cards.add(card);
		cardDisplay.setCards(cards);
		cardDisplay.repaint();
	}
	
	/**
	 * Set the hand of the current player
	 * @param hand the hand to display
	 */
	public void setHand(Hand hand){
		//Transform the hand into a list of cards
		List<Card> cards = null;
		if (hand != null){
			cards = new ArrayList<Card>();
			for (Card c : hand){
				cards.add(c);
			}
		}
		this.hand.setCards(cards);
	}
	
	/**
	 * Get the map of images for the cards
	 * 
	 * @return the card images
	 */
	public Map<String, Image> getCardImages(){
		return cardImages;
	}

	/**
	 * Set whether to display the buttons to accuse or end turn.
	 * 
	 * @param visible whether the buttons should be displayed
	 */
	public void displayTurnButtons(boolean visible){
		accusation.setVisible(visible);
		endTurn.setVisible(visible);
		repaint();
	}

	/**
	 * Set whether to display the button to start a suggestion.
	 * 
	 * @param visible whether the button should be displayed
	 */
	public void displaySuggestion(boolean visible){
		suggestion.setVisible(visible);
		repaint();
	}

	/**
	 * Set whether to display the button to roll the dice
	 * 
	 * @param visible whether the button should be displayed
	 */
	public void displayRollDice(boolean visible){
		rollDice.setVisible(visible);
		repaint();
	}

	/**
	 * Add a listener for frame events
	 * 
	 * @param listener the listener
	 * @return whether the listener was successfully added
	 */
	public boolean addFrameListener(FrameListener listener){
		return listeners.add(listener);
	}

	/**
	 * Alerts the frame listeners that a location has been selected.
	 * 
	 * @param loc the location selected
	 */
	public void onLocationSelect(Location loc){
		for (FrameListener l : listeners){
			l.onLocationSelect(loc);
		}
	}

	/**
	 * Alerts the frame listeners that a token has been selected.
	 * 
	 * @param token the token selected
	 */
	public void onTokenSelect(Token token){
		for (FrameListener l : listeners){
			l.onTokenSelect(token);
		}
	}
	
	/**
	 * Returns the board canvas
	 * 
	 * @return the board canvas
	 */
	public BoardCanvas getCanvas(){
		return canvas;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType type) {
		//Log that a player joined the game
		log.append(String.format("> %s (%s) joined the game as %s\n", playerName, character.getName(), type == PlayerType.LocalAI? "AI" : "human"));
	}

	@Override
	public void onTurnBegin(String name, Character playersCharacter) {
		//Set and log that a new player's turn has started
		canvas.setCurrentPlayer(playersCharacter);
		log.append(String.format("> Its %s's (%s) turn\n", name, playersCharacter.getName()));
	}

	@Override
	public void onDiceRolled(int dice1, int dice2) {
		dice.showDice(dice1, dice2);
	}

	@Override
	public void onCharacterMove(Character character, Location destination) {
		//Makes sure when a character is moved that the canvas displays it
		canvas.repaint();
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion, Room room) {
		//Display and log that a suggestion was undisputed
		JOptionPane.showMessageDialog(this, suggester.getName() + "'s suggestion was undisputed");
		
		canvas.setCurrentAction("Suggestion Succeeded");
		log.append(String.format("> %s suggested that %s killed Dr Black in the %s with a %s and no-one could disprove that\n",
				suggester.getName(), suggestion.getCharacter().getName(), room.getName(), suggestion.getWeapon().getName()));
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Room room, Character disprover) {
		//Display and log that a suggestion was disproved
		JOptionPane.showMessageDialog(this, suggester.getName() + "'s suggestion was disproved by " + disprover.getName());
		
		canvas.setCurrentAction("Suggestion Disproved");
		log.append(String.format("> %s suggested that %s killed Dr Black in the %s with a %s but %s proved that could not be\n",
				suggester.getName(), suggestion.getCharacter().getName(), room.getName(), suggestion.getWeapon().getName(), disprover.getName()));
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		//Display and log that an accusation was made and whether it was correct
		String message = accuser.getName() + " made an accusation which proved to be " + correct;
		List<Card> cards = Arrays.asList(accusation.getWeapon(), accusation.getCharacter(), accusation.getRoom());
		showCards(message, cards);
		
		log.append(String.format("> %s accused %s of killing Dr Black in the %s with a %s\n" +
				(correct ? "This was magically verified as true" : "They were wrong and got killed") + "\n",
				accuser.getName(), accusation.getCharacter().getName(), accusation.getRoom().getName(), accusation.getWeapon().getName()));
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
		//Makes sure when a weapon is moved that the canvas displays it
		canvas.repaint();
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
		//Display and log that the game was won
		JOptionPane.showMessageDialog(this, name + " (" + playersCharacter.getName() + ") won the game");
		canvas.setCurrentAction("Game Over");
		log.append(String.format("> %s (%s) won the game!\n", name, playersCharacter.getName()));
	}

	@Override
	public void waitingForNetworkPlayers(int i) {
		//Log that the game is still waiting
		log.append(String.format("> Still waiting for %d network players\n", i));
	}

	@Override
	public void onLostGame(String name, Character playersCharacter) {
		//Display and log the given character lost the game
		JOptionPane.showMessageDialog(this, name + " (" + playersCharacter.getName() + ") lost the game");
		log.append(String.format("> %s (%s) lost the game due to an incorrect accusation\n", name, playersCharacter.getName()));
	}

	
	@Override
	public void onSuggestion(String suggesterPlayerName, Character suggester, Suggestion suggestion, Room room) {
		//Display the suggestion, regardless of whether it will be disproved
		String message = suggesterPlayerName + " (" + suggester.getName() + ") made a suggestion";
		List<Card> cards = Arrays.asList(suggestion.getWeapon(), suggestion.getCharacter(), room);
		
		showCards(message, cards);
		
	}

	/**
	 * Opens a dialog box, displaying the given cards
	 * 
	 * @param message the message to accompany the cards
	 * @param cards the cards to display
	 */
	private void showCards(String message, List<Card> cards) {
		//Setup the dialog box
		final JDialog alertDialog = new JDialog(this);
		alertDialog.setSize(500, 300);
		alertDialog.setResizable(false);
		alertDialog.setAlwaysOnTop(true);
		alertDialog.setLayout(new BorderLayout());
		
		//Add the message
		alertDialog.add(new JLabel(message), BorderLayout.NORTH);
		
		//Add the card display
		CardListPanel cardsPanel = new CardListPanel(getCardImages());
		cardsPanel.setCards(cards);
		alertDialog.add(cardsPanel, BorderLayout.CENTER);
		
		//Add the button to close
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				alertDialog.setVisible(false);
			}
		});
		alertDialog.add(ok, BorderLayout.SOUTH);
		
		//Wait for OK to be pressed
		alertDialog.setVisible(true);
		while (alertDialog.isVisible()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		alertDialog.dispose();
	}


}
