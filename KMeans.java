import java.util.ArrayList;
import java.lang.Math;
/**
 * A k-means clustering algorithm implementation.
 * 
 */

public class KMeans {

    public KMeansResult cluster(double[][] centroids, 
				double[][] instances, double threshold) {
	/* ... YOUR CODE GOES HERE ... */

	boolean thresholdMet = false; // Has the threhold been met?
	int iteration = 0; // Current iteration of KMeans
	KMeansResult results = new KMeansResult(); // KMeans return
	
	/* Initialize return values */
	results.centroids = new double[centroids.length][centroids[0].length];
	ArrayList<Double> distortionIterations = new ArrayList<Double>();
	results.clusterAssignment = new int[instances.length];
       
	while (!thresholdMet) {

	    /* 
	       In each iteration, you should first reallocate the clusters for
	       each instance...
	    */

	    /* Loop through instance rows */
	    for (int i = 0; i < instances.length; i++) {
		double distance;
		distance = 0;
		double[] distanceList;
		distanceList = new double[centroids.length];

		/* Loop through centroids rows */
		for (int j = 0; j < centroids.length; j++) {
		    
		    /* Loop through instance columns */
		    for (int k = 0; k < instances[i].length; k++) {
		    
			/* 
			   Get distance by Euclidean distance across 
			   dimension(s) 
			*/
			distance += Math.pow((instances[i][k] - 
					      centroids[j][k]), 2);
		    }

		    /* Distance to centroid mean at this instance row */
		    distanceList[j] = Math.sqrt(distance);
		}

		/* Retrieve smallest distance for this instance */
		double smallest_distance = distanceList[0];
		
		int index = 0;

		/* 
		   Loop through all distances in distanceList and find 
		   smallest distance 
		*/
		for (int j = 1; j < distanceList.length; j++) {
		    
		    double temp = distanceList[j];
		    if (temp < smallest_distance) {
			smallest_distance = temp;
			index = j;
		    }
		}
		
		/* cluserAssignment index is match for match with instances row */
		results.clusterAssignment[i] = index;
	    }

	    /* 
	       ...It is possible that, in some iteration after reallocation, 
	       there is a centroid c
	       which does not match to any instances. In this case, we call
	       the centroid of this cluster an orphaned centroid. This is a
	       problem, because you would have no instances assigned to the
	       centroid to average over to determine the centroids new
	       location... 
	    */

	    boolean orphanedCentroid = false;
	    boolean repeat = false;
	    int thisOrphanedCentroid = 0;
	    
	    while (!repeat) {
		
		orphanedCentroid = false;

		/* Checking for orphaned centroids */
		for (int i = 0; i < centroids.length; i++) {

		    for (int j = 0; 
			 j < results.clusterAssignment.length; j++) {
		    
			/* Found a centroid assignment...life is good */
			if (results.clusterAssignment[j] == i) {
			    orphanedCentroid = false;
			    break;
			}
		    
			/* If no centroid assignment ever, remains true */
			orphanedCentroid = true;
		    }
		
		    if (orphanedCentroid == true)
			thisOrphanedCentroid = i;
		}
	    
		/*
		  ...To solve this, we specify you to implement the following
		  behaviors in this scenario:
		*/
		if (orphanedCentroid == true) {
		    
		    /* 
		       1.  Search among all the instances for the instance
		       x whose distance is farthest from its assigned centroid
		    */
		    double maxDistance = 0.0;
		    int instance = 0;
		    
		    /* Loop over all instance rows */
		    for (int j = 0; j < instances.length; j++) {
			
			double distance;
			distance = 0.0;
			    
			/* Loop over all instances columns */
			for (int x = 0; x < instances[j].length; x++) {
				
			    distance += Math.pow((instances[j][x] -
						  centroids[results.
							    clusterAssignment[j]][x]), 2);
			}
			    
			distance = Math.sqrt(distance);
			    
			    
			if (distance > maxDistance) {
			    maxDistance = distance;
			    instance = j;
			}
		    }

		    /* 
		       2. Choose x's position as the position of c, the
		       orphaned centroid
		    */
		    for (int j = 0; j < instances[instance].length; j++) {
			
			centroids[thisOrphanedCentroid][j] =
			    instances[instance][j];
		    }
		    
		    /* 
		       3. Reallocate again the cluster assignments for all
		       x.
		    */
		    results.clusterAssignment[instance] = thisOrphanedCentroid;
		    
		    /* 
		       4. Check if there is still an orphaned centroid.
		       If so, repeat step 1 to 4 until all orphaned centroids
		       have been removed.
		    */
		    // Loop back essentially
		}

		/* No orphaned centroid */
		else
		    break;
	    }

	    /* 
	       Once you have removed all possible orphaned centroids,
	       you should update the centroids coordinates by averaging
	       over all the instances assigned to it
	    */
	    double averageDimensionX = 0.0;
	    int width = 0;

	    /* Loop instances */
	    for (int j = 0; j < centroids.length; j++) {
		while (width < instances[0].length) {
		    for (int i = 0; i < instances.length; i++) {
			
			if (results.clusterAssignment[i] != j)
			    continue;
			else {
			    averageDimensionX += instances[i][width];
			}
		    }
		    centroids[j][width] = averageDimensionX;
		    averageDimensionX = 0.0;
		    width++;
		}
	    }

	    /* 
	       After that, you are also required to calculate the
	       distortion of an iteration, and store it into the 
	       array distortionIterations.
	    */
	    
	    double distortion = 0.0;
	    for (int i = 0; i < instances.length; i++) {

		for (int j = 0; j < centroids.length; j++) {
		    
		    for (int k = 0; k < instances[i].length; k++) {
			
			distortion += Math.pow((instances[i][k]
						- centroids[j][k]), 2);
		    }
		}
	    }
	    
	    distortionIterations.add(iteration, distortion);
	    
	    if (distortionIterations.size() > 1) {

		if (Math.abs(((distortionIterations.get(iteration) - 
			       distortionIterations.get(iteration - 1)))
			     / distortionIterations.get(iteration - 1)) 
		    < threshold) {
		    thresholdMet = true;
		}
	    }

	    iteration++;
	}
	
	results.centroids = centroids;
	results.distortionIterations = new double[distortionIterations.size()];

	for (int i = 0; i < distortionIterations.size(); i++)
	    results.distortionIterations[i] = distortionIterations.get(i);

	}
	return results;
    }
   
}
