import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class parser_ingredients {

	public static String monNormaliseur(String s) {
		CharSequence cq = s;
		cq = Normalizer.normalize(cq,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		String returnedString = cq.toString();
		return returnedString.toLowerCase();
	}

	public static ArrayList<String> getAllIngredients() {
		ArrayList<String> returnedList = new ArrayList<String>();
		String file = "ingredientsSimplifie.txt";
		try {
			InputStream ips = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String line;
			while ((line=br.readLine())!=null){
				returnedList.add(monNormaliseur(line.split(", ")[0]));
			}
			br.close(); 
		} catch(Exception e) {
			System.out.println(e);
		}
		return returnedList;
	}

	public static boolean monContains(String w1, String w2) {
		if(w1.contains(w2)) { // on trouve direct
			return true;
		}
		else {
			if(w1.startsWith(w2)) { // pour gérer les pluriels
				return true;
			}
		}
		return false;
	}

	public static Ingredient transformWithExistant(String line, ArrayList<String> listIngredients) {
		
		line = monNormaliseur(line);
		for(String s : listIngredients) {
			if(monContains(line,s)) {
				String[] splitedLine = line.split(" ");
				double quantity = 1.;
				try{ quantity = new Integer(splitedLine[0]); }
				catch (NumberFormatException e) {
					quantity = 1;
				}
				String typeQ = "";
				try{typeQ = splitedLine[1];} catch (Exception e) {};
				char typeQC = 'x';
				switch (typeQ) {
				case "cl" :
					typeQC = 'l';
					quantity /= 100.;
					break;
				case "ml" :
					typeQC = 'l';
					quantity /= 1000.;
					break;
				case "mg" :
					typeQC = 'g';
					quantity /= 1000.;
					break;
				case "g" :
					typeQC = 'g';
					break;
				case "l" :
					typeQC = 'l';
					break;
				default :
					typeQC = 'p';
				}
				return new Ingredient(s, typeQC, quantity);
			}
		}
		return new Ingredient("LOST : " + line, 'x', 0);
	}
	
	public static ArrayList<Ingredient> findAllExistants(String line, ArrayList<Ingredient> listIngredients) {
		ArrayList<Ingredient> found = new ArrayList<Ingredient>();
		String[] subLines = line.split("\\.|;|,|\\n");
		for(String l : subLines) {
			Ingredient ing = transformWithExistant(l, transformListIngredientToString(listIngredients));
			if(!ing.getName().startsWith("LOST") && !found.contains(ing.getName())) {
				found.add(ing);
			}
		}
		return found;
	}
	
	public static ArrayList<String> transformListIngredientToString(ArrayList<Ingredient> ings) {
		ArrayList<String> ingsString = new ArrayList<String>();
		for(int i = 0; i < ings.size(); i++) {
			ingsString.add(ings.get(i).getName());
		}
		return ingsString;
	}

	public static void main(String[] args) throws Exception {
		int nb = 0;
		// Tous les ingrédients formattées
		ArrayList<String> listAllIngredients = getAllIngredients();

		// création du fichier de résultat
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("result.xml")));
		writer.write("<resultats>\n");

		// Lecture des recettes
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File("recettes.xml"));

		//racine
		Element racine = document.getDocumentElement();
		NodeList racineNoeuds = racine.getChildNodes();
		// Liste des recettes
		ArrayList<Element> recettes = new ArrayList<Element>();
		for(int i = 0; i < racineNoeuds.getLength(); i++) {
			if(racineNoeuds.item(i).getNodeName().equals("recette")) {
				Element elmt = (Element) racineNoeuds.item(i);
				recettes.add(elmt);

				// Liste avec ingredients et etapes
				NodeList noeuds = elmt.getChildNodes();

				ArrayList<Ingredient> ingredientsATrier = new ArrayList<Ingredient>();

				for(int j = 0; j < noeuds.getLength(); j++) {
					if(noeuds.item(j).getNodeName().equals("ingredients")) {
						Element listIngredients = (Element) noeuds.item(j);

						// Liste des ingredients
						NodeList childs = listIngredients.getChildNodes();
						for(int k = 0; k < childs.getLength(); k++) {
							if(childs.item(k).getNodeName().equals("ingredient")) {
								ingredientsATrier.add(transformWithExistant(childs.item(k).getTextContent(),listAllIngredients));
							}
						}
					}
					
					if(noeuds.item(j).getNodeName().equals("etapes")) {
						Element etapes = (Element) noeuds.item(j);
						String etapesLines = etapes.getTextContent();
						
						ArrayList<Ingredient> foundIngredients = findAllExistants(etapesLines, ingredientsATrier);

						ArrayList<Ingredient> ingredientsTries = new ArrayList<Ingredient>();

						for(Ingredient fing : foundIngredients) {
							for(Ingredient ing : ingredientsATrier) {
								if(monContains(fing.getName(), ing.getName()) || monContains(ing.getName(), fing.getName())) {
									if(! ingredientsTries.contains(ing)) {
										ingredientsTries.add(ing);
									}
								}
							}
						}
						
						// ecrire le fichier resultats
						writer.write("\t<ingredients>\n");
						for(Ingredient ting : ingredientsTries) {
							writer.write("\t\t<ingredient>\n\t\t\t<name>" + ting.getName() + "</name>\n\t\t\t<type>" + ting.getTypeQ() + "</type>\n\t\t\t<quantity>" + ting.getQuantity() + "</quantity>\n\t\t</ingredient>\n");
							ingredientsATrier.remove(ting);
						}
						// ingrédients restant
						for(Ingredient ting : ingredientsATrier) {
							writer.write("\t\t<ingredient>\n\t\t\t<name>" + ting.getName() + "</name>\n\t\t\t<type>" + ting.getTypeQ() + "</type>\n\t\t\t<quantity>" + ting.getQuantity() + "</quantity>\n\t\t</ingredient>\n");
						}
						writer.write("\t</ingredients>\n");
					}
				}
			}
		}
		writer.write("</resultats>\n");
		writer.close();
	}
}