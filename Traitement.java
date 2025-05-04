import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Class responsible for processing and normalizing input data,
 * initializing and managing the SOM grid (neuronal matrix),
 * and storing intermediate results such as normalized values and distance vectors.
 *
 * @author Fab.16
 */
public class Traitement {
  /** List of raw input data (flowers or weights). */
  private ArrayList<Weight> mesFleurs;

  /** Normalized version of the input data. */
  private ArrayList<Weight> normeFleurs;

  /** Randomized subset of the input data used for training. */
  private ArrayList<Weight> intervaleAleatoire;

  /** Array of shuffled indices to randomize data reading. */
  private int[] indiceFleursMelanger;

  /** Array of mean values per feature dimension. */
  private double[] moyenne;

  /** Vector representing the global average of the input data. */
  private ArrayList<Double> vecteurMoyen;

  /** Main grid (matrix) of neurons used in the SOM. */
  private Neuron[][] matrice;

  /** Submatrix used during the training or classification phase. */
  private Neuron[][] sousMatrice;

  /** List of neurons with the shortest distance to input vectors (BMUs). */
  private ArrayList<Neuron> lesPlusPetitNoeud;


  /**
   * Constructs the Traitement object and processes the input file.
   * Parses each line as a Weight instance and normalizes the data.
   *
   * @param nomFichier the name of the CSV file containing the input data.
   */
  public Traitement(final String nomFichier) {
    this.normeFleurs = new ArrayList<Weight>();
    try {
      System.out.println("Traitement du fichier ...");
      final File fichier = new File(nomFichier);
      final InputStream st = new FileInputStream(fichier);
      final InputStreamReader str = new InputStreamReader(st);
      final BufferedReader bf = new BufferedReader(str);
      this.mesFleurs = new ArrayList<Weight>();
      String ligne;
      while ((ligne = bf.readLine()) != null) {
        final String[] tab = ligne.split(",");
        final Weight f = new Weight(tab);
        this.mesFleurs.add(f);
      }
      this.normalisation(this.mesFleurs, this.normeFleurs);
    }
    catch (Exception e) {
      System.out.println("erreur d'ouverture de fichier");
    }
  }


  /**
   * Gets the original (non-normalized) input data.
   *
   * @return the list of original Weight instances.
   */
  public ArrayList<Weight> getMesFleurs() {
    return this.mesFleurs;
  }

  /**
   * Gets the normalized version of the input data.
   *
   * @return the list of normalized Weight instances.
   */
  public ArrayList<Weight> getNormeFleurs() {
    return this.normeFleurs;
  }

  /**
   * Gets the randomized input data used during training.
   *
   * @return the list of Weight instances read in random order.
   */
  public ArrayList<Weight> getIntervalAleatoire() {
    return this.intervaleAleatoire;
  }

  /**
   * Gets the main SOM neuron matrix.
   *
   * @return the 2D array of Neuron objects.
   */
  public Neuron[][] getMatrice() {
    return this.matrice;
  }

  /**
   * Gets the submatrix used for specialized tasks.
   *
   * @return the 2D array of Neuron objects (subset of the main grid).
   */
  public Neuron[][] getSousMatrice() {
    return this.sousMatrice;
  }

  /**
   * Gets the list of neurons that are closest to input data (BMUs).
   *
   * @return the list of best matching neurons.
   */
  public ArrayList<Neuron> getListeNoeud() {
    return this.lesPlusPetitNoeud;
  }

