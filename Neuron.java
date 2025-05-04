/**
 * Represents a neuron (node) in the Self-Organizing Map (SOM).
 * Each node holds a reference flower (Fleur), its coordinates in the map,
 * the distance to an input vector, and an optional label (etiquette).
 *
 *  * @author Fab.16
 */
public class Neuron {

  /** Reference flower vector associated with this node. */
  private Weight w;

  /** Label assigned to this node, typically the class of the closest input. */
  private String classe;

  /** Distance between the input vector and this node. */
  private double distance;

  /** X-coordinate of this node in the SOM grid. */
  private int x;

  /** Y-coordinate of this node in the SOM grid. */
  private int y;

  /**
   * Constructs a node with the given reference vector, distance, and coordinates.
   *
   * @param f the reference flower vector (Fleur) for this node
   * @param distance the distance between this node and an input vector
   * @param x the x-coordinate of the node
   * @param y the y-coordinate of the node
   */
  public Neuron(Weight f, double distance, int x, int y) {
    w = f;
    classe = "";
    this.distance = distance;
    this.x = x;
    this.y = y;
  }

  /**
   * Returns a string representation of the node.
   *
   * @return the label (etiquette) enclosed in brackets
   */
  public String toString() {
    return  "[" + classe + "]";
  }

  /**
   * Returns the distance between this node and the current input vector.
   *
   * @return the distance value
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Returns the x-coordinate of the node.
   *
   * @return the x position
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the node.
   *
   * @return the y position
   */
  public int getY() {
    return y;
  }

  /**
   * Returns the reference vector (Fleur) associated with this node.
   *
   * @return the reference vector
   */
  public Weight getW() {
    return w;
  }

  /**
   * Returns the label assigned to this node.
   *
   * @return the node's label
   */
  public String getEtiquette() {
    return classe;
  }

  /**
   * Sets the distance between this node and the current input vector.
   *
   * @param distance the new distance value
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Sets the label of the node.
   *
   * @param name the new label to assign
   */
  public void setEtiquette(String name) {
    classe = name;
  }
}
