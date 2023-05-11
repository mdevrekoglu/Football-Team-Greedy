import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Mehmet_Devrekoglu_2020510028
 */
public class Mehmet_Devrekoglu_2020510028 {

    // A dynamic programming approach to find optimal solution
    public static int dp(int n, int p, int c, int[] playerSalaries, int[] playerDemands) {

        // A cumilative array for the remaning players
        // It makes very faster to check if you can take a player from the previous year
        int[] remaningPlayer = new int[n + 1];

        // To find maximum number of players you can take from the previous year
        // It is called min because value is negative at the beginning
        // We will multiply it with -1 at the end
        int min = Integer.MAX_VALUE;

        // To find optimal solution we assign current year's cost due to change of limit
        double[][] arr;

        // Calculating cumilative array
        for (int i = 1; i < remaningPlayer.length; i++) {
            int difference = p - playerDemands[i];
            
            if(difference < 0 && remaningPlayer[i - 1] == 0){
                remaningPlayer[i] = 0;
            }else if(difference < 0 && remaningPlayer[i - 1] != 0){
                if(difference * -1 > remaningPlayer[i - 1]){
                    remaningPlayer[i] = 0;
                }else{
                    remaningPlayer[i] = remaningPlayer[i - 1] + difference;
                }
            }else if(difference == 0){
                remaningPlayer[i] = remaningPlayer[i - 1];
            }else if(difference > 0){
                remaningPlayer[i] = remaningPlayer[i - 1] + difference;
            }

            // Finding minimum value of remaning players
            if(difference < min){
                min = difference;
            }

            // For debugging
            // System.out.printf("(%d) Remaning players by years: (%d)\n", i, (remaningPlayer[i]));
        }

        // For debugging
        /* 
        for (int i = 1; i <= n; i++) {
            System.out.printf("(%d) difference between produced and demands: (%d)\n", i, (p - playerDemands[i]));
        }
        System.out.println();
        */
        
        // If minimum value is negative, it means that your problem includes negative values
        // Which means you have to take players from the previous year or you have to train new players
        if(min < 0){
            min *= -1;
        }else{ // If not you don't have to take players from the previous year or you don't have to train new players
            min = 0;
        }
        
        // n-> number of years wanted to be planned
        // p-> number of players you raise in a year
        // c-> cost of a coach for a year
        // playerSalaries-> array of salaries of players for a year for each number of players
        // playerDemands-> array of demands of players for each year
        arr = new double[min + 2][n + 1]; // +1 for 0th year, +1 for 0 players (min)
        
        for(int i = 1; i <= min + 1; i++){
            int[] remaningPlayerCopy = remaningPlayer.clone();
            for (int j = n; j >= 1; j--) {
                
                // Difference between produced and demands
                int difference = p - playerDemands[j];

                // If difference is positive or zero, it means that you have more players than you need
                // So you can take the cost of the previous year
                if(difference >= 0){
                    arr[i][j] = arr[i][(j != n) ? j + 1 : j];
                }
                // if difference is negative, it means that you have less players than you need
                // And there is two options, hiring player from previous year or hiring new coachs to train player
                else{ 
                    
                    // There are three limits for the difference
                    // It can take maximum i-1 players from the previous year
                    // It can take maximum the difference between produced and demands
                    // It can take maximum the remaning players from the previous year
                    double cost = 0;

                    // Find the limit
                    int limit = Math.min(Math.min(i - 1, difference * -1), remaningPlayerCopy[j - 1]);
                    // Find the first index which is '0'
                    int index = findIndex(remaningPlayerCopy, j - 1);

                    // For debugging
                    // System.out.println("Limit: " + limit);

                    // If next year taken players, you must take what is left from the previous year
                    // Because holding one more year is more expensive than taking a player from the previous year
                    // So it must be more efficient to take a player from the previous year
                    // Therefore limit rule is not applied in this part
                    if(remaningPlayerCopy[(j != n) ? j + 1 : j] < 0){
                        
                        for (int k = index; k < j; k++) {
                            if(remaningPlayerCopy[k] +  remaningPlayerCopy[(j != n) ? j + 1 : j] >= difference * -1){
                                cost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) (difference * -1) / (double) remaningPlayerCopy[k];
                            }else if(remaningPlayerCopy[k] + remaningPlayerCopy[(j != n) ? j + 1 : j] > 0
                                 && remaningPlayerCopy[k] + remaningPlayerCopy[(j != n) ? j + 1 : j] < difference * -1){
                                cost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) (remaningPlayerCopy[k] + remaningPlayerCopy[(j != n) ? j + 1 : j]) / (double) remaningPlayerCopy[k];
                            }
                        }

                        // Assign the cost
                        arr[i][j] = arr[i][(j != n) ? j + 1 : j] + cost;

                        // Assign total number of players taken from the previous years
                        remaningPlayerCopy[j] = difference + remaningPlayerCopy[(j != n) ? j + 1 : j];

                        // Print how many players taken from the previous years and how many coaches hired
                        if(i == min + 1){
                            System.out.printf("(%d) %d Coachs hired, %d Players taken from the previous years\n", j
                            , difference * -1 - remaningPlayerCopy[j - 1], remaningPlayerCopy[j - 1]);
                        }

                        // Dont need to check the rest of the code
                        continue;
                    }
                    
                    
                    int num = -1;
                    // If the limit is 0,
                    // it means that you can't take any player from the previous year
                    if(limit == i - 1){
                        num = remaningPlayerCopy[j - 1] - limit;
                    }
                    // If the limit is difference * -1, it means that
                    // You can maximum take diffrence * -1 players from the previous year
                    else if(limit == difference * -1){
                        num = remaningPlayerCopy[j - 1] - limit;
                    }
                    // If the limit is remaning players from the previous year,
                    // It means that you can maximum take remaning players from the previous year
                    else if(limit == remaningPlayerCopy[j - 1]){
                        num = remaningPlayerCopy[j - 1] - limit;
                    }

                    // Remove the player that will not be taken from the remaning players
                    for(int k = index; k < j; k++){
                        if(remaningPlayerCopy[k] <= num){
                            remaningPlayerCopy[k] = 0;
                        }else{
                            remaningPlayerCopy[k] -= num;
                        }
                    }

                    // Calculate the cost for this year
                    cost += ((difference * -1) - remaningPlayerCopy[j - 1]) * c;
                    for(int k = index; k < j; k++){
                        if(remaningPlayerCopy[k] <= remaningPlayerCopy[j - 1]){
                            cost += (double) playerSalaries[remaningPlayerCopy[k]];
                        }else{
                            cost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) remaningPlayerCopy[j - 1] / (double) remaningPlayerCopy[k];
                        }
                    }

                    // Calculate the cost for the previous years
                    double previousYearsCost = arr[i - 1][j] - arr[i - 1][(j != n) ? j + 1 : 0];

                    // Print how many players taken from the previous years and how many coaches hired
                    if(i == min + 1){
                        System.out.printf("(%d) %d Coachs hired, %d Players taken from the previous years\n", j
                        , difference * -1 - remaningPlayerCopy[j - 1], remaningPlayerCopy[j - 1]);
                    }

                    // If the limit is 0 cost has to be calculated value
                    if(i == 1){
                        arr[i][j] = cost + arr[i][(j != n) ? j + 1 : j];
                    }else if(cost <= previousYearsCost){ // cost < arr[i - 1][j] && remaningPlayerCopy[j - 1] != 0
                        
                        // Assign total cost
                        arr[i][j] = cost + arr[i][(j != n) ? j + 1 : j];
                        
                        // Assign negative value to the remaning players to check if it is taken from the previous year for the next years
                        if(remaningPlayerCopy[j - 1] != 0){
                            remaningPlayerCopy[j] = remaningPlayerCopy[j - 1] * -1;
                        }
                                              
                    }else{

                        // If previous calculated value is smaller than the calculated value
                        // It means that you have to take the previous value
                        arr[i][j] = previousYearsCost + arr[i][(j != n) ? j + 1 : j];

                        // Remove the player that will not be taken from the remaning players
                        for(int k = index; k < j; k++){
                            if(remaningPlayerCopy[k] <= remaningPlayerCopy[j - 1]){
                                remaningPlayerCopy[k] = 0;
                            }else{
                                remaningPlayerCopy[k] -= remaningPlayerCopy[j - 1];
                            }
                        }
                    }
                }
            }
        }

        // For debugging
        // printInt2Arr(arr);

        // Return left bottom value
        // Because algorithm works top to bottom
        return (int)arr[min + 1][1];
    }

    // This is an essential method for the algorithm
    // Finds the index of the first 0 in the array
    // If there is no 0 in the array, it returns 1 which is the first index of the array
    public static int findIndex(int arr[], int start){

        for (int i = start; i >= 1; i--) {
            if(arr[i] == 0){
                return i + 1;
            }
        }
        return 1;
    }

    // Reads the file and assigns the values to the array
    public static int[] readAndAssign(String fileName) {

        BufferedReader reader; // Reading object
        String line; // Line read from the file
        int[] arr = new int[fileSize(fileName)]; // Array to be returned

        try {

            reader = new BufferedReader(new FileReader(fileName)); // Assigning the file to the reader
            reader.readLine(); // Skipping the first line because it is meaningless
            while ((line = reader.readLine()) != null) { // Reading the file line by line until it ends
                String[] parts = line.split("\t"); // Splitting the line into parts
                arr[Integer.parseInt(parts[0])] = Integer.parseInt(parts[1]); // Assigning the given indexses and values
                                                                              // to the array
            }
            reader.close(); // Closing the reader

        } catch (IOException e) { // If an error occurs while reading the file
            // TODO Auto-generated catch block
            System.out.println("Error in reading file"); // Warning message
            e.printStackTrace(); // Printing the error
            System.exit(0); // Exiting the program
        }

        return arr; // Returning the array
    }

    // Function returns the number of lines in the given file
    public static int fileSize(String fileName) {

        BufferedReader reader; // Reading object
        int lines = 0; // Number of lines in the file

        try {

            reader = new BufferedReader(new FileReader(fileName)); // Assigning the file to the reader
            while (reader.readLine() != null) { // Reading the file line by line until it ends
                lines++;
            }
            reader.close(); // Closing the reader

        } catch (IOException e) { // If an error occurs while reading the file
            // TODO Auto-generated catch block
            System.out.println("Error in reading file"); // Warning message
            e.printStackTrace(); // Printing the error
            System.exit(0); // Exiting the program
        }

        return lines; // Returning the number of lines in the file
    }

    // Function prints given 2D array
    public static void printInt2Arr(double[][] arr){
        
        System.out.println();
        System.out.print("\t");
        for (int i = 0; i < arr[0].length; i++) {
            System.out.printf("%d.\t", i);
        }
        System.out.println();

        for(int i = 0; i < arr.length; i++){
            System.out.printf("%d.\t", i);
            for(int j = 0; j < arr[i].length; j++){
                System.out.printf("%.2f\t", arr[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        System.out.println("\nHello World!");

        // Reading given files and assigning them to arrays
        int[] playerSalaries = readAndAssign("players_salary.txt");
        int[] playerDemands = readAndAssign("yearly_player_demand.txt");

        int n = 20; // n-> number of years wanted to be planned
        int p = 5; // p-> number of players you raise in a year
        int c = 10; // c-> cost of a coach for a year

        // Call DP function
        int minimumCost = dp(n, p, c, playerSalaries, playerDemands);
        System.out.println();

        // Printing the results
        System.out.println("Number of years wanted to be planned: " + n);
        System.out.println("Number of players you raise in a year: " + p);
        System.out.println("Cost of a coach for a year: " + c);
        System.out.println("Minimum cost: " + minimumCost);
    }
}