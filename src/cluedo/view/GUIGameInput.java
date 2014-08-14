package cluedo.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class GUIGameInput implements GameInput, FrameListener, CardListener{

	private boolean wait = false;

	private boolean waitForDiceRoll = false;
	private boolean endingTurn = false;
	private boolean accusing = false;
	private boolean suggesting = false;
	private CluedoFrame frame;

	private Location selectedLocation = null;
	private Token selectedToken = null;
	private Card selectedCard = null;

	private GameConfig gameConfig;

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
		JDialog dialog = new JDialog(frame);
		dialog.setMinimumSize(new Dimension(200, 300));
		dialog.setLayout(new GridLayout(characters.size()+2, 1));

		dialog.add(new JLabel(playerName + ", who would you like to be?"));



		ButtonGroup group = new ButtonGroup();
		for (Character ch : characters){
			JRadioButton rb = new JRadioButton(ch.getName());
		    rb.setActionCommand(ch.getName());
		    rb.setEnabled(availableCharacters.contains(ch));
		    rb.setSelected(availableCharacters.contains(ch));
		    dialog.add(rb);
		    group.add(rb);
		}

		wait = true;

		JButton submit = new JButton("Done");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wait = false;
			}
		});

		dialog.add(submit);
		dialog.setVisible(true);

		while (wait){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		dialog.dispose();
		String name = group.getSelection().getActionCommand();
		for (Character ch : availableCharacters){
			if (ch.getName().equals(name)){

				return ch;
			}
		}
		assert false: "Code should not be reached";
		return null;
	}

	@Override
	public void startTurn(Hand h) {
		frame.getCanvas().setCurrentAction("Roll the Dice");
		frame.displayRollDice(true);
		frame.setHand(h);
		waitForDiceRoll = true;
		while (waitForDiceRoll){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.displayRollDice(false);
		suggesting = false;
		accusing = false;
		endingTurn = false;
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		wait = true;
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setPossibleLocations(possibleLocations);
		frame.getCanvas().repaint();
		selectedLocation = null;
		frame.getCanvas().setCurrentAction("Choose Move");
		while (selectedLocation == null || !possibleLocations.contains(selectedLocation)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setPossibleLocations(null);
		frame.displayTurnButtons(true);
		if (selectedLocation instanceof Room){
			frame.getCanvas().setCurrentAction("Make a Suggestion?");
			frame.displaySuggestion(true);
		} else {
			frame.getCanvas().setCurrentAction("Make an Accusation?");
		}
		return selectedLocation;
	}

	@Override
	public boolean hasSuggestion() {
		while (!(endingTurn || suggesting || accusing)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.displaySuggestion(false);
		return suggesting;
	}

	@Override
	public boolean hasAccusation() {
		while (!(endingTurn || accusing)){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.displayTurnButtons(false);
		return accusing;
	}

	@Override
	public Weapon pickWeapon() {
		frame.getCanvas().setCurrentAction("Pick the Weapon");
		frame.getCanvas().focusWeapons(true);
		selectedToken = null;

		while (selectedToken == null || selectedToken instanceof Character){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().focusWeapons(false);
		return (Weapon) selectedToken;
	}

	@Override
	public Character pickCharacter() {
		selectedToken = null;
		frame.getCanvas().setCurrentAction("Pick the Murderer");
		frame.getCanvas().focusCharacters(true);
		while (selectedToken == null || selectedToken instanceof Weapon){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().setCurrentAction("Suggestion Succeeded");
		frame.getCanvas().focusCharacters(false);
		return (Character) selectedToken;
	}

	@Override
	public Room pickRoom() {
		frame.getCanvas().unselectLocation();
		frame.getCanvas().repaint();
		selectedLocation = null;
		frame.getCanvas().setCurrentAction("Pick the Room");
		frame.getCanvas().focusRooms(true);
		while (selectedLocation == null || selectedLocation instanceof Tile){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		frame.getCanvas().focusRooms(false);
		return (Room) selectedLocation;
	}

	@Override
	public Card selectDisprovingCardToShow(Character character, Character suggester, List<Card> possibleShow) {
		JDialog dialog = new JDialog(frame);
		dialog.setSize(400, 300);
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		
		dialog.setLayout(new GridLayout(2, 1));
		dialog.setLayout(new GridBagLayout());
		GridBagConstraints con = new GridBagConstraints();
		con.fill = GridBagConstraints.BOTH;
		setConstraints(con, 1, 0, 1, false, false);
		dialog.add(new JLabel(character.getName() + ", choose a card to disprove the suggestion"), con);
		setConstraints(con, 0, 1, 3, true, true);
		CardListPanel disprovePanel = new CardListPanel(frame.getCardImages());
		disprovePanel.setCards(possibleShow);
		disprovePanel.addListener(this);
		dialog.add(disprovePanel, con);
		dialog.setVisible(true);
		
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
		frame.getCanvas().setCurrentAction("Suggestion Disproved");
		// TODO: Show card to the user

		System.out.printf("Psst [message only shown to current player]. %s disproved your suggestion by showing they hold %s\n", characterDisproved.getName(), disprovingCard.getName());

	}

	@Override
	public String getSingleName() {
		return gameConfig.getSingleName();
	}

	@Override
	public void onLocationSelect(Location loc) {
		selectedLocation = loc;
	}

	@Override
	public void onTokenSelect(Token token) {
		selectedToken = token;
	}

	@Override
	public void onNumberOfPlayers(int num) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPlayerSelection(List<String> names, int numberAI, int numberNetwork) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSinglePlayerName(String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRollDice() {
		waitForDiceRoll = false;
	}

	@Override
	public void onSuggest() {
		suggesting = true;
	}

	@Override
	public void onAccuse() {
		accusing = true;
	}

	@Override
	public void onEndTurn() {
		endingTurn = true;
	}

	@Override
	public void onCardSelected(Card c) {
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