  /**
   * Computes the average feature vector (mean vector) from the normalized data.
   *
   * <p>This method initializes the `moyenne` array to hold the sum of each feature dimension
   * across all normalized weights (`normeFleurs`). Then it calculates the average for each dimension
   * by dividing the sum by the number of data points. The result is stored in `vecteurMoyen`.</p>
   *
   * <p>The computed mean vector is also printed to the console.</p>
   */
  public void calculeVecteurMoyen() {
    this.moyenne = new double[this.normeFleurs.getFirst().getDimention().length];
    this.vecteurMoyen = new ArrayList<>(this.normeFleurs.getFirst().getDimention().length);
    for (final Weight e : this.normeFleurs) {
      for (int i = 0; i < e.getDimention().length; ++i) {
        final double[] dimention = e.getDimention();
        final double[] moyenne = this.moyenne;
        final int n = i;
        moyenne[n] += dimention[i];
      }
    }
    System.out.print("Le vecteur moyenne est : ");
    for (int j = 0; j < this.moyenne.length; ++j) {
      this.vecteurMoyen.add(this.moyenne[j] / this.mesFleurs.size());
      System.out.print(this.vecteurMoyen.get(j));
    }
    System.out.println();
  }

  /**
   * Creates an array of random weight vectors within specified intervals based on the mean vector.
   *
   * <p>This method generates random weight vectors within the range defined by the mean vector (`vecteurMoyen`)
   * and the specified upper (`borneSupp`) and lower (`borneInf`) bounds. The number of vectors is determined
   * by the formula `5 * sqrt(number of flowers)`, rounded to the nearest multiple of 10. Each vector is created
   * with the calculated limits for each dimension and added to the `intervaleAleatoire` list.</p>
   *
   * @param borneSupp the upper bound for the intervals around the mean vector
   * @param borneInf the lower bound for the intervals around the mean vector
   * @return a list of random `Weight` objects created within the specified intervals
   */
  public ArrayList<Weight> creerIntervale(final double borneSupp, final double borneInf) {
    this.intervaleAleatoire = new ArrayList<Weight>();
    final double[] limiteMax = new double[this.vecteurMoyen.size()];
    final double[] limiteMin = new double[this.vecteurMoyen.size()];
    for (int i = 0; i < limiteMax.length; ++i) {
      limiteMax[i] = this.vecteurMoyen.get(i) + borneSupp;
      limiteMin[i] = this.vecteurMoyen.get(i) - borneInf;
    }
    int nbVecteur = (int)(5.0 * Math.sqrt(this.mesFleurs.size()));
    nbVecteur -= nbVecteur % 10;
    for (int j = 0; j < nbVecteur; ++j) {
      final Weight f = new Weight(this.vecteurMoyen.size(), limiteMin, limiteMax);
      this.intervaleAleatoire.add(f);
    }
    return this.intervaleAleatoire;
  }


  /**
   * Prints the dimensions and labels of each weight vector in the provided interval list.
   *
   * <p>This method iterates over each `Weight` object in the provided `intervale` list, printing the
   * dimensions of each vector (separated by commas) followed by the corresponding label of the vector.</p>
   *
   * @param intervale the list of `Weight` objects whose dimensions and labels are to be printed
   */
  public void lectureIntervale(final ArrayList<Weight> intervale) {
    for (final Weight f : intervale) {
      for (int i = 0; i < f.getDimention().length; ++i) {
        System.out.print(f.getDimention()[i] + ",");
      }
      System.out.print(f.getLabel() + "\n");
    }
    System.out.println();
  }


  /**
   * Prints the dimensions and labels of each weight vector in the provided interval list, in a random order.
   *
   * <p>This method first randomizes the order of the input `intervale` list by shuffling the indices of
   * the elements. It then prints the dimensions of each `Weight` object (separated by commas) followed by
   * the corresponding label of the vector.</p>
   *
   * @param intervale the list of `Weight` objects whose dimensions and labels are to be printed in a random order
   */
  public void lectureIntervaleAleatoire(final ArrayList<Weight> intervale) {
    this.indiceFleursMelanger = new int[intervale.size()];
    for (int i = 0; i < intervale.size(); ++i) {
      this.indiceFleursMelanger[i] = i;
    }
    for (int j = 0; j < this.indiceFleursMelanger.length; ++j) {
      final int rand = (int)(Math.random() * this.indiceFleursMelanger.length);
      final int tmp = this.indiceFleursMelanger[j];
      this.indiceFleursMelanger[j] = this.indiceFleursMelanger[rand];
      this.indiceFleursMelanger[rand] = tmp;
    }
    for (int k = 0; k < intervale.size(); ++k) {
      final Weight f = intervale.get(this.indiceFleursMelanger[k]);
      for (int l = 0; l < f.getDimention().length; ++l) {
        System.out.print(f.getDimention()[l] + ",");
      }
      System.out.print(f.getLabel() + "\n");
    }
  }

