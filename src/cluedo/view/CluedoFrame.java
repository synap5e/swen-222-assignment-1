package cluedo.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import util.json.JsonObject;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Character;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Suggestion;

/**
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class CluedoFrame extends JFrame implements GameListener {

	private JMenuBar menu;
	private Canvas canvas;

	private List<FrameListener> listeners;

	private JButton suggestion;
	private JButton accusation;
	private JButton rollDice;
	private JButton endTurn;

	public CluedoFrame(Board board, JsonObject def){
		listeners = new ArrayList<FrameListener>();

		setTitle("Cluedo");
		setMinimumSize(new Dimension(600, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));

		setJMenuBar(menu);

		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints con = createConstraints();

		this.canvas = new Canvas(this, board, def);

		//Add Gap
		setConstraints(con, 1, 0, 1, false, true);
		pane.add(new JLabel(""), con);
		setConstraints(con, 1, 3, 1, true, false);
		pane.add(new JLabel(""), con);

		suggestion = createButton(pane, "Suggest", 0, 1, 1, 2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onSuggest();
				}
			}
		}, con);
		accusation = createButton(pane, "Accuse", 0, 2, 1, 2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onAccuse();
				}
			}
		}, con);
		endTurn = createButton(pane, "End Turn", 0, 3, 1, 20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onEndTurn();
				}
			}
		}, con);
		rollDice = createButton(pane, "Roll Dice", 0, 4, 1, 20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (FrameListener l : listeners){
					l.onRollDice();
				}
			}
		}, con);
		displayTurnButtons(false);
		suggestion.setVisible(false);
		displayRollDice(false);

		setConstraints(con, 0, 0, 2, true, true);
		con.gridheight = 5;

		con.insets = new Insets(0,0,0,0);
		pane.add(canvas, con);
		setVisible(true);
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
	 * @param width - how many columns should the button be in
	 * @param listener - the listener for the button
	 * @param con - the constraints to use
	 *
	 * @return the created button
	 */
	private JButton createButton(Container pane, String text, int x, int y, int width, int bottomInset, ActionListener listener, GridBagConstraints con){
		JButton button = new JButton(text);
		button.setOpaque(false);
		button.addActionListener(listener);
		setConstraints(con, x, y, width, false, false);
		con.insets = new Insets(1, 20, bottomInset, 1);
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
	private void setConstraints(GridBagConstraints con, int x, int y, int width, boolean expandX, boolean expandY){
		con.gridy = y;
		con.gridx = x;
		con.gridwidth = width;
		con.insets = new Insets(1, 20, 2, 1);
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

	public Canvas getCanvas(){
		return canvas;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType type) {
		canvas.onCharacterJoinedGame(playerName, character, type);
	}

	public void onTurnBegin(String name, Character playersCharacter) {
		canvas.onTurnBegin(name, playersCharacter);
	}

	@Override
	public void onDiceRolled(int dice1, int dice2) {
		canvas.onDiceRolled(dice1, dice2);
	}

	@Override
	public void onCharacterMove(Character character, Location destination) {
		canvas.onCharacterMove(character, destination);
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion) {
		canvas.onSuggestionUndisputed(suggester, suggestion);
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover) {
		canvas.onSuggestionDisproved(suggester, suggestion, disprover);
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		canvas.onAccusation(accuser, accusation, correct);
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
		canvas.onWeaponMove(weapon, room);
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
		canvas.onGameWon(name, playersCharacter);
	}

	@Override
	public void waitingForNetworkPlayers(int i) {
		canvas.waitingForNetworkPlayers(i);
	}

	@Override
	public void onLostGame(String name, Character playersCharacter) {
		canvas.onLostGame(name, playersCharacter);
	}



	public GameConfig createGameConfig(int min, int max) {
		return new GameConfig(this, min, max);
	}


}
