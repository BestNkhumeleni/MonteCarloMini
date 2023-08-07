package MonteCarloMini;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MonteCarloMinimizationParallel extends RecursiveTask<Found> {
   static final boolean DEBUG = false;
   static long startTime = 0;
   static long endTime = 0;
   int length;
   TerrainArea terrain;
   int rows, columns;
   int id = 0;
   Random rand = new Random();
   int THRESHOLD = 2500;

   public MonteCarloMinimizationParallel(int length, TerrainArea terrain, int rows, int columns) {
      this.length = length;
      this.terrain = terrain;
      this.rows = rows;
      this.columns = columns;
   }

   protected Found compute() {
      if (length <= THRESHOLD) {
         SearchParallel search = new SearchParallel(id++, rand.nextInt(rows), rand.nextInt(columns), terrain);
         int minHeight = search.find_valleys();
         return new Found(search, minHeight);
      } else {
         int newLength = (int) length / 2;
         MonteCarloMinimizationParallel left = new MonteCarloMinimizationParallel(newLength, terrain, rows, columns);
         MonteCarloMinimizationParallel right = new MonteCarloMinimizationParallel(length - newLength, terrain, rows,
               columns);
         left.fork();
         Found rightResult = right.compute();
         Found leftResult = left.join();
         if (leftResult.minimum <= rightResult.minimum) {
            return leftResult;
         } else {
            return rightResult;
         }
      }
   }

   private static void tick() {
      startTime = System.currentTimeMillis();
   }

   private static void tock() {
      endTime = System.currentTimeMillis();
   }

   public static void main(String[] args) {
      int rows, columns; // grid size
      double xmin, xmax, ymin, ymax; // x and y terrain limits
      TerrainArea terrain; // object to store the heights and grid points visited by searches
      double searches_density; // Density - number of Monte Carlo searches per grid position - usually less
                               // than 1!
      int num_searches; // Number of searches
      Random rand = new Random(); // the random number generator
      if (args.length != 7) {
         System.out.println("Incorrect number of command line arguments provided.");
         if (args.length < 7) {
            System.out.println("add " + (7 - args.length) + "more argument(s)");
         } else {
            System.out.println("remove " + (args.length - 7) + " argument(s)");
         }
         System.exit(0);
      }
      /* Read argument values */
      rows = Integer.parseInt(args[0]);
      columns = Integer.parseInt(args[1]);
      xmin = Double.parseDouble(args[2]);
      xmax = Double.parseDouble(args[3]);
      ymin = Double.parseDouble(args[4]);
      ymax = Double.parseDouble(args[5]);
      searches_density = Double.parseDouble(args[6]);
      terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
      num_searches = (int) (rows * columns * searches_density);

      tick();
      MonteCarloMinimizationParallel first = new MonteCarloMinimizationParallel(num_searches, terrain, rows, columns);
      ForkJoinPool pool = new ForkJoinPool(); // the pool of worker threads

      Found result = pool.invoke(first); // start everything running - give the task to the pool
      tock();

      if (DEBUG) {
         /* print final state */
         terrain.print_heights();
         terrain.print_visited();
      }

      System.out.printf("Run parameters\n");
      System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
      System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax);
      System.out.printf("\t Search density: %f (%d searches)\n", searches_density, num_searches);

      /* Total computation time */
      System.out.printf("Time: %d ms\n", endTime - startTime);
      int tmp = terrain.getGrid_points_visited();
      System.out.printf("Grid points visited: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0, "%");
      tmp = terrain.getGrid_points_evaluated();
      System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0, "%");

      /* Results */
      System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", result.minimum,
            terrain.getXcoord(result.location.getPos_row()), terrain.getYcoord(result.location.getPos_col()));
   }
}

class Found {
   SearchParallel location;
   int minimum;

   public Found(SearchParallel location, int minimum) {
      this.location = location;
      this.minimum = minimum;
   }
}