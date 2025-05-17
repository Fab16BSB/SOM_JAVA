package code;

/**
 * Represents a data point with multiple numerical dimensions and an optional label.
 * Can be initialized from string input, random values, or a predefined vector.
 *
 * Used for training and evaluating Self-Organizing Maps (SOM).
 *
 * @author Fab.16
 */
public class Weight {

  /** Numerical features of the data point. */
  private double[] dimention;

  /** Label or name associated with the data point. */
  private String label;

  /**
   * Constructs a Data object from an array of strings, where all but the last
   * element are numerical values, and the last element is the label.
   *
   * @param info array of strings containing the feature values and label
   */
  public Weight(String[] info) {
    dimention = new double[info.length - 1];
    for (int i = 0; i < info.length; i++) {
      if (i == info.length - 1) {
        label = info[i];
      } else {
        dimention[i] = Double.parseDouble(info[i]);
      }
    }
  }

  /**
   * Constructs a Data object with random values within specified min and max bounds.
   *
   * @param taille number of dimensions
   * @param min minimum values for each dimension
   * @param max maximum values for each dimension
   */
  public Weight(int taille, double[] min, double[] max) {
    label = "";
    dimention = new double[taille];
    for (int i = 0; i < taille; i++) {
      dimention[i] = ((1.0D - Math.random()) * (max[i] - min[i]) + min[i]);
    }
  }

  /**
   * Constructs a Data object from a given array of values and a label.
   *
   * @param caracteristique array of feature values
   * @param nom the label or name of the data point
   */
  public Weight(double[] caracteristique, String nom) {
    dimention = new double[caracteristique.length];
    for (int i = 0; i < dimention.length; i++) {
      dimention[i] = caracteristique[i];
    }
    this.label = nom;
  }

  /**
   * Returns the array of feature values.
   *
   * @return the dimensions of the data point
   */
  public double[] getDimention() {
    return dimention;
  }

  /**
   * Returns the label or name of the data point.
   *
   * @return the name associated with this data
   */
  public String getLabel() {
    return label;
  }

  /**
   * Updates the value of a specific dimension.
   *
   * @param val the new value to set
   * @param position the index of the dimension to update
   */
  public void setDimention(double val, int position) {
    dimention[position] = val;
  }
}
