import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Day1 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var currentCalories = 0;
        var totalCalories = new ArrayList<Integer>();
        while(scanner.hasNextLine()) {
            var calories = tryParse(scanner.nextLine());
            if(calories < 0) {
                totalCalories.add(currentCalories);
                currentCalories = 0;
            } else {
                currentCalories += calories;
            }
        }
        if(currentCalories > 0) {
            totalCalories.add(currentCalories);
        }
        var sorted = totalCalories.stream().sorted(Collections.reverseOrder()).toList();
        System.out.println("Highest = " + sorted.get(0));
        System.out.println("Top 3 = " + (sorted.get(0) + sorted.get(1) + sorted.get(2)));
    }

    private static int tryParse(String line) {
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
