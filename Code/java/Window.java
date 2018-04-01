import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class Window extends JFrame implements ActionListener{

	private JLabel titre;
	private ButtonGroup boutonRadio;
	private JRadioButton br1;
	private JRadioButton br2;
	private JButton creer;
	private JTextField choixIngredient;	
	private JPanel espaceChoix;
	private JPanel espaceBoutonCreer;
	private String[] ingredients;
	private ArrayList<String> ingr;
	
	public Window(String nom){
		this.setTitle(nom);
		this.ingredients = null;
		this.ingr = new ArrayList<String>();
		initComponents();
	}
	
	private void initComponents(){
		
		//Creation des elements a ajouter au layout
		this.titre = new JLabel("Creation de recette");
		this.boutonRadio = new ButtonGroup();
		this.br1 = new JRadioButton("A partir d'une liste d'ingredients");
		
		this.br1.setSelected(true);
		this.br2 = new JRadioButton("Aleatoirement");
		this.boutonRadio.add(br1);
		this.boutonRadio.add(br2);
		this.creer = new JButton("Creer");
		this.creer.addActionListener(this);
		this.choixIngredient = new JTextField();
		//Mise en forme des layouts et ajout des elements au layout
		this.setLayout(new GridLayout(6,1));
		this.add(this.titre);
		this.titre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		this.add(this.br1);
		this.espaceChoix = new JPanel();
		this.add(this.espaceChoix);
		//Layout pour le choix ingredient
		this.espaceChoix.setLayout(new BorderLayout());
		this.espaceChoix.add("North",new JLabel("   (Separer les ingredients par une virgule \",\")"));
		this.espaceChoix.add("West",new JLabel("        "));
		this.espaceChoix.add("East",new JLabel("                "));
		this.espaceChoix.add("Center",this.choixIngredient);
		this.add(this.br2);
		//Layout pour le bouton creer
		this.espaceBoutonCreer = new JPanel();
		this.add(this.espaceBoutonCreer);
		this.espaceBoutonCreer.setLayout(new BorderLayout());
		this.espaceBoutonCreer.add("West",new JLabel("                                        "));
		this.espaceBoutonCreer.add("East",new JLabel("   "));
		this.espaceBoutonCreer.add("Center",this.creer);
		//Taille de l'ecran
		Dimension tailleEcran = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int hauteur = (int)tailleEcran.getHeight();
		int largeur = (int)tailleEcran.getWidth();
		this.setSize(largeur/5,hauteur/3);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		/*Si l'utilisateur fourni la liste des ingredients */
		if (this.br1.isSelected()){
			this.listeIngredients();
			System.out.println(this.ingr.toString());
			if(!this.ingr.isEmpty()) {
				WindowRecette recette = new WindowRecette("Recette", this.ingr);
			} else {
				JOptionPane.showMessageDialog(null, "Vous n'avez entre aucun aliment", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			WindowRecette recette = new WindowRecette("Recette");
		}
	}
	
	private void listeIngredients(){
		this.ingr.clear();
		/*Recuperer la liste des ingredients que l'utilisateur donne et les enregistrer sur l'ArrayList*/
		String str = this.choixIngredient.getText();
		this.ingredients = str.split(",");
		//faire passer les ingredients entres par l'utilisateur dans le normaliseur
		for(int i=0; i<this.ingredients.length;i++){
			this.ingredients[i] = parser_ingredients.transformWithExistant(this.ingredients[i],parser_ingredients.getAllIngredients()).getName();
		}
		//mettre les aliments du tableau dans l'arraylist
		for(int i=0; i<this.ingredients.length; i++){
			this.ingr.add(this.ingredients[i]);
		}
	}
}
