import java.util.*;

public class Day3 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var rucksacks = new Rucksacks(scanner);
        System.out.println("rucksacks.sumOfPriorities() = " + rucksacks.sumOfPriorities());
    }

    private static class Rucksacks {
        private final Collection<Rucksack> rucksacks;
        public Rucksacks(Scanner scanner) {
            rucksacks = new ArrayList<>();
            while(scanner.hasNextLine()) {
                var line = scanner.nextLine();
                var rucksack = Rucksack.build(line);
                if(rucksack != null) {
                    rucksacks.add(rucksack);
                }
            }
        }

        public int sumOfPriorities() {
            return rucksacks.stream().mapToInt(sack -> sack.sharedItem().priority()).sum();
        }

        @Override
        public String toString() {
            return "Rucksacks{" +
                    "rucksacks=" + rucksacks +
                    '}';
        }
    }

    private static class Rucksack {

        private final Compartment firstCompartment;
        private final Compartment secondCompartment;

        private Rucksack(String line) {
            var halfLength = line.length() / 2;
            firstCompartment = new Compartment(line.substring(0, halfLength));
            secondCompartment = new Compartment(line.substring(halfLength));
        }

        public static Rucksack build(String line) {
            if(line != null && line.length() > 1) {
                return new Rucksack(line);
            }
            return null;
        }

        public Item sharedItem() {
            return firstCompartment.sharedWith(secondCompartment);
        }

        @Override
        public String toString() {
            return "Rucksack{" +
                    "firstCompartment=" + firstCompartment +
                    ", secondCompartment=" + secondCompartment +
                    '}';
        }
    }

    private static class Compartment {
        private final Collection<Item> items;

        public Compartment(String itemCodes) {
            items = itemCodes.chars().mapToObj(Item::new).toList();
        }

        public Item sharedWith(Compartment secondCompartment) {
            return items.stream()
                    .filter(myItem -> secondCompartment.items.stream().anyMatch(item -> item.equals(myItem)))
                    .findAny()
                    .orElseThrow();
        }

        @Override
        public String toString() {
            return "Compartment{" +
                    "items=" + items +
                    '}';
        }
    }

    private static class Item {
        private final int priority;

        public Item(int code) {
            if(code >= 97) {
                priority = getLowercasePriority(code);
            } else {
                priority = getUppercasePriority(code);
            }
        }

        private static int getUppercasePriority(int code) {
            return code - 65 + 27;
        }

        private static int getLowercasePriority(int code) {
            return code - 96;
        }

        public int priority() {
            return priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Item item)) return false;
            return priority == item.priority;
        }

        @Override
        public int hashCode() {
            return Objects.hash(priority);
        }

        @Override
        public String toString() {
            return "Item{" +
                    "priority=" + priority +
                    '}';
        }
    }
}
