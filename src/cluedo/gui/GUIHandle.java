package cluedo.gui;

import java.util.List;
import java.util.Set;

import cluedo.game.board.Character;

public interface GUIHandle {

	public int getNumberOfPlayers();

	public Character chooseCharacter(List<Character> characters);

}