  /**
   * Normalizes the dimensions of each `Weight` object in the provided list and stores the result in a new list.
   *
   * <p>This method calculates the Euclidean norm (magnitude) of each weight vector and normalizes its dimensions
   * by dividing each component by the norm. The normalized weight vector is then added to the result list.</p>
   *
   * @param maListe the list of `Weight` objects to be normalized
   * @param listeResult the list where the normalized `Weight` objects will be stored
   */
  public void normalisation(final ArrayList<Weight> maListe, final ArrayList<Weight> listeResult) {
    final double[] normeVecteur = new double[maListe.getFirst().getDimention().length];
    for (final Weight f : maListe) {
      double norme = 0.0;
      for (int i = 0; i < f.getDimention().length; ++i) {
        norme += Math.pow(f.getDimention()[i], 2.0);
      }
      norme = Math.sqrt(norme);
      for (int i = 0; i < f.getDimention().length; ++i) {
        normeVecteur[i] = f.getDimention()[i] / norme;
      }
      final Weight fleurNormaliser = new Weight(normeVecteur, "");
      listeResult.add(fleurNormaliser);
    }
  }

  /**
   * Generates a matrix of `Neuron` objects based on the random intervals.
   *
   * <p>This method creates a 2D matrix of `Neuron` objects, where each `Neuron` is initialized with a `Weight`
   * object from the random intervals. The matrix dimensions are based on the size of the random interval list, with
   * each row containing 10 neurons.</p>
   *
   * <p>The method assumes that the total number of random intervals is divisible by 10. The `Neuron` objects are
   * placed in the matrix in row-major order, and each `Neuron` is initialized with its corresponding `Weight`, a
   * default value for its additional parameter (0.0), and its position in the matrix.</p>
   *
   * @see Neuron
   * @see Weight
   */
  public void genereMatrice() {
    this.matrice = new Neuron[this.intervaleAleatoire.size() / 10][10];
    System.out.println("generation de la matrice de noeuds ...");
    int i = 0;
    for (int k = 0; k < this.intervaleAleatoire.size() / 10; ++k) {
      for (int l = 0; l < 10; ++l) {
        final Neuron n = new Neuron((Weight)this.intervaleAleatoire.get(i), 0.0, k, l);
        ++i;
        this.matrice[k][l] = n;
      }
    }
  }

  /**
   * Finds and stores the neurons with the smallest distance in a 2D array of `Neuron` objects.
   *
   * <p>This method iterates through the provided 2D array of `Neuron` objects and identifies the neuron with
   * the smallest distance value. It then adds all neurons with the same minimum distance to the `lesPlusPetitNoeud`
   * list. This list will contain all neurons that share the minimum distance value.</p>
   *
   * @param tab A 2D array of `Neuron` objects to be analyzed.
   * @see Neuron
   */
  public void plusPetit(final Neuron[][] tab) {
    this.lesPlusPetitNoeud = new ArrayList<Neuron>();
    Neuron min = tab[0][0];
    for (final Neuron[] n : tab) {
      Neuron[] array;
      for (int length2 = (array = n).length, j = 0; j < length2; ++j) {
        final Neuron e = array[j];
        if (e.getDistance() < min.getDistance()) {
          min = e;
        }
      }
    }
    this.lesPlusPetitNoeud.add(min);
    for (final Neuron[] n : tab) {
      Neuron[] array2;
      for (int length4 = (array2 = n).length, l = 0; l < length4; ++l) {
        final Neuron e = array2[l];
        if (e.getDistance() == min.getDistance()) {
          this.lesPlusPetitNoeud.add(e);
        }
      }
    }
  }

