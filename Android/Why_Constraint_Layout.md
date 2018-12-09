# Why Constraint Layout

## How Android Draws Views
Three phases:
1. Measure: <br>
The system does a top-down traversal of the tree to determine how large each ViewGroup and View element should be. When a **ViewGroup** is measured, it also measures its children.
2. Layout: <br>
3. Draw: <br>