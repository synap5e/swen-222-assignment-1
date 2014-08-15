package cluedo.view;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.json.JsonObject;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/**
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class CluedoFrame extends JFrame implements GameListener {

	private JMenuBar menu;
	private BoardCanvas canvas;

	private List<FrameListener> listeners;

	private Map<String, Image> cardImages;
	
	private CardListPanel hand;
	private CardListPanel cardDisplay;
	private DiceCanvas dice;
	
	private JTextArea log;
	
	private JButton suggestion;
	private JButton accusation;
	private JButton rollDice;
	private JButton endTurn;

	public CluedoFrame(Board board, JsonObject def){
		listeners = new ArrayList<FrameListener>();

		setTitle("Cluedo");
		setMinimumSize(new Dimension(950, 850));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));

		setJMenuBar(menu);

		//Load images
		loadImages();
		
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
		canvas = new BoardCanvas(this, board, def, cardImages);
		setConstraints(con, 1, 0, true, true);
		con.gridheight = 2;
		pane.add(canvas, con);
		
		//Setup the hand panel
		hand = new CardListPanel(cardImages);
		hand.setPreferredSize(new Dimension(200, 200));
		setConstraints(con, 1, 2, true, false);
		con.gridheight = 1;
		pane.add(hand, con);
		
		//Setup log
		
		log = new JTextArea();
		log.setLineWrap(true);
		log.setWrapStyleWord(true);
		log.setEditable(false);
		JScrollPane logBox = new JScrollPane(log, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logBox.setPreferredSize(new Dimension(250, 10));
		setConstraints(con, 2, 0, false, false);
		con.gridheight = 3;
		pane.add(logBox, con);
		
		setVisible(true);
	}
	
	private void loadImages(){
		cardImages = new HashMap<String, Image>();
		try {
			//Load card back
			cardImages.put("back", ImageIO.read(new File("./images/card_back.png")));
			
			//Load weapon pictures
			cardImages.put("Dagger", ImageIO.read(new File("./images/card_dagger.png")));
			cardImages.put("Revolver", ImageIO.read(new File("./images/card_revolver.png")));
			cardImages.put("Rope", ImageIO.read(new File("./images/card_rope.png")));
			cardImages.put("Spanner", ImageIO.read(new File("./images/card_spanner.png")));
			cardImages.put("Lead Piping", ImageIO.read(new File("./images/card_lead_piping.png")));
			cardImages.put("Candlestick", ImageIO.read(new File("./images/card_candlestick.png")));

			//Load character pictures
			cardImages.put("Colonel Mustard", ImageIO.read(new File("./images/card_colonel_mustard.png")));
			cardImages.put("Miss Scarlett", ImageIO.read(new File("./images/card_miss_scarlett.png")));
			cardImages.put("Mrs Peacock", ImageIO.read(new File("./images/card_mrs_peacock.png")));
			cardImages.put("Mrs White", ImageIO.read(new File("./images/card_mrs_white.png")));
			cardImages.put("Professor Plum", ImageIO.read(new File("./images/card_professor_plum.png")));
			cardImages.put("Rev. Green", ImageIO.read(new File("./images/card_rev_green.png")));

			//Load room pictures
			cardImages.put("Ballroom", ImageIO.read(new File("./images/card_ballroom.png")));
			cardImages.put("Billiard Room", ImageIO.read(new File("./images/card_billiard_room.png")));
			cardImages.put("Conservatory", ImageIO.read(new File("./images/card_conservatory.png")));
			cardImages.put("Dining Room", ImageIO.read(new File("./images/card_dining_room.png")));
			cardImages.put("Hall", ImageIO.read(new File("./images/card_hall.png")));
			cardImages.put("Kitchen", ImageIO.read(new File("./images/card_kitchen.png")));
			cardImages.put("Library", ImageIO.read(new File("./images/card_library.png")));
			cardImages.put("Lounge", ImageIO.read(new File("./images/card_lounge.png")));
			cardImages.put("Study", ImageIO.read(new File("./images/card_study.png")));
		} catch (IOException e) {
		}
	}
	
	private JPanel createButtonPanel(){
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBackground(new Color(212,196,173));
		buttonPanel.setPreferredSize(new Dimension(110, 200));
		GridBagConstraints con = createConstraints();
		
		suggestion = createButton(buttonPanel, "Suggest", 0, 0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onSuggest();
				}
			}
		}, con);
		accusation = createButton(buttonPanel, "Accuse", 0, 1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onAccuse();
				}
			}
		}, con);
		endTurn = createButton(buttonPanel, "End Turn", 0, 2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onEndTurn();
				}
			}
		}, con);
		rollDice = createButton(buttonPanel, "Roll Dice", 0, 3, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onRollDice();
				}
			}
		}, con);
		//Add Gap
		setConstraints(con, 0, 4, false, true);
		JLabel gap = new JLabel("");
		gap.setMinimumSize(new Dimension(110, 5));
		buttonPanel.add(gap, con);
		
		displayTurnButtons(false);
		suggestion.setVisible(false);
		displayRollDice(false);
		
		return buttonPanel;
	}
	
	public void setDisplayedCard(Card card){
		List<Card> cards = new ArrayList<Card>();
		cards.add(card);
		cardDisplay.setCards(cards);
		cardDisplay.repaint();
	}
	
	public void setHand(Hand hand){
		List<Card> cards = null;
		if (hand != null){
			cards = new ArrayList<Card>();
			for (Card c : hand){
				cards.add(c);
			}
		}
		this.hand.setCards(cards);
	}
	
	public Map<String, Image> getCardImages(){
		return cardImages;
	}

	public void displayTurnButtons(boolean visible){
		accusation.setVisible(visible);
		endTurn.setVisible(visible);
		repaint();
	}

	public void displaySuggestion(boolean visible){
		suggestion.setVisible(visible);
		repaint();
	}

	public void displayRollDice(boolean visible){
		rollDice.setVisible(visible);
		repaint();
	}

	public boolean addFrameListener(FrameListener listener){
		return listeners.add(listener);
	}

	public void onLocationSelect(Location loc){
		for (FrameListener l : listeners){
			l.onLocationSelect(loc);
		}
	}

	public void onTokenSelect(Token token){
		for (FrameListener l : listeners){
			l.onTokenSelect(token);
		}
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
		con.insets = new Insets(5, 0, 0, 0);
		pane.add(button, con);
		return button;
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
	private void setConstraints(GridBagConstraints con, int x, int y, boolean expandX, boolean expandY){
		con.gridy = y;
		con.gridx = x;
		con.insets = new Insets(0, 0, 0, 0);
		con.weightx = (expandX) ? 1 : 0;
		con.weighty = (expandY) ? 1 : 0;
	}

	/**
	 * Create the constraints with a predefined insets and set to fill both directions.
	 *
	 * @return The created constraints
	 */
	private GridBagConstraints createConstraints(){
		GridBagConstraints con = new GridBagConstraints();
		con.insets = new Insets(1, 2, 2, 1);
		con.fill = GridBagConstraints.BOTH;
		return con;
	}

	public BoardCanvas getCanvas(){
		return canvas;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType type) {
		log.append(String.format("> %s (%s) joined the game as %s\n", playerName, character.getName(), type == PlayerType.LocalHuman? "human" : "AI"));
	}

	public void onTurnBegin(String name, Character playersCharacter) {
		canvas.onTurnBegin(name, playersCharacter);
		log.append(String.format("> Its %s's (%s) turn\n", name, playersCharacter.getName()));
	}

	@Override
	public void onDiceRolled(int dice1, int dice2) {
		dice.showDice(dice1, dice2);
	}

	@Override
	public void onCharacterMove(Character character, Location destination) {
		canvas.repaint();
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion) {
		//TODO add room
		log.append(String.format("> %s suggested that %s killed Dr Black with a %s and no-one could disprove that\n",
				suggester.getName(), suggestion.getCharacter().getName(), suggestion.getWeapon().getName()));
	
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover) {
		//TODO add room
		log.append(String.format("> %s suggested that %s killed Dr Black with a %s but %s proved that could not be\n",
				suggester.getName(), suggestion.getCharacter().getName(), suggestion.getWeapon().getName(), disprover.getName()));
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		log.append(String.format("%s accused %s of killing Dr Black in the %s with a %s\n" +
				(correct ? "This was magically verified as true" : "They were wrong and got killed") + "\n",
				accuser.getName(), accusation.getCharacter().getName(), accusation.getRoom().getName(), accusation.getWeapon().getName()));
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
		canvas.repaint();
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
		canvas.setCurrentAction("Game Over");
		log.append(String.format("> %s (%s) won the game!\n", name, playersCharacter.getName()));
	}

	@Override
	public void waitingForNetworkPlayers(int i) {
		log.append(String.format("> Still waiting for %d network players\n", i));
	}

	@Override
	public void onLostGame(String name, Character playersCharacter) {
		log.append(String.format("> %s (%s) lost the game due to an incorrect accusation\n", name, playersCharacter.getName()));
	}


}
