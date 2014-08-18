package cluedo.controller.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cluedo.controller.interaction.GameListener;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** This class defines a basic AI that keeps track of cards that it thinks may be possible 
 * and progressively works through that list until it has a suggestion that is not disproved,
 * then makes an accusation. If there are multiple possible rooms it will favor checking the 
 * closest room.
 * 
 * It should never make an incorrect accusation.
 *
 * @author Simon Pinfold
 *
 */
public class BasicAIPlayer extends Player implements GameListener{

	private GameStateFacade gameView;
	
	// credit to http://wiki.teamfortress.com/wiki/Bots#AI_bot_names
	private static final String[] AI_NAMES = new String[]{
		"A Professional With Standards", "AimBot", "AmNot", "Aperture Science Prototype XR7", "Archimedes!", "BeepBeepBoop",
		"Big Mean Muther Hubbard", "Black Mesa", "BoomerBile", "Cannon Fodder", "CEDA", "Chell", "Chucklenuts", "Companion Cube",
		"Crazed Gunman", "CreditToTeam", "CRITRAWKETS", "Crowbar", "CryBaby", "CrySomeMore", "C++", "DeadHead", "Delicious Cake",
		"Divide by Zero", "Dog", "Force-A-Nature", "Freakin' Unbelievable", "Gentlemanne of Leisure", "GENTLE MANNE of LEISURE",
		"GLaDOS", "Grim Bloody Fable", "GutsAndGlory!", "Hat-Wearing MAN", "Headful of Eyeballs", "Herr Doktor", "HI THERE",
		"Hostage", "Humans Are Weak", "H@XX0RZ", "I LIVE!", "It's Filthy in There!", "IvanTheSpaceBiker", "Kaboom!", "Kill Me",
		"LOS LOS LOS", "Maggot", "Mann Co.", "Me", "Mega Baboon", "Mentlegen", "Mindless Electrons", "MoreGun", "Nobody",
		"Nom Nom Nom", "NotMe", "Numnutz", "One-Man Cheeseburger Apocalypse", "Poopy Joe", "Pow!", "RageQuit", "Ribs Grow Back",
		"Saxton Hale", "Screamin' Eagles", "SMELLY UNFORTUNATE", "SomeDude", "Someone Else", "Soulless", "Still Alive", "TAAAAANK!",
		"Target Practice", "ThatGuy", "The Administrator", "The Combine", "The Freeman", "The G-Man", "THEM", "Tiny Baby Man", 
		"Totally Not A Bot", "trigger_hurt", "WITCH", "ZAWMBEEZ", "Ze Ubermensch", "Zepheniah Mann", "0xDEADBEEF", "10001011101"};
	private static LinkedList<String> names = null;
	
	/** Gets another random name for the AI, if possible, choosing one that has
	 * not been taken yet.
	 * 
	 * @return the new name
	 */
	private static String getNextName() {
		if (names == null || names.size() == 0){
			names = new LinkedList<String>(Arrays.asList(AI_NAMES));
			Collections.shuffle(names);
		}
		return names.poll();
	}
	
	private static boolean enableThinkWait = true;
	
	/** Pause for a short amount of time so the AI does
	 * not make it's turns instantaneously. 
	 * 
	 */
	public static void thinkWait() {
		if (!enableThinkWait) return;
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** Disable the wait time on all AI players
	 * 
	 */
	public static void disableThinkWait() {
		enableThinkWait = false;
	}
	
	
	private List<Weapon> possibleWeapons;
	private List<Character> possibleCharacters;
	private List<Room> possibleRooms;
	private Accusation possibleAccusation;

	private boolean sure;
	
	public BasicAIPlayer(Hand h, Character c, GameStateFacade gameView) {
		super(getNextName(), h, c);
		this.gameView = gameView;
		
		possibleWeapons = gameView.getWeapons();
		possibleCharacters = gameView.getCharacters();
		possibleRooms = gameView.getRooms();
		
		for (Card card : hand){
			possibleRooms.remove(card);
			possibleWeapons.remove(card);
			possibleCharacters.remove(card);
			
		}
		
		createPossibleAccusation();
	}

	/** Create a new possible accusation, favoring the closest possible room.
	 * 
	 */
	private void createPossibleAccusation() {
		List<Room> posRoomsClone = new ArrayList<Room>(possibleRooms);
		Collections.sort(posRoomsClone, new Comparator<Room>() {
			@Override
			public int compare(Room o1, Room o2) {
				return gameView.distanceBetween(gameView.getMyLocation(), o1) - gameView.distanceBetween(gameView.getMyLocation(), o2);
			}
		});
		Room closestPossibleRoom = posRoomsClone.get(0);
		this.possibleAccusation = new Accusation(possibleWeapons.get(0), possibleCharacters.get(0), closestPossibleRoom);
	}
	
	
	// javadoc inherited from interfaces
	
	@Override
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType type) {
	}

	@Override
	public void onTurnBegin(String name, Character playersCharacter) {
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		// we now know !(a.w ^ a.c ^ a.r), but this is too complex for a basic AI
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
	}

	@Override
	public void onCharacterMove(Character character, Location room) {
	}

	@Override
	public void onDiceRolled(int dice1, int dice2) {
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		// get the closest location to the room we want to go to
		// this is not quite optimal pathing, as it does not consider that
		// passing through a room ends a turn, but is good enough for a basic AI
		return gameView.getClosestLocation(possibleLocations, possibleAccusation.getRoom());
	}

	@Override
	public boolean hasSuggestion() {
		thinkWait();
		return gameView.getMyLocation() == possibleAccusation.getRoom();
	}

	@Override
	public Suggestion getSuggestion() {
		return new Suggestion(possibleAccusation.getWeapon(), possibleAccusation.getCharacter());
	}

	@Override
	public boolean hasAccusation() {
		thinkWait();
		return sure;
	}

	@Override
	public Accusation getAccusation() {
		return possibleAccusation;
	}

	@Override
	protected Card selectDisprovingCardToShow(Character character, List<Card> possibleShow) {
		thinkWait();
		
		// no tricky buisness, just show the fist possible card
		return possibleShow.get(0);
	}

	@Override
	public void suggestionDisproved(Suggestion suggestion, Character character, Card disprovingCard) {
		// the card shown is not possible to be one of the cards in the correct
		// accusation as it is in a player's hand
		possibleRooms.remove(disprovingCard);
		possibleWeapons.remove(disprovingCard);
		possibleCharacters.remove(disprovingCard);
	}

	@Override
	public void waitForDiceRollOK() {
		thinkWait();
		createPossibleAccusation();
	}

	@Override
	public void waitingForNetworkPlayers(int i) {
	}

	@Override
	public void onLostGame(String name, Character playersCharacter) {
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion, Room room) {
		if (suggester == character){
			this.sure = true;
		}
		// this would also tell us that each player other than suggester does not have the cards \
		// in suggestion, or the room but this is too complex for our basic AI
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Room room, Character disprover) {
		// agian this could be used, but is too complex for a basic AI
	}

	@Override
	public void onSuggestion(String suggesterPlayerName, Character suggester, Suggestion suggestion, Room room) {
		
	}

}
