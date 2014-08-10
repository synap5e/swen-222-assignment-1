package cluedo.gui;

import java.util.List;

import cluedo.game.board.Character;

public interface GUIHandle {

	public int getNumberOfPlayers();

	public Character chooseCharacter(int playerNumber, List<Character> characters);

	public int getNumberOfHumanPlayers(int max);

}