  /**
   * Generates a random index within the given list.
   *
   * <p>This method generates a random index between 0 and the size of the list (exclusive),
   * which can be used to select a random element from the provided list.</p>
   *
   * @param liste The list from which the random index will be generated.
   * @return A randomly generated index within the bounds of the list size.
   */
  public int getElementAleatoire(final ArrayList<Neuron> liste) {
    final int random = (int)(Math.random() * liste.size());
    return random;
  }

  /**
   * Calculates a submatrix of neurons based on the provided neuron and a specified neighborhood size.
   *
   * <p>This method iterates through the given matrix of neurons and selects the neurons that are within
   * a specified distance (defined by the "voisin" parameter) from the provided "plusPetit" neuron.
   * The selected neurons are used to form a submatrix. The submatrix is stored in the
   * `sousMatrice` field.</p>
   *
   * @param plusPetit The reference neuron used to define the neighborhood.
   * @param matrice The 2D array of neurons from which the submatrix will be selected.
   * @param voisin The distance used to define the neighborhood around the "plusPetit" neuron.
   */
  public void calculeSousMatrice(final Neuron plusPetit, final Neuron[][] matrice, final int voisin) {
    final ArrayList<Neuron> resultat = new ArrayList<Neuron>();
    for (final Neuron[] n : matrice) {
      Neuron[] array;
      for (int length2 = (array = n).length, n3 = 0; n3 < length2; ++n3) {
        final Neuron e = array[n3];
        if (e.getX() <= plusPetit.getX() + voisin && e.getX() >= plusPetit.getX() - voisin && e.getY() <= plusPetit.getY() + voisin && e.getY() >= plusPetit.getY() - voisin) {
          resultat.add(e);
        }
      }
    }
    int MaxX = resultat.getFirst().getX();
    int MaxY = resultat.getFirst().getY();
    int MinX = resultat.getFirst().getX();
    int MinY = resultat.getFirst().getY();
    for (int i = 1; i < resultat.size() - 1; ++i) {
      if (resultat.get(i).getX() < MinX) {
        MinX = resultat.get(i).getX();
      }
      if (resultat.get(i).getX() > MaxX) {
        MaxX = resultat.get(i).getX();
      }
      if (resultat.get(i).getY() < MinY) {
        MinY = resultat.get(i).getY();
      }
      if (resultat.get(i).getY() > MaxY) {
        MaxY = resultat.get(i).getY();
      }
    }
    this.sousMatrice = new Neuron[MaxX - MinX + 1][MaxY - MinY + 1];
    int k = 0;
    for (int j = 0; j < MaxX - MinX + 1; ++j) {
      for (int l = 0; l < MaxY - MinY + 1; ++l) {
        this.sousMatrice[j][l] = resultat.get(k);
        ++k;
      }
    }
  }

  /**
   * Prints the neurons in the given matrix with their coordinates and distance values.
   *
   * <p>This method iterates through the 2D array of neurons and prints the coordinates (X, Y)
   * and distance of each neuron in the matrix.</p>
   *
   * @param matrice The 2D array of neurons to be printed.
   */
  public void afficheMatrice(final Neuron[][] matrice) {
    for (final Neuron[] M : matrice) {
      Neuron[] array;
      for (int length2 = (array = M).length, j = 0; j < length2; ++j) {
        final Neuron e = array[j];
        System.out.print(e.getX() + " " + e.getY() + " " + e.getDistance() + "|");
      }
      System.out.println();
    }
  }

