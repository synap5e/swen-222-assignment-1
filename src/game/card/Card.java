package game.card;

public abstract class Card {
	public enum Type {WEAPON, ROOM, PERSON};
	
	private Type type;
	
	public Card(Type type){
		this.type = type;
	}
	
	public Type getType(){
		return type;
	}
	
	public abstract String getName();
	public abstract String getImageName();
}
