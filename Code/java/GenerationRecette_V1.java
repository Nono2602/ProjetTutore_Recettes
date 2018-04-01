import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

//Caractéristiques implémentées : - 1 ingrédient ne peut pas apparaitre 2x dans la liste d'ingredients
//								  - On calcul l'affinité du n-ième ingredient avec tous les (n-1)ième ingredients
//								  - La fonction isValid permet de garder une coherence dans la recette tout en ouvrant les possibilités

public class GenerationRecette_V1 {
	private int size;
	
	private ArrayList<String> recette; //Liste de tous les ingredients + leur quantité associée
	private ArrayList<String> listIngr; //Liste d'ingrédients fournie par l'utilisateur
	private ArrayList<String> ingredients; //Liste de tous les ingrédients composant la recette
	
	private MatriceAssociation associations; 
	private int sizeTab;
	private double qIngre1;

	/**Constructeur, appelle la méthode lauch de MatriceAssociation pour remplir la matrice*/
	public GenerationRecette_V1(int size, ArrayList<String> Ingre_input) {
		this.size = size;
		this.listIngr = Ingre_input;
		this.sizeTab = 15;
		this.ingredients=new ArrayList<String>();
		this.recette = new ArrayList<String>();
		this.associations = new MatriceAssociation();
		try {
			this.associations.parseMatriceAssociation();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.qIngre1 = 0.0;
	}

	/** Génère une liste d'ingredients*/
	public ArrayList<String> generateList() {
		Couple[] tabAffinity = new Couple[this.sizeTab];
		String currentIngre;
		
		// Si on ne donne pas d'ingredient initial, on en prend 1 aleatoirement
		if (this.listIngr.isEmpty()) {
			Random randomGenerator = new Random();
			int randomIngr = randomGenerator.nextInt(this.associations.getHashMap().size());
			currentIngre = this.getKeyHashMap(randomIngr);
			this.ingredients.add(currentIngre);
			this.qIngre1 = this.getQuantiteRandom(currentIngre);

			this.recette.add(this.getKeyHashMap(randomIngr) + " (" + qIngre1 + " " + this.associations.getTypes().get(randomIngr) + ")");
			
		} else {
			//ajout du premier ingredient avec sa quantite aleatoire
			currentIngre = this.listIngr.get(this.listIngr.size() - 1);
			this.ingredients.add(currentIngre);
			this.qIngre1 = this.getQuantiteRandom(this.listIngr.get(0));
			String element = this.listIngr.get(0) + " (" + qIngre1 + " " + this.associations.getTypes().get(this.getIndice(this.listIngr.get(0))) + ")";
			this.recette.add(element);
					
			//Puis ajout des autres ingrédients fournis avec leur quantité calculée
			for (int i = 1; i < this.listIngr.size(); i++) {
				this.ingredients.add(this.listIngr.get(i));
				String quantite = this.getQuantite(this.listIngr.get(i));
				element = this.listIngr.get(i) + " (" + quantite + ")";
				this.recette.add(element);
			}
		}
		
		//On determine ensuite les ingredients et leur quantité pour completer la liste		
		for (int i = this.listIngr.size(); i < this.size; i++) {
			tabAffinity = this.getAffinities(currentIngre);
			currentIngre = this.getRandomAffinity(tabAffinity);
			String quantite = this.getQuantite(currentIngre);
			this.ingredients.add(currentIngre);
			this.recette.add(currentIngre+" ("+ quantite+")");
		}

		//On flush la liste d'ingrédient pour la prochaine utilisation
		this.ingredients.clear();
		
		return this.recette;
	}

	/**A partir d'un Ingredient, génére une liste des sizeTab meilleures 
	 * affinités de cet ingredient*/
	private Couple[] getAffinities(String currentIngr) {
		// On veut recuperer la valeur de l'affinité mais il faut aussi se
		// souvenir de l'indice
		// -> On utilise des Couples
		int current = 0;
		int cntValid = 0;
		Couple[] topList = new Couple[this.sizeTab];
		for (int j = 0; j < topList.length; j++) {
			topList[j] = new Couple(Integer.MIN_VALUE, -1);
		}
		
		
		for (int i = 0; i < this.associations.getHashMap().size(); i++) {
			// On verifie ici que le prochain ingredient a au moins une affinité
			// avec les ingredients de la liste
			if (isValid(i)) {
				cntValid++;
				current = calculAffinitiesList(i);
				int indice = 0;
				for (int j = 0; j < topList.length; j++) {
					if (current < topList[j].getValue()) {
						indice++;
					}
				}
				if (indice < this.sizeTab) {
					for (int k = topList.length - 1; k >= indice + 1; k--) {
						topList[k].setValue(topList[k - 1].getValue());
						topList[k].setIndice(topList[k - 1].getIndice());
					}
					topList[indice].setValue(current);
					topList[indice].setIndice(i);
				}
			}
			// Permet de remplir le tableau avec des ingredients aleatoire si
			// jamais pas assez d'ingredients valid pour remplir le tab
			topList = isTabFull(topList, cntValid);
		}
		return topList;
	}

	/** Permet de garder une cohérence au niveau des ingredients mais laisse les
	* possibilités ouvertes
	* il faut que l'ingredient n+1 match avec au moins 80% des ingredients de
	* la liste*/
	private boolean isValid(int index) {
		int cntMatch = 0;
		boolean cond1, cond2, toReturn;
		if (!this.ingredients.contains(this.getKeyHashMap(index))){
			for (int j = 0; j < this.listIngr.size(); j++) {
				cond1 = this.associations.getMatrix()[this.associations.getHashMap().get(this.listIngr.get(j))][index].getAffinity() != 0;
				cond2 = this.associations.getMatrix()[index][this.associations.getHashMap().get(this.listIngr.get(j))].getAffinity() != 0;
				if (cond1 || cond2) {
					cntMatch++;
				}
			}
		}
		if(this.listIngr.size() > 0) {
			toReturn = ((cntMatch * 100) / this.listIngr.size() >= 80);
		}
		else {
			if(this.ingredients.contains(this.getKeyHashMap(index))) {
				toReturn = false;
			}
			else {
				toReturn = true;
			}
		}
		return toReturn;
	}

	/** Permet de remplir le tableau avec des ingredients aleatoire si jamais pas
	* assez d'ingredients valid pour remplir le tab*/
	private Couple[] isTabFull(Couple[] tab, int nbValid) {
		if (nbValid >= tab.length) {
			return tab;
		} else {
			for (int index = nbValid; index < tab.length; index++) {
				Random randomGenerator = new Random();
				int randomIngr = randomGenerator.nextInt(this.associations.getHashMap().size());
				tab[index].setValue(0);
				tab[index].setIndice(randomIngr);
			}
		}
		return tab;
	}

	/** Permet de récupérer l'ingredient à partir de son indice (fonctionne ici
	 	car hashmap injective)*/
	public String getKeyHashMap(int indice) {
		for (String ingre : this.associations.getHashMap().keySet()) {
			if (this.associations.getHashMap().get(ingre) == indice) {
				return ingre;
			}
		}
		System.out.println("ERROR: Ingredient not found in method getKeyHashMap\n");
		System.exit(-1);
		return "";
	}

	/** Prend un ingredient aléatoire dans la liste de Couples*/
	private String getRandomAffinity(Couple[] tab) {
		String toReturn = new String();
		int cntError = 0;
		// la boucle do..while permet de ne pas avoir 2x le même ingredient dans
		// la liste
		do {
			if (cntError < tab.length + 1) {
				Random randomGenerator = new Random();
				int randomIngr = tab[randomGenerator.nextInt(this.sizeTab)].getIndice();
				toReturn = this.getKeyHashMap(randomIngr);
				cntError++;
			} else {
				System.out.println(this.listIngr);
				System.exit(0);
			}
		} while (this.listIngr.contains(toReturn));
		return toReturn;
	}

	/** Permet de calculer l'affinité d'un ingredient avec tous les ingredients
	 *  déjà present dans la liste */
	private int calculAffinitiesList(int index) {
		int toReturn = 0;
		int indice;
		int score;
		for (int k = 0; k < this.listIngr.size(); k++) {
			indice = this.associations.getHashMap().get(this.listIngr.get(k));
			score = this.associations.getMatrix()[indice][index].getAffinity();
			toReturn += score;
		}
		return toReturn;
	}
	
	  /**Retourne l'indice d'un ingredient donneé */
	private int getIndice(String ingre){
		return this.associations.getHashMap().get(ingre);
	}
	
	/**Retourne une quantite aleatoire pour le premier ingredient */
	private double getQuantiteRandom(String ingre){
		double quantite = 0.0;
		
		switch(this.associations.getTypes().get(this.getIndice(ingre))){
		case 'p' :
			quantite = Math.round((double)(Math.random() * (10-1)+1));
			break;
		case 'l' :
			quantite = this.floor((double)(Math.random() * (1-1)+1),2,0.0);
			break;
		case 'g':
			quantite = this.floor((double)(Math.random() * (500-10)+10),2,0.0);
			break;
		default:
			quantite = Math.round((double)(Math.random() * (20-1)+1));
			break;
		}
		System.out.println("Random Q vaut : "+quantite);
		return quantite;
	}
	
	 /** Retourne une quantite aleatoire pour le deuxieme ingredient
	 * par rapport a la quantite du premier ingredient  */
	private String getQuantite(String ingre){
		String assos="";
		double random=0;
		
		System.out.println(ingre);
		System.out.println(this.ingredients);
		
		
		for (int j=0;j<this.ingredients.size();j++){
			
			System.out.println("size:"+this.ingredients.size());
			
			random=calculQ(j, ingre);
			if (random!=0){
				System.out.println("on break");
				break;
			}
		}
		if (random==0){
			System.out.println("Random vaut 0");
			double quantite=this.getQuantiteRandom(ingre);
			int j = this.getIndice(ingre);
			char unite = this.associations.getTypes().get(j);
			assos = quantite + " " +unite;
			}
		else {
			int j = this.getIndice(ingre);
			char unite = this.associations.getTypes().get(j);
			double quantite = 0.0;
			if (unite == 'p'){
				quantite = Math.round(this.qIngre1*random);
				if (quantite == 0.0){
					quantite = 1;
				}
			}else{
				quantite = this.floor(this.qIngre1*random,2,0.0);
			}
			assos = quantite + " " +unite;
		}
		return assos;
	}
	
	/**Calcul le rapport de quantité entre les ingrédients en utilisant les lois normales associées*/  
	private double calculQ(int i, String ingre){

		System.out.println(this.ingredients);
		int indice1 = this.getIndice(this.ingredients.get(i));
		int indice2 = this.getIndice(ingre);

		
		double moyenne = (this.associations.getMatrix()[indice1][indice2]).getMoyenne();
		double ecartType = (this.associations.getMatrix()[indice1][indice2]).getEcartType();
		System.out.println("moyenne : "+moyenne);
		System.out.println("ecart Type : "+ecartType);

		//double ya = (1.645 + moyenne) * ecartType;
		//double random = Math.abs((double)(Math.random()*((moyenne+ya)-(moyenne-ya))));
		double random = Math.abs((double)Math.random()*((moyenne+ecartType)-(moyenne-ecartType)));
		System.out.println(random);
		return random;
	}
	
	private double floor (double a, int decimales, double plus)
    {
        double p = Math.pow(10.0, decimales);
        //return Math.floor((a*p) + 0.5) / p; // avec arrondi �ventuel (sans arrondi >>>> + 0.0
        return Math.floor((a*p) + plus) / p;
     }

	/** Fonction inverse à GenerationRecette, permet de verifier que l'algorithme de génération
	 * nous donne bien une liste d'ingrédients qui respecte la logique des recettes de la base de donnée
	 */
	public void listMatchingTest() {
		Couple[] tabAffinity = new Couple[this.sizeTab];
		String currentIngre = this.listIngr.get(0);
		boolean match = true;
		try {
		System.setOut(new PrintStream(new FileOutputStream("test.txt")));
		 } catch (FileNotFoundException e) {
		 e.printStackTrace();
		}
		System.out.println("Il faut matcher :" + this.listIngr);
		for (int indexList = 1; indexList < this.size; indexList++) {
			tabAffinity = this.getAffinities(currentIngre);
			match = match && this.isNextInTab(tabAffinity, indexList);
			if (!match) {
				System.out.println("Stop matching at the " + indexList
						+ " ingredient!");
				System.exit(-1);
			}
			currentIngre = this.listIngr.get(indexList);
		}
		System.out.println("On arrive à matcher la liste !");
	}

	/**Fonction utilisée pour la fonction inverse
	 * Permet de determiner que l'ingrédient suivant dans la liste est bien calculé par l'algorithme de
	 * génération.
	 * @param tab
	 * @param index
	 * @return Boolean
	 */
	private boolean isNextInTab(Couple[] tab, int index) {
		for (int i = 0; i < tab.length; i++) {
			if (this.getKeyHashMap(tab[i].getIndice()).equals(this.listIngr.get(index))) {
				return true;
			}
		}
		return false;
	}
	
	
}
