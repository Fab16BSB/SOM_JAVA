package code;

import java.util.Scanner;

public class Lancement {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Veuillez saisir le nom du fichier à lire :");
    Traitement t = new Traitement(sc.nextLine());

    System.out.println("Lecture des données du fichier...");
    //t.lectureIntervale(t.getDatas());

    System.out.println("Lecture aléatoire des données du fichier...");
    t.lectureIntervaleAleatoire(t.getDatas());

    System.out.println("\nLecture des données normalisées...");
    //t.lectureIntervale(t.getNormeDatas());

    t.calculeVecteurMoyen();

    System.out.println("Veuillez saisir une borne supérieure pour l'intervalle :");
    double borneSupp = Double.parseDouble(sc.nextLine());

    System.out.println("Veuillez saisir une borne inférieure pour l'intervalle :");
    double borneInf = Double.parseDouble(sc.nextLine());

    System.out.println("\nCréation des intervalles de données...");
    t.creerIntervale(borneSupp, borneInf);

    t.genereMatrice();

    System.out.println("Veuillez saisir votre mode de lecture : 0 pour normal, 1 pour aléatoire");
    int aleatoire = Integer.parseInt(sc.nextLine());

    t.rapprochement(aleatoire);
    //t.afficheMatrice(t.getMatrice());
    System.out.println("Rapprochement terminé.\n");
    //t.afficherNoeud();

    System.out.println("Classement des données...\n");
    t.distanceEuclidienneInverse();
    t.rename();
    t.afficherNoeud();
    t.compteClasse();
  }
}