  /**
   * Performs a "clustering" or "convergence" process on a set of neurons using a form of
   * adjustment based on a random or fixed selection of neurons and their distances.
   *
   * <p>This method iterates through a series of steps designed to adjust the properties
   * (dimensions) of neurons in a matrix based on their Euclidean distance to other neurons,
   * gradually converging the neurons towards a defined goal. It dynamically adjusts the
   * calculation and selection of neurons over multiple iterations, with the alpha value
   * decreasing as the process progresses.</p>
   *
   * @param aleatoire An integer indicating whether to use a random selection of neurons
   *                  (1 for random, any other value for fixed selection).
   */
  public void rapprochement(final int aleatoire) {
    double val = 0.0;
    double multiplicateur = 0.7;
    double alpha = 0.7;
    final boolean phase = false;
    for (int j = 0; j < 5 * this.normeFleurs.size(); ++j) {
      for (int k = 0; k < this.normeFleurs.size(); ++k) {
        if (aleatoire == 1) {
          this.calculeDistanceEucliedienne(this.normeFleurs.get(this.indiceFleursMelanger[k]));
        }
        else {
          this.calculeDistanceEucliedienne(this.normeFleurs.get(k));
        }
        this.plusPetit(this.matrice);
        final int indicePetitNoeud = this.getElementAleatoire(this.lesPlusPetitNoeud);
        if (j < 0.2 * (5 * this.normeFleurs.size())) {
          if (j < 0.2 * (5 * this.normeFleurs.size()) / 3.0) {
            this.calculeSousMatrice(this.lesPlusPetitNoeud.get(indicePetitNoeud), this.getMatrice(), 3);
          }
          if (j >= 0.2 * (5 * this.normeFleurs.size()) / 3.0 && j < 2.0 * (0.2 * (5 * this.normeFleurs.size()) / 3.0)) {
            this.calculeSousMatrice(this.lesPlusPetitNoeud.get(indicePetitNoeud), this.getMatrice(), 2);
          }
          if (j >= 2.0 * (0.2 * (5 * this.normeFleurs.size()) / 3.0)) {
            this.calculeSousMatrice(this.lesPlusPetitNoeud.get(indicePetitNoeud), this.getMatrice(), 1);
          }
        }
        else {
          multiplicateur = 0.007;
          if (j < 0 * (5 * this.normeFleurs.size() / 2)) {
            this.calculeSousMatrice(this.lesPlusPetitNoeud.get(indicePetitNoeud), this.getMatrice(), 2);
          }
          if (j >= 0 * (5 * this.normeFleurs.size() / 2)) {
            this.calculeSousMatrice(this.lesPlusPetitNoeud.get(indicePetitNoeud), this.getMatrice(), 1);
          }
        }
        Neuron[][] sousMatrice;
        for (int length = (sousMatrice = this.sousMatrice).length, l = 0; l < length; ++l) {
          final Neuron[] sousM = sousMatrice[l];
          Neuron[] array;
          for (int length2 = (array = sousM).length, n = 0; n < length2; ++n) {
            final Neuron e = array[n];
            for (int i = 0; i < e.getW().getDimention().length; ++i) {
              if (aleatoire == 1) {
                val = e.getW().getDimention()[i] + alpha * (this.normeFleurs.get(this.indiceFleursMelanger[k]).getDimention()[i] - e.getW().getDimention()[i]);
              }
              else {
                val = e.getW().getDimention()[i] + alpha * (this.normeFleurs.get(k).getDimention()[i] - e.getW().getDimention()[i]);
              }
              e.getW().setDimention(val, i);
            }
          }
        }
        this.fusionnerMatrice();
      }
      if ((!phase && alpha > 0.07) || (phase && alpha > 7.0E-5)) {
        alpha = multiplicateur * (1 - j / (5 * this.normeFleurs.size()));
      }
    }
  }

