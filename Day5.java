import java.util.*;
import java.util.stream.Collectors;

public class Day5 {
    public static void main(String[] args) {
        var stacks = Stacks.build(new Scanner(System.in), 9);
        System.out.println("stacks.topCrates() = " + stacks.topCrates());
    }

    enum InputState {
        CRATES,
        SEPARATION_LINE,
        MOVES,
        ADVANCED_MOVES
    }

    private static class Stacks {
        private final Collection<CrateStack> stacks;

        public Stacks(Collection<CrateStack> stacks) {
            this.stacks = stacks;
        }

        public static Stacks build(Scanner scanner, int numberOfStacks) {
            var stacks = createStacks(numberOfStacks);
            var state = InputState.CRATES;
            while(scanner.hasNextLine()) {
                switch (state) {
                    case CRATES -> {
                        var row = Row.build(scanner.nextLine());
                        if (row != null) {
                            stacks.forEach(stack -> stack.push(row.getCrate(stack.getIndex())));
                        } else {
                            stacks.forEach(CrateStack::reverseCrates);
                            state = InputState.SEPARATION_LINE;
                        }
                    }
                    case SEPARATION_LINE -> {
                        scanner.nextLine();
                        state = InputState.ADVANCED_MOVES;
                    }
                    case MOVES -> {
                        var move = Move.build(scanner.nextLine());
                        for (int i = 0; i < move.amount(); i++) {
                            var crate = stacks.get(move.source()).pop();
                            stacks.get(move.target()).push(crate);
                        }
                    }
                    case ADVANCED_MOVES -> {
                        var move = Move.build(scanner.nextLine());
                        var temp = new CrateStack(0);
                        for (int i = 0; i < move.amount(); i++) {
                            var crate = stacks.get(move.source()).pop();
                            temp.push(crate);
                        }
                        for (int i = 0; i < move.amount(); i++) {
                            var crate = temp.pop();
                            stacks.get(move.target()).push(crate);
                        }
                    }
                }
            }
            return new Stacks(stacks);
        }

        private static ArrayList<CrateStack> createStacks(int numberOfStacks) {
            var stacks = new ArrayList<CrateStack>();
            for (int i = 0; i < numberOfStacks; i++) {
                stacks.add(new CrateStack(i));
            }
            return stacks;
        }

        public String topCrates() {
            return stacks.stream().map(stack -> stack.pop().getCode()).collect(Collectors.joining());
        }

        @Override
        public String toString() {
            var builder = new StringBuilder();
            for (var stack : stacks) {
                builder.append(stack.toString()).append("\n");
            }
            return builder.toString();
        }
    }

    private static class Row {
        private final String line;

        public Row(String line) {
            this.line = line;
        }

        public static Row build(String line) {
            if(line.contains("[")) {
                return new Row(line);
            }
            return null;
        }

        public Crate getCrate(int stackIndex) {
            return Crate.build(line.substring(stringIndex(stackIndex), stringIndex(stackIndex) + 1));
        }

        private int stringIndex(int index) {
            return index * 4 + 1;
        }
    }

    private static class CrateStack {
        private final int index;
        private Stack<Crate> stack = new Stack<>();

        public CrateStack(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void push(Crate crate) {
            if(crate != null) {
                stack.push(crate);
            }
        }

        public void reverseCrates() {
            var temp = new java.util.Stack<Crate>();
            while(!stack.empty()) {
                temp.add(stack.pop());
            }
            stack = temp;
        }

        public Crate pop() {
            return stack.pop();
        }

        @Override
        public String toString() {
            return stack.stream().map(Crate::getCode).collect(Collectors.joining(""));
        }
    }

    private static class Crate {
        private final String code;

        public Crate(String code) {
            this.code = code;
        }

        public static Crate build(String code) {
            if(!code.equals(" ")) {
                return new Crate(code);
            }
            return null;
        }

        public String getCode() {
            return code;
        }
    }

    private static class Move {
        private final String[] components;

        public Move(String[] components) {
            this.components = components;
        }

        public static Move build(String line) {
            return new Move(line.split(" "));
        }

        public int amount() {
            return Integer.parseInt(components[1]);
        }

        // Note: index, start from 0
        public int source() {
            return Integer.parseInt(components[3]) - 1;
        }

        // Note: index, start from 0
        public int target() {
            return Integer.parseInt(components[5]) - 1;
        }

        @Override
        public String toString() {
            return String.join(" ", components);
        }
    }
}
