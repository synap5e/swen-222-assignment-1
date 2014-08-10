package cluedo.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Weapon;

public class GUIHandleImpl implements GUIHandle {

	private boolean wait = false;
	private int numberOfPlayers;
	
	@Override
	public int getNumberOfPlayers(int min, int max) {
		JDialog dialog = new JDialog();

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
		JDialog dialog = new JDialog();

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
				System.out.println(names.get(i).getText());
			}
		}

		return playerNames;
	}

	@Override
	public Character chooseCharacter(String playerName,
			List<Character> characters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startTurn(Hand h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		// TODO Auto-generated method stub
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