  /**
   * Calculates and updates the Euclidean distance between the given weight and the
   * weights of all neurons in the matrix.
   *
   * <p>This method iterates through each neuron in the matrix and computes the Euclidean
   * distance between the weight of the neuron and the provided weight. The distance is
   * calculated as the square root of the sum of squared differences between corresponding
   * dimensions of the weight vectors. The calculated distance is then updated for each neuron.</p>
   *
   * @param f The weight object to which the Euclidean distance is calculated. The method
   *          compares each neuron's weight with this one.
   */
  public void calculeDistanceEucliedienne(final Weight f) {
    Neuron[][] matrice;
    for (int length = (matrice = this.matrice).length, i = 0; i < length; ++i) {
      final Neuron[] n = matrice[i];
      Neuron[] array;
      for (int length2 = (array = n).length, k = 0; k < length2; ++k) {
        final Neuron e = array[k];
        double newDistance = 0.0;
        for (int j = 0; j < e.getW().getDimention().length; ++j) {
          newDistance += Math.pow(e.getW().getDimention()[j] - f.getDimention()[j], 2.0);
        }
        newDistance = Math.sqrt(newDistance);
        e.setDistance(newDistance);
      }
    }
  }

  /**
   * Calculates and updates the inverse Euclidean distance for each neuron in the matrix
   * based on the weight vectors of the flowers, and assigns the corresponding label
   * to each neuron.
   *
   * <p>This method computes the Euclidean distance between the weight vector of each neuron
   * in the matrix and the weight vectors of all flowers. The neuron is then assigned the label
   * of the flower that is closest to it based on the inverse Euclidean distance. The
   * distance is calculated as the square root of the sum of squared differences between
   * corresponding dimensions of the weight vectors.</p>
   *
   * @see #plusPetiteDistance(double[])
   */
  public void distanceEuclidienneInverse() {
    final double[] lesDistance = new double[this.normeFleurs.size()];
    double distance = 0.0;
    Neuron[][] matrice;
    for (int length = (matrice = this.matrice).length, k = 0; k < length; ++k) {
      final Neuron[] n = matrice[k];
      Neuron[] array;
      for (int length2 = (array = n).length, l = 0; l < length2; ++l) {
        final Neuron e = array[l];
        for (int i = 0; i < this.normeFleurs.size(); ++i) {
          for (int j = 0; j < e.getW().getDimention().length; ++j) {
            distance += Math.pow(e.getW().getDimention()[j] - this.normeFleurs.get(i).getDimention()[j], 2.0);
          }
          lesDistance[i] = Math.sqrt(distance);
          distance = 0.0;
        }
        e.setEtiquette(this.mesFleurs.get(this.plusPetiteDistance(lesDistance)).getLabel());
      }
    }
  }

  /**
   * Returns the index of the smallest value in the given array.
   *
   * <p>This method searches for the smallest value in the provided array
   * and returns the index of that value.</p>
   *
   * @param tab the array of distances (or values) to search through
   * @return the index of the smallest value in the array
   */
  public int plusPetiteDistance(final double[] tab) {
    double min = tab[0];
    int position = 0;
    for (int i = 1; i < tab.length - 1; ++i) {
      if (tab[i] < min) {
        min = tab[i];
        position = i;
      }
    }
    return position;
  }

  /**
   * Prints the details of each neuron in the matrix to the console.
   *
   * <p>This method iterates over the neuron matrix and prints each neuron's
   * string representation, separated by tabs. Each row of the matrix is printed
   * on a new line.</p>
   */
  public void afficherNoeud() {
    Neuron[][] matrice;
    for (int length = (matrice = this.matrice).length, i = 0; i < length; ++i) {
      final Neuron[] n = matrice[i];
      Neuron[] array;
      for (int length2 = (array = n).length, j = 0; j < length2; ++j) {
        final Neuron e = array[j];
        System.out.print(String.valueOf(e.toString()) + " \t");
      }
      System.out.print("\n");
    }
  }

