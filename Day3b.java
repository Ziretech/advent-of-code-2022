import java.util.*;

public class Day3b {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var groups = new Groups(scanner);
        System.out.println("groups.sumOfPriorities() = " + groups.sumOfPriorities());
    }

    private static class Groups {

        private final ArrayList<Group> groups = new ArrayList<>();

        public Groups(Scanner scanner) {
            while(scanner.hasNextLine()) {
                var group = new Group(scanner.nextLine(), scanner.nextLine(), scanner.nextLine());
                groups.add(group);
            }
        }

        public int sumOfPriorities() {
            return groups.stream().mapToInt(group -> group.findBadge().priority()).sum();
        }
    }

    private static class Group {

        private final Rucksack firstRucksack;
        private final Rucksack secondRucksack;
        private final Rucksack thirdRucksack;

        public Group(String firstLine, String secondLine, String thirdLine) {
            firstRucksack = new Rucksack(firstLine);
            secondRucksack = new Rucksack(secondLine);
            thirdRucksack = new Rucksack(thirdLine);
        }
        public Item findBadge() {
            return firstRucksack.commonItems(secondRucksack.commonItems(thirdRucksack.items())).singleItem();
        }

    }

    private static class Rucksack {

        private final Compartment compartment;

        private Rucksack(String line) {
            compartment = new Compartment(line);
        }

        public Compartment items() {
            return compartment;
        }

        public Compartment commonItems(Compartment items) {
            return compartment.sharedWith(items);
        }
    }

    private static class Compartment {
        private final Collection<Item> items;

        public Compartment(String itemCodes) {
            items = itemCodes.chars().mapToObj(Item::new).toList();
        }

        public Compartment(Collection<Item> items) {
            this.items = items;
        }

        public Compartment sharedWith(Compartment secondCompartment) {
            return new Compartment(items.stream()
                    .filter(myItem -> secondCompartment.items.stream().anyMatch(item -> item.equals(myItem)))
                    .toList());
        }

        @Override
        public String toString() {
            return "Compartment{" +
                    "items=" + items +
                    '}';
        }

        public Item singleItem() {
            return items.stream().findAny().orElseThrow();
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
