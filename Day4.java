import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Day4 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var pairs = Pairs.build(scanner);
        System.out.println("pairs.amountWithRangeFullyContainingTheOther() = " + pairs.amountWithRangeFullyContainingTheOther());
        System.out.println("pairs.amountWithRangeOverlappingTheOther() = " + pairs.amountWithRangeOverlappingTheOther());
    }

    private static class Pairs {
        private final Collection<Pair> pairs;

        public Pairs(Collection<Pair> pairs) {
            this.pairs = pairs;
        }

        public static Pairs build(Scanner scanner) {
            var pairs = new ArrayList<Pair>();
            while(scanner.hasNextLine()) {
                var line = scanner.nextLine();
                pairs.add(Pair.build(line));
            }
            return new Pairs(pairs);
        }

        public int amountWithRangeFullyContainingTheOther() {
            return pairs.stream().filter(Pair::haveRangeFullyContainingOther).toList().size();
        }

        public int amountWithRangeOverlappingTheOther() {
            return pairs.stream().filter(Pair::haveRangeOverlappingOther).toList().size();
        }
    }

    private static class Pair {
        private final Range firstRange;
        private final Range secondRange;

        public Pair(Range firstRange, Range secondRange) {
            this.firstRange = firstRange;
            this.secondRange = secondRange;
        }

        public static Pair build(String line) {
            var rangeLine = line.split(",");
            return new Pair(Range.build(rangeLine[0]), Range.build(rangeLine[1]));
        }

        public boolean haveRangeFullyContainingOther() {
            return firstRange.contains(secondRange) || secondRange.contains(firstRange);
        }

        public boolean haveRangeOverlappingOther() {
            return firstRange.overlapsToRight(secondRange) || secondRange.overlapsToRight(firstRange);
        }
    }

    private static class Range {

        private final int beginning;
        private final int end;

        public Range(int beginning, int end) {
            this.beginning = beginning;
            this.end = end;
        }

        public static Range build(String line) {
            var range = line.split("-");
            return new Range(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
        }

        public boolean contains(Range otherRange) {
            return beginning <= otherRange.beginning && end >= otherRange.end;
        }

        public boolean overlapsToRight(Range otherRange) {
            return contains(otherRange.beginning);
        }

        private boolean contains(int number) {
            return beginning <= number && end >= number;
        }
    }
}
