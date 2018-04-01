import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MatriceAssociation {
	Association[][] matrix;
	Map<String, Integer> hashmap;
	ArrayList<Character> types;
	int cnt;

	// Constructeur
	public MatriceAssociation() {
		this.hashmap = new HashMap<>();
		this.cnt = 0;
		this.types = new ArrayList<Character>();
	}

	// Initialise la matrice d'association avec des 0
	private void MatrixInit(int size) {
		this.matrix = new Association[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.matrix[i][j] = new Association();
			}
		}
	}

	// Ecrit dans un fichier en sortie la table d'associatio en .csv ainsi que
	// la hashmap en .csv
	private void sortieTxt() {
		File file = new File("outputhashmap.csv");
		file.delete();
		file = new File("output.csv");
		file.delete();

		try {
			System.setOut(new PrintStream(new FileOutputStream("outputhashmap.csv")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String str : this.hashmap.keySet()) {
			System.out.println(str + ", " + this.hashmap.get(str) + ", " + this.types.get(this.hashmap.get(str)));
		}

		try {
			System.setOut(new PrintStream(new FileOutputStream("output.csv")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int j = 0; j < this.hashmap.size(); j++) {
			for (int i = 0; i < this.hashmap.size(); i++) {
				if (i == 0) {
					this.matrix[i][j].print();
				} else {
					System.out.print(";");
					this.matrix[i][j].print();
				}
			}
			System.out.println("");
		}
		/*
		 * // créer un nouveau fichier excel FileOutputStream out; try { out =
		 * new FileOutputStream("matrix.xls"); // créer un classeur Workbook wb
		 * = new HSSFWorkbook(); // créer une feuille Sheet mySheet =
		 * wb.createSheet();
		 * 
		 * // créer une ligne de légende des ingredients Row rowLegend = null;
		 * rowLegend = mySheet.createRow(0); int indice=1; for(String str :
		 * this.hashmap.keySet()){
		 * rowLegend.createCell(indice).setCellValue(str); indice++; }
		 * 
		 * // créer une colonne de légende des ingredients int indice_ligne=1;
		 * for(String str : this.hashmap.keySet()){ Row row =
		 * mySheet.createRow(indice_ligne); row.createCell(0).setCellValue(str);
		 * for(int indice_col=1;indice_col<=this.hashmap.size();indice_col++){
		 * row.createCell(indice_col).setCellValue(this.matrix[this.hashmap.get(
		 * rowLegend.getCell(indice_col).getStringCellValue())][this.hashmap.get
		 * (str)]); } indice_ligne++; }
		 * 
		 * wb.write(out);
		 * 
		 * out.close(); } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	// Retourne la martice d'association
	public Association[][] getMatrix() {
		return this.matrix;
	}

	// Retourne la HashMap
	public Map<String, Integer> getHashMap() {
		return this.hashmap;
	}

	// Calcul les affinités entre les ingredients listés dans le fichier xml et
	// remplit la matrice
	public void launch() {

		// indice dans la matrice
		int indice1, indice2;

		/*
		 * Etape 1 : récupération d'une instance de la classe
		 * "DocumentBuilderFactory"
		 */
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : création d'un Document
			 */
			final Document document = builder.parse(new File("result.xml"));

			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();

			// Boucle pour parcourir balises ingredient et init hashtable
			// on donne le path de tous les ingredients et on les parcourt pour
			// les ajouter
			final NodeList baliseIngredient = racine.getElementsByTagName("ingredient");
			final int nbBalises = baliseIngredient.getLength();
			for (int i = 0; i < nbBalises; i++) {
				Node name = ((Element) baliseIngredient.item(i)).getElementsByTagName("name").item(0);
				Node type =  ((Element) baliseIngredient.item(i)).getElementsByTagName("type").item(0);
				if (!name.getTextContent().startsWith("LOST")) {
					if (this.hashmap.get(name.getTextContent()) == null) {
						this.hashmap.put(name.getTextContent(), this.cnt);
						this.types.add(type.getTextContent().charAt(0));
						this.cnt++;
					}
				}

			}

			// CREATION DE LA MATRICE D'ASSOCIATION
			this.MatrixInit(this.cnt + 1);

			// Matrice d'association
			/*
			 * Etape 5 : récupération des recettes
			 */
			final NodeList racineNoeuds = racine.getChildNodes(); // balise
																	// recette
			final int nbRacineNoeuds = racineNoeuds.getLength(); // nb_recette

			for (int i = 0; i < nbRacineNoeuds; i++) {
				if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element recette = (Element) racineNoeuds.item(i);
					/*
					 * Etape 7 : Ajout dans la table d'association
					 */
					final NodeList ingredients = recette.getElementsByTagName("ingredient");
					final int nbIngredients = ingredients.getLength();

					for (int j = 0; j < nbIngredients - 1; j++) {

						final Element ingredient = (Element) ingredients.item(j);

						String nom = ingredient.getElementsByTagName("name").item(0).getTextContent();
						String type = ingredient.getElementsByTagName("type").item(0).getTextContent();
						String quantity = ingredient.getElementsByTagName("quantity").item(0).getTextContent();

						if (!nom.startsWith("LOST")) {

							Ingredient ingre = new Ingredient(nom, type.charAt(0), Double.parseDouble(quantity));

							for (int k = j + 1; k < nbIngredients; k++) {
								final Element ingredient2 = (Element) ingredients.item(k);
								nom = ingredient2.getElementsByTagName("name").item(0).getTextContent();
								type = ingredient2.getElementsByTagName("type").item(0).getTextContent();
								quantity = ingredient2.getElementsByTagName("quantity").item(0).getTextContent();

								if (!nom.startsWith("LOST")) {

									Ingredient ingre2 = new Ingredient(nom, type.charAt(0),
											Double.parseDouble(quantity));
									// Ajout dans la table d'association
									indice1 = this.hashmap.get(ingre.getName());
									indice2 = this.hashmap.get(ingre2.getName());
									if (indice1 != indice2) {
										this.matrix[indice1][indice2].increAffinity();
										double quotienQuant = ingre.getQuantity() / ingre2.getQuantity();
										this.matrix[indice1][indice2].calcMoyenne(quotienQuant);
									}
								}
							}
						}
					}
				}
			}

			this.CalculEcartType();
			this.sortieTxt();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
		} catch (final SAXException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void CalculEcartType(){
		
		try{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			/*
			 * Etape 2 : création d'un parseur
			 */
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*
			 * Etape 3 : création d'un Document
			 */
			final Document document = builder.parse(new File("result.xml"));

			/*
			 * Etape 4 : récupération de l'Element racine
			 */
			final Element racine = document.getDocumentElement();
			
			int indice1, indice2;

			final NodeList racineNoeuds = racine.getChildNodes(); // balise
																// recette
			final int nbRacineNoeuds = racineNoeuds.getLength(); // nb_recette

			for (int i = 0; i < nbRacineNoeuds; i++) {
				if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
				final Element recette = (Element) racineNoeuds.item(i);
				/*
				 * Etape 7 : Ajout dans la table d'association
				 */
				final NodeList ingredients = recette.getElementsByTagName("ingredient");
				final int nbIngredients = ingredients.getLength();

				for (int j = 0; j < nbIngredients - 1; j++) {

					final Element ingredient = (Element) ingredients.item(j);

					String nom = ingredient.getElementsByTagName("name").item(0).getTextContent();
					String type = ingredient.getElementsByTagName("type").item(0).getTextContent();
					String quantity = ingredient.getElementsByTagName("quantity").item(0).getTextContent();

					if (!nom.startsWith("LOST")) {

						Ingredient ingre = new Ingredient(nom, type.charAt(0), Double.parseDouble(quantity));

						for (int k = j + 1; k < nbIngredients; k++) {
							final Element ingredient2 = (Element) ingredients.item(k);
							nom = ingredient2.getElementsByTagName("name").item(0).getTextContent();
							type = ingredient2.getElementsByTagName("type").item(0).getTextContent();
							quantity = ingredient2.getElementsByTagName("quantity").item(0).getTextContent();

							if (!nom.startsWith("LOST")) {

								Ingredient ingre2 = new Ingredient(nom, type.charAt(0),
										Double.parseDouble(quantity));
								// Ajout dans la table d'association
								indice1 = this.hashmap.get(ingre.getName());
								indice2 = this.hashmap.get(ingre2.getName());
								if (indice1 != indice2) {
									double quotienQuant = ingre.getQuantity() / ingre2.getQuantity();
									this.matrix[indice1][indice2].increSumEcartType(quotienQuant);
								}
							}
						}
					}
				}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Character> getAllIngredientsWithType() {
		HashMap<String, Character> returnedList = new HashMap<String, Character>();
		String file = "ingredientsSimplifie.txt";
		try {
			InputStream ips = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line=br.readLine())!=null){
				returnedList.put(parser_ingredients.monNormaliseur(line.split(", ")[0]), line.split(", ")[1].charAt(0));
			}
			br.close(); 
		} catch(Exception e) {
			System.out.println(e);
		}
		return returnedList;
	}
	
	public void parseMatriceAssociation() throws NumberFormatException, IOException {
		HashMap<String, Character> ingres = getAllIngredientsWithType();
		
		BufferedReader br = new BufferedReader(new FileReader("outputhashmap.csv"));
		this.hashmap = new HashMap<String, Integer>();
		this.cnt = 0;
		int nbLines = countLines("outputhashmap.csv");
		this.types = new ArrayList<Character>();
		for(int i = 0; i < nbLines; i++) {
			this.types.add('p');
		}
		
		for(String line; (line = br.readLine()) != null; ) {
			String[] infos = line.split(", ");
			this.hashmap.put(infos[0], new Integer(infos[1]));
			this.types.set(this.hashmap.get(infos[0]), ingres.get(infos[0]));
			this.cnt++;
	    }
		this.MatrixInit(this.cnt);
		
		br = new BufferedReader(new FileReader("output.csv"));
		int i = 0;
		for(String line; (line = br.readLine()) != null; ) {
			int j = 0;
			String[] assocs = line.split(";");
			for(String assoc : assocs) {
				this.matrix[j][i] = Association.parseAssociation(assoc);
				j++;
			}
			i++;
		}
	}

	public static void main(String[] args) {
		MatriceAssociation matrix = new MatriceAssociation();
		matrix.launch();
		
		/*try {
			matrix.parseMatriceAssociation();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		matrix.sortieTxt();
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		ArrayList<Character> arc = matrix.getTypes();
		for(Character c : arc) {
			System.out.println(c);
		}*/
	}
	
	
	public ArrayList<Character> getTypes() {
		return types;
	}

	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

}
