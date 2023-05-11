import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Mehmet_Devrekoglu_2020510028
 */
public class Mehmet_Devrekoglu_2020510028 {

    // A dynamic programming approach to find optimal solution
    public static int Greedy(int n, int p, int c, int[] playerSalaries, int[] playerDemands) {

        // A cumilative array for the remaning players
        // It makes very faster to check if you can take a player from the previous year
        int[] remaningPlayer = new int[n + 1];

        // To find maximum number of players you can take from the previous year
        // It is called min because value is negative at the beginning
        // We will multiply it with -1 at the end
        int min = Integer.MAX_VALUE;

        // To find optimal solution we assign current year's cost due to change of limit
        double cost = 0;

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
        for (int i = n; i >= 1; i--) {
            
            int difference = p - playerDemands[i]; // Difference between produced and demands
            int[] remaningPlayerCopy = remaningPlayer.clone(); // Copy of remaning players array
            
            if(difference < 0){
                int index = findIndex(remaningPlayerCopy, i - 1); // Index of the first non-zero element in the remaning players array

                if(remaningPlayerCopy[(i != n) ? i + 1 : i] < 0){

                    // Calculate cost of taking players from the previous year
                    for (int k = index; k < i; k++) {
                        if(remaningPlayerCopy[k] +  remaningPlayerCopy[(i != n) ? i + 1 : i] >= difference * -1){
                            cost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) (difference * -1) / (double) remaningPlayerCopy[k];
                        }else if(remaningPlayerCopy[k] + remaningPlayerCopy[(i != n) ? i + 1 : i] > 0
                             && remaningPlayerCopy[k] + remaningPlayerCopy[(i != n) ? i + 1 : i] < difference * -1){
                            cost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) (remaningPlayerCopy[k] + remaningPlayerCopy[(i != n) ? i + 1 : i]) / (double) remaningPlayerCopy[k];
                        }
                    }

                    // Assign total number of players taken from the previous years
                    remaningPlayerCopy[i] = difference + remaningPlayerCopy[(i != n) ? i + 1 : i];

                    // Print how many players taken from the previous years and how many coaches hired
                    System.out.printf("(%d) %d Coachs hired, %d Players taken from the previous years\n", i
                    , difference * -1 - remaningPlayerCopy[i - 1], remaningPlayerCopy[i - 1]);

                }else{

                    // Find the minimum value
                    int limit = Math.min(difference * -1, remaningPlayerCopy[i - 1]);
                    // To find out if calculated cost is less than the cost of training new players
                    double tempCost = 0;
                    // To remove the players that will not be taken from the remaning players
                    int num = 0;

                    // If the limit is difference * -1, it means that
                    // You can maximum take diffrence * -1 players from the previous year
                    if(limit == difference * -1){
                        num = remaningPlayerCopy[i - 1] - limit;
                    }
                    // If the limit is remaning players from the previous year,
                    // It means that you can maximum take remaning players from the previous year
                    else if(limit == remaningPlayerCopy[i - 1]){
                        num = remaningPlayerCopy[i - 1] - limit;
                    }

                    // Remove the player that will not be taken from the remaning players
                    for(int k = index; k < i; k++){
                        if(remaningPlayerCopy[k] <= num){
                            remaningPlayerCopy[k] = 0;
                        }else{
                            remaningPlayerCopy[k] -= num;
                        }
                    }

                    // Calculate the cost for this year
                    tempCost += ((difference * -1) - remaningPlayerCopy[i - 1]) * c;
                    for(int k = index; k < i; k++){
                        if(remaningPlayerCopy[k] <= remaningPlayerCopy[i - 1]){
                            tempCost += (double) playerSalaries[remaningPlayerCopy[k]];
                        }else{
                            tempCost += (double) playerSalaries[remaningPlayerCopy[k]] * (double) remaningPlayerCopy[i - 1] / (double) remaningPlayerCopy[k];
                        }
                    }

                    if(tempCost < (difference * -1) * c){

                        // Add the cost to the total cost
                        cost += tempCost;
                        // Assign negative value to the remaning players to check if it is taken from the previous year for the next years
                        if(remaningPlayerCopy[i - 1] != 0){
                            remaningPlayerCopy[i] = remaningPlayerCopy[i - 1] * -1;
                        }
                    }else{

                        // Calculate the cost for this year
                        cost += (difference * -1) * c;
                        // Remove the player that will not be taken from the remaning players
                        for(int k = index; k < i; k++){
                            if(remaningPlayerCopy[k] <= remaningPlayerCopy[i - 1]){
                                remaningPlayerCopy[k] = 0;
                            }else{
                                remaningPlayerCopy[k] -= remaningPlayerCopy[i - 1];
                            }
                        }
                    }

                    // Print how many players taken from the previous years and how many coaches hired           
                    System.out.printf("(%d) %d Coachs hired, %d Players taken from the previous years\n", i
                    , difference * -1 - remaningPlayerCopy[i - 1], remaningPlayerCopy[i - 1]);       
                }
            }
        }

        // Return left bottom value
        // Because algorithm works top to bottom
        return (int)cost;
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

    public static void main(String[] args) {

        System.out.println("\nHello World!");

        // Reading given files and assigning them to arrays
        int[] playerSalaries = readAndAssign("players_salary.txt");
        int[] playerDemands = readAndAssign("yearly_player_demand.txt");

        int n = 20; // n-> number of years wanted to be planned
        int p = 5; // p-> number of players you raise in a year
        int c = 10; // c-> cost of a coach for a year

        // Call Greedy function
        long startTime = System.nanoTime();
        int minimumCost = Greedy(n, p, c, playerSalaries, playerDemands);
        long endTime = System.nanoTime();
        System.out.println();

        // Printing the results
        System.out.println("Number of years wanted to be planned: " + n);
        System.out.println("Number of players you raise in a year: " + p);
        System.out.println("Cost of a coach for a year: " + c);
        System.out.println("Minimum cost: " + minimumCost);
        System.out.println("Time: " + (endTime - startTime) / 1000000 + " ms");
    }
}