  /**
   * Merges the sub-matrix into the main matrix at the corresponding positions.
   *
   * <p>This method iterates over the sub-matrix and the main matrix, and updates
   * the neurons in the main matrix with those from the sub-matrix based on matching
   * coordinates (X and Y).</p>
   */
  public void fusionnerMatrice() {
    for (int i = 0; i < this.matrice.length; ++i) {
      for (int j = 0; j < this.matrice.length; ++j) {
        Neuron[][] sousMatrice;
        for (int length = (sousMatrice = this.sousMatrice).length, k = 0; k < length; ++k) {
          final Neuron[] n = sousMatrice[k];
          Neuron[] array;
          for (int length2 = (array = n).length, l = 0; l < length2; ++l) {
            final Neuron e = array[l];
            if (i == e.getX() && j == e.getY()) {
              this.matrice[i][j] = e;
            }
          }
        }
      }
    }
  }

  /**
   * Renames the flower labels in the neuron matrix.
   *
   * <p>This method assigns a new label (a character starting from 'a') to each unique
   * flower label found in the matrix. It ensures that each flower label is replaced
   * by a corresponding character.</p>
   *
   * <p>First, it collects all unique flower labels in the matrix and then assigns
   * a new label to each flower, starting from 'a' and incrementing sequentially.</p>
   */
  public void rename() {
    final ArrayList<String> nomFleur = new ArrayList<String>();
    final char nom = 'a';
    Neuron[][] matrice;
    for (int length = (matrice = this.matrice).length, k = 0; k < length; ++k) {
      final Neuron[] n = matrice[k];
      Neuron[] array;
      for (int length2 = (array = n).length, l = 0; l < length2; ++l) {
        final Neuron e = array[l];
        if (!nomFleur.contains(e.getEtiquette())) {
          nomFleur.add(e.getEtiquette());
        }
      }
    }
    for (int j = 0; j < nomFleur.size(); ++j) {
      final String a = Character.toString((char)(nom + j));
      System.out.print(nomFleur.get(j) + " = " + a + "\t");
    }
    System.out.println();
    for (int i = 0; i < nomFleur.size(); ++i) {
      Neuron[][] matrice2;
      for (int length3 = (matrice2 = this.matrice).length, n3 = 0; n3 < length3; ++n3) {
        final Neuron[] n2 = matrice2[n3];
        Neuron[] array2;
        for (int length4 = (array2 = n2).length, n4 = 0; n4 < length4; ++n4) {
          final Neuron e2 = array2[n4];
          if (nomFleur.get(i).equals(e2.getEtiquette())) {
            e2.setEtiquette(Character.toString((char)(nom + i)));
          }
        }
      }
    }
  }

  /**
   * Counts the occurrences of each flower label in the neuron matrix.
   *
   * <p>This method counts how many times each unique flower label appears in the
   * matrix and prints the label along with its count. The result is printed in the
   * format: "label = count".</p>
   *
   * <p>It first collects all unique flower labels from the matrix, then counts the
   * number of occurrences of each label and outputs the counts.</p>
   */
  public void compteFleur() {
    final ArrayList<String> nom = new ArrayList<String>();
    Neuron[][] matrice;
    for (int length = (matrice = this.matrice).length, k = 0; k < length; ++k) {
      final Neuron[] n = matrice[k];
      Neuron[] array;
      for (int length2 = (array = n).length, l = 0; l < length2; ++l) {
        final Neuron e = array[l];
        if (!nom.contains(e.getEtiquette())) {
          nom.add(e.getEtiquette());
        }
      }
    }
    final int[] tab = new int[nom.size()];
    for (int i = 0; i < nom.size(); ++i) {
      Neuron[][] matrice2;
      for (int length3 = (matrice2 = this.matrice).length, n3 = 0; n3 < length3; ++n3) {
        final Neuron[] n2 = matrice2[n3];
        Neuron[] array2;
        for (int length4 = (array2 = n2).length, n4 = 0; n4 < length4; ++n4) {
          final Neuron e2 = array2[n4];
          if (e2.getEtiquette().equals(nom.get(i))) {
            final int[] array3 = tab;
            final int n5 = i;
            ++array3[n5];
          }
        }
      }
    }
    for (int j = 0; j < tab.length; ++j) {
      System.out.print(nom.get(j) + " = " + tab[j] + "\t");
    }
  }
}
