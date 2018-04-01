//Classe utilisée pour les méthodes de la classe GenerationRecette 


public class Couple {
	private int value;
	private int indice;
	
	public Couple(int value, int indice){
		this.value=value;
		this.indice=indice;
	}
	
	public void setValue(int value){this.value=value;}
	public void setIndice(int indice){this.indice=indice;}
	
	public int getValue(){return this.value;}
	public int getIndice(){return this.indice;}
	
}
