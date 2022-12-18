import java.util.*;
import java.util.stream.IntStream;

public class Day10 {
    public static void main(String[] args) {
         var lines = read();
//        var lines = example();

        var program = Program.build(lines);
        var strength = program.signalStrengthDuringCycles(20, 60, 100, 140, 180, 220);
        System.out.println("strength = " + strength);
        var pixelLits = IntStream.range(0, 240).mapToObj(program::isPixelLitAtPosition).map(lit -> lit ? "#" : ".").toList();
        for (int i = 0; i < 240; i++) {
            if(i % 40 == 0) {
                System.out.println();
            }
            System.out.print(pixelLits.get(i));
        }
    }

    private static class Program {

        private final Collection<Operation> operations;

        public Program(Collection<Operation> operations) {
            this.operations = operations;
        }

        public static Program build(Collection<String> lines) {
            var operations = lines.stream().map(Operation::build).toList();
            return new Program(operations);
        }

        public int signalStrengthDuringCycles(int... cycles) {
            return Arrays.stream(cycles).map(targetCycle -> valueOfRegisterX(targetCycle) * targetCycle).sum();
        }

        private int valueOfRegisterX(int targetCycle) {
            var cycle = 0;
            var registerX = 1;
            var instructionPointer = operations.iterator();

            while(cycle < targetCycle) {
                var operation = instructionPointer.next();

                cycle += operation.completionTime();
                if(cycle <= targetCycle) {
                    registerX = operation.updateStrength(registerX);
                }
            }
            return registerX;
        }

        @Override
        public String toString() {
            return "Program{" +
                    "operations=" + operations +
                    '}';
        }

        public boolean isPixelLitAtPosition(int targetCycle) {
            return Math.abs(targetCycle % 40 - valueOfRegisterX(targetCycle)) < 2;
        }
    }

    private static abstract class Operation {

        public static Operation build(String line) {
            var components = line.split(" ");
            return switch (components[0]) {
                case "noop" -> new NoOperation();
                case "addx" -> new AddOperation(components[1]);
                default -> throw new IllegalArgumentException("Unknown operation: '%s'".formatted(components[0]));
            };
        }

        public abstract int completionTime();

        public abstract int updateStrength(int strength);

        private static class NoOperation extends Operation {
            @Override
            public int completionTime() {
                return 1;
            }

            @Override
            public int updateStrength(int strength) {
                return strength;
            }

            @Override
            public String toString() {
                return "NoOperation{}";
            }
        }

        private static class AddOperation extends Operation {
            private final int strength;

            public AddOperation(String strengt) {
                this.strength = Integer.parseInt(strengt);
            }

            @Override
            public int completionTime() {
                return 2;
            }

            @Override
            public int updateStrength(int strength) {
                return strength + this.strength;
            }

            @Override
            public String toString() {
                return "AddOperation{" +
                        "strength=" + strength +
                        '}';
            }
        }
    }

    private static Collection<String> read() {
        var scanner = new Scanner(System.in);
        var lines = new ArrayList<String>();
        while(scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        return lines;
    }

    private static Collection<String> example() {
        return Arrays.stream("""
                addx 15
                addx -11
                addx 6
                addx -3
                addx 5
                addx -1
                addx -8
                addx 13
                addx 4
                noop
                addx -1
                addx 5
                addx -1
                addx 5
                addx -1
                addx 5
                addx -1
                addx 5
                addx -1
                addx -35
                addx 1
                addx 24
                addx -19
                addx 1
                addx 16
                addx -11
                noop
                noop
                addx 21
                addx -15
                noop
                noop
                addx -3
                addx 9
                addx 1
                addx -3
                addx 8
                addx 1
                addx 5
                noop
                noop
                noop
                noop
                noop
                addx -36
                noop
                addx 1
                addx 7
                noop
                noop
                noop
                addx 2
                addx 6
                noop
                noop
                noop
                noop
                noop
                addx 1
                noop
                noop
                addx 7
                addx 1
                noop
                addx -13
                addx 13
                addx 7
                noop
                addx 1
                addx -33
                noop
                noop
                noop
                addx 2
                noop
                noop
                noop
                addx 8
                noop
                addx -1
                addx 2
                addx 1
                noop
                addx 17
                addx -9
                addx 1
                addx 1
                addx -3
                addx 11
                noop
                noop
                addx 1
                noop
                addx 1
                noop
                noop
                addx -13
                addx -19
                addx 1
                addx 3
                addx 26
                addx -30
                addx 12
                addx -1
                addx 3
                addx 1
                noop
                noop
                noop
                addx -9
                addx 18
                addx 1
                addx 2
                noop
                noop
                addx 9
                noop
                noop
                noop
                addx -1
                addx 2
                addx -37
                addx 1
                addx 3
                noop
                addx 15
                addx -21
                addx 22
                addx -6
                addx 1
                noop
                addx 2
                addx 1
                noop
                addx -10
                noop
                noop
                addx 20
                addx 1
                addx 2
                addx 2
                addx -6
                addx -11
                noop
                noop
                noop
                """.split("\n")).toList();
    }
}
