<h1> Self Organizing Map </h1>


## 🌍 Multilingual README Versions

- 🇫🇷 [Français](./README.fr.md)
- 🇬🇧 [English (you are here)](#)
- 🇪🇸 [Español](./README.es.md)

## 📘 Project Overview

This project is a simplified implementation of a **Self-Organizing Map (SOM)** developed in **Bachelor's 3** at **Université Paris 8**. A **C** version of the project is also available. You can access the C version's repository [here](https://github.com/Fab16BSB/SOM_C). The main objective was to understand the workings of the SOM algorithm by recreating a *from scratch* version with the following constraints:

- **No global variables** used.  
- **BMUs (Best Matching Units)** are stored in a **linked list**.  
- The size of collections: matrices and linked lists must be **calculated dynamically**.  

---

### 📊 Dataset

The dataset used is the **Iris dataset**, available on Kaggle:  
[Iris Dataset](https://www.kaggle.com/uciml/iris)

- **150 samples**  
- **4 features** per sample:  
  - Sepal length  
  - Sepal width  
  - Petal length  
  - Petal width  
- **3 classes**:  
  1. Iris-setosa  
  2. Iris-versicolor  
  3. Iris-virginica  

---

### ⚙️ SOM Algorithm Functioning

The **Self-Organizing Map (SOM)** is an unsupervised neural network used for dimensionality reduction and data visualization. Here are the main steps:

1. **Initialization**  
   The neurons in the map are randomly initialized in the feature space.

2. **Distance Calculation**  
   For each input data, the Euclidean distance between this data and all neurons is calculated.  
   The neuron with the smallest distance is called the **BMU** (Best Matching Unit). This process follows the **Winner-Takes-All** (WTA) principle, where only the neuron closest to the input data is selected, along with its neighbors.

3. **Map Update**  
   The BMU and its neighbors are adjusted to move closer to the input data, according to a learning rate α. This gradually adjusts the map to better represent the input data.

4. **Iteration**  
   Steps 2 to 3 are repeated for a set number of iterations.  
   - The learning rate **decreases** over time.  
   - The **neighborhood size** gradually shrinks, allowing the map to specialize while maintaining topological consistency.

The result is a topologically organized map, where similar classes are found in close areas. The SOM algorithm thus groups similar data while preserving their structure.

---

### 🧑‍💻 Technologies Used

- **Language:** Java (from scratch implementation)  

---

### 📝 Compilation and Execution

1. **Clone the repository**

   ```bash
   git clone https://github.com/Fab16BSB/SOM_JAVA.git
   cd SOM_JAVA
   ````
2. **Compilation**

   ```bash
   javac *.java
   ```

3. **Execution**

   ```bash
   java Lancement
   ````

### 🙌 Acknowledgements
I would like to thank my instructor for offering me this project and for his valuable advice.

