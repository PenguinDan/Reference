# Why Constraint Layout

## How Android Draws Views
Three phases:
1. Measure: <br>
The system does a top-down traversal of the tree to determine how large each ViewGroup and View element should be. When a **ViewGroup** is measured, it also measures its children.
2. Layout: <br>
Each **ViewGroup** determines the positions of its children using the sizes determined in the measure phase.
3. Draw: <br>
Another top-down traversal is done. For each object in the view tree, a Canvas object is created to sent a list of drawing commands to the GPU. These commands include the **ViewGroup** and **View** objects' sizes and positions, which the system determined during the previous 2 phases.