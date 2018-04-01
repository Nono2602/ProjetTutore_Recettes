Readme :

Fichiers à la racine du projet :
IngredientsSimplifie.txt : liste de tous les ingrédients pour la normalisation
output.csv : matrice d'association
outputHashmap.csv : hashmap pour la correspondance entre le nom des ingrédients et leur indice dans la table d'association
recettes.xml : recettes brutes extraites d'un site de cuisine
result.xml : recettes normalisées pour exploitation (résultat de la normalisation)

Classes java :
Association : une association entre deux ingrédients, c'est une case de la matrice d'association
Couple : utilisé pour la génération des recettes
GenerationRecette_V1 : génère une liste d'ingrédients
Ingredient : modélisation d'un ingrédient
Launcher : permet de lancer l'IHM, celle-ci va charger la matrice d'assocation du fichier output.csv toute seule
MatriceAssocation : modélisation d'une matrice d'association, contient une fonction main() pour faire à nouveau la phase d'apprentissage et recréer cette matrice
parser_ingredients : normalise les listes d'ingrédients et les met dans l'ordre de leur apparition dans les étapes de la recette
window : fenêtre principale de l'IHM
windowRecette : fenêtre pour afficher les recettes générées

Je n'ai pas fourni le lien du parser que nous avons fait pour récupérer les recettes de cuisine du site utilisé.