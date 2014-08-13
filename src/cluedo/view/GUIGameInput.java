package cluedo.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import cluedo.controller.interaction.GameInput;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;

/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public class GUIGameInput implements GameInput, FrameListener{

	private boolean wait = false;
	
	private boolean waitForDiceRoll = false;
	private boolean endingTurn = false;
	private boolean accusing = false;
	private boolean suggesting = false;
	private int numberOfPlayers;
	private CluedoFrame frame;
	
	private Location selectedLocation = null;
	private Token selectedToken = null;
	
	public GUIGameInput(CluedoFrame frame){
		this.frame = frame;
		frame.addFrameListener(this);
	}
	
	@Override
	public int getNumberOfPlayers(int min, int max) {
		JDialog dialog = new JDialog(frame);
		SpinnerNumberModel numberOfPlayersModel = new SpinnerNumberModel(min, min, max, 1);
		JSpinner spinner = new JSpinner(numberOfPlayersModel);
		
		dialog.add(spinner, BorderLayout.EAST);
		dialog.add(new JLabel("How may players?"), BorderLayout.WEST);
		
		wait = true;
		
		JButton submit = new JButton("Done");
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				wait = false;
			}
		});
		
		dialog.add(submit, BorderLayout.SOUTH);
		dialog.setVisible(true);
		
		//TODO: Remove
		wait = false;
		
		while (wait){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		
		dialog.dispose();
		numberOfPlayers = numberOfPlayersModel.getNumber().intValue();
		return numberOfPlayers;
	}

	@Override
	public List<String> getHumanNames() {
		JDialog dialog = new JDialog(frame);

		dialog.setLayout(new GridLayout(numberOfPlayers+1, 2));
		
		List<JTextField> names = new ArrayList<JTextField>();
		List<JCheckBox> ais = new ArrayList<JCheckBox>();
		
		for (int i = 0; i < numberOfPlayers; ++i){
			final JTextField name = new JTextField();
			dialog.add(name);
			JCheckBox isAI = new JCheckBox("AI");
			isAI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					name.setEditable(!name.isEditable());
				}
			});
			dialog.add(isAI);
			names.add(name);
			ais.add(isAI);
		}
		
		dialog.add(new JLabel(""));
		
		
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
		
		//TODO: Remove
		wait = false;
		while (wait){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
		
		dialog.dispose();
		
		List<String> playerNames = new ArrayList<String>();
		for (int i = 0; i < numberOfPlayers; ++i){
			if (!ais.get(i).isSelected()){
				playerNames.add(names.get(i).getText());
			}
		}
		
		//FIXME
		int num = Integer.parseInt(JOptionPane.showInputDialog(frame, "Number of network players?"));
		for (int i=0;i<num;i++) playerNames.remove(playerNames.size()-1);

		//TODO: return to previous
		List<String> temp = new ArrayList<String>();
		Collections.addAll(temp, "James", "Simon");
		return temp;//playerNames;
	}

	@Override
	public Character chooseCharacter(String playerName,
			List<Character> characters, List<Character> availableCharacters) {
		JDialog dialog = new JDialog(frame);

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
		frame.getCanvas().setCurrentHand(h);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suggestionDisproved(Character characterDisproved,
			Card disprovingCard) {
		frame.getCanvas().setCurrentAction("Suggestion Disproved");
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNetworkPlayerCount() {
		// TODO
		return Integer.parseInt(JOptionPane.showInputDialog(frame, "Number of network players?"));
	}

	@Override
	public String getSingleName() {
		// TODO Auto-generated method stub
		return JOptionPane.showInputDialog("Name?");
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
	public void onPlayerSelection(List<String> names, int numberAI,
			int numberNetwork) {
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

}
