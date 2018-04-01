import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;

public class WindowRecette extends JFrame implements ActionListener{

	private JLabel nom;
	private String titre;
	private JButton plus;
	private JButton save;
	private JButton fermer;
	private ArrayList<String> ingredients;
	private ArrayList<String> listeIngredients;
	private GenerationRecette_V1 recette;
	private JTabbedPane tab;
	private ArrayList<String> recettes;
	private static int numR;
	
	public WindowRecette(String titre){
		numR = 1;
		this.titre = titre;
		this.ingredients = new ArrayList<>();
		this.listeIngredients = new ArrayList<>();
		this.recettes = new ArrayList<>();
		this.recette = new GenerationRecette_V1(7, this.ingredients);
		initComponents();
	}
	
	public WindowRecette(String titre, ArrayList<String> ingredients){
		numR = 1;
		this.titre = titre;
		this.ingredients = ingredients;
		this.listeIngredients = new ArrayList<>();
		this.recettes = new ArrayList<>();
		this.recette = new GenerationRecette_V1(7, this.ingredients);
		initComponents();
	}
	
	private void initComponents(){
		this.setLayout(new BorderLayout());
		this.nom = new JLabel(this.titre);
		this.add("North",this.nom);
		this.nom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		this.tab = new JTabbedPane();
		this.add("Center", this.tab);
		generateRecette();
		//les boutons
		JPanel panelBoutons = new JPanel();
		panelBoutons.setLayout(new GridLayout(1,3));
		this.plus = new JButton("Plus");
		this.plus.addActionListener(this);
		this.save = new JButton("Enregistrer");
		this.save.addActionListener(this);
		this.fermer = new JButton("X");
		this.fermer.addActionListener(this);
		panelBoutons.add(this.plus);
		panelBoutons.add(this.save);
		panelBoutons.add(this.fermer);
		this.add("South", panelBoutons);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
	
	//generation d'une liste des ingredients 
	private void generateRecette (){
		this.listeIngredients.clear();
		this.listeIngredients = this.recette.generateList();
		toStringRecette();
	}
	
	//impression de la recette sur l'interface graphique
	private void toStringRecette(){
		/*La recette generee*/
		String s = ""; 
		for (int i = 0; i < this.listeIngredients.size(); i++){
			s += " - " + this.listeIngredients.get(i) + "\n";
		}
		this.ajoutRecette(this.listeIngredients);
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setText(s);
		this.tab.add("Recette "+numR,text);
		this.tab.setSelectedIndex(this.tab.getTabCount()-1);
		numR++;
		this.pack();
		this.repaint();
//		this.setSize(numR*80,260);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.plus) {
			generateRecette();
		} else if(e.getSource() == this.save) {
			try {
				//Creer une fenetre pour choisir ou enregistrer le fichier
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("TXT File", "txt"));
				String approve = new String("Enregistrer");
				int resultatEnregistrer = chooser.showDialog(chooser,approve);
				if (resultatEnregistrer == JFileChooser.APPROVE_OPTION) { 
					//Ajoute l'extension TXT au fichier si il ne l'a pas deja
					String cheminFichier;
					if(chooser.getSelectedFile().getAbsolutePath().endsWith(".txt")) {
						cheminFichier = chooser.getSelectedFile().getAbsolutePath();
					} else {
						cheminFichier = chooser.getSelectedFile().getAbsolutePath()+".txt";
					}
					//Se prepare a ecrire les infos dans le fichier
					File fichier = new File(cheminFichier);	    
				    FileWriter fw = new FileWriter(fichier);
					BufferedWriter out = new BufferedWriter(fw);
				    out.write(toStringRecetteDansFichier());
				    out.close();
					JOptionPane.showMessageDialog(null, "Fichier enregistre avec succes","Recette sauvegardee",JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (IOException err) {
			    System.err.println("Error: " + err.getMessage());
			}
		} else if(e.getSource() == this.fermer) {
			int index = this.tab.getSelectedIndex();
			if(index >= 0) {
				this.tab.remove(index);
				this.recettes.remove(index);
			}
		}
		
	}
	
	public void ajoutRecette(ArrayList<String> liste) {
		String s = "";
		for(int i = 0; i < this.listeIngredients.size();i++) {
			s += "- "+liste.get(i)+"\r\n"; 
		}
		this.recettes.add(s);
	}
	
	public String toStringRecetteDansFichier() {
		int index = this.tab.getSelectedIndex();
		return this.recettes.get(index);
	}
}
