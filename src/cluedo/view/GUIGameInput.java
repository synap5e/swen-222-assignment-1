package cluedo.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cluedo.controller.interaction.GameInput;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;

/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public class GUIGameInput implements GameInput {

	private boolean wait = false;
	private int numberOfPlayers;
	private CluedoFrame frame;
	
	public GUIGameInput(CluedoFrame frame){
		this.frame = frame;
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

		return playerNames;
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
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		wait = true;
		frame.getCanvas().unselectLocation();
		frame.getCanvas().setPossibleLocations(possibleLocations);
		frame.getCanvas().repaint();
		while (wait){
			try {
				Thread.sleep(20);
				if (possibleLocations.contains(frame.getCanvas().getSelectedLocation())){
					frame.getCanvas().setPossibleLocations(null);
					return frame.getCanvas().getSelectedLocation();
				}
			} catch (InterruptedException e1) {
			}
		}
		return null;
	}

	@Override
	public boolean hasSuggestion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Weapon pickWeapon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character pickCharacter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAccusation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Room pickRoom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Card selectDisprovingCardToShow(Character character,
			List<Card> possibleShow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suggestionDisproved(Character characterDisproved,
			Card disprovingCard) {
		// TODO Auto-generated method stub
		
	}

}
