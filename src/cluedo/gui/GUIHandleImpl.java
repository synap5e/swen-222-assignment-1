package cluedo.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Weapon;

public class GUIHandleImpl implements GUIHandle {

	private boolean wait = false;
	
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
		return numberOfPlayersModel.getNumber().intValue();
	}

	@Override
	public List<String> getHumanNames() {
		// TODO Auto-generated method stub
		return null;
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
