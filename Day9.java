import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Day9 {
    public static void main(String[] args) {
        var lines = read();
//        var lines = example();

        var motions = Motions.build(lines);
        var headCoordinates = motions.getHeadCoordinates();
        System.out.println("headCoordinates = " + headCoordinates);
        var tailCoordinates = TailCoordinates.build(headCoordinates);
        System.out.println("tailCoordinates.amountOfUniqueCoordinates() = " + tailCoordinates.amountOfUniqueCoordinates());
    }

    private static Collection<String> example() {
        return Arrays.asList("""
                R 4
                U 4
                L 3
                D 1
                R 4
                D 1
                L 5
                R 2
                """.split("\n"));
    }

    private static ArrayList<String> read() {
        var scanner = new Scanner(System.in);
        var lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        return lines;
    }

    private static class Motions {
        private final Collection<Motion> motions;

        public Motions(Collection<Motion> motions) {
            this.motions = motions;
        }

        public static Motions build(Collection<String> lines) {
            var motions = lines.stream().map(Motion::build).toList();
            return new Motions(motions);
        }

        public HeadCoordinates getHeadCoordinates() {
            var currentCoordinate = Coordinate.START;
            var headCoordinates = new HeadCoordinates(currentCoordinate);
            for (var motion : motions) {
                var coordinates = motion.getCoordinates(currentCoordinate);
                currentCoordinate = headCoordinates.add(coordinates);
            }
            return headCoordinates;
        }
    }

    private static class HeadCoordinates {
        private final ArrayList<Coordinate> coordinates = new ArrayList<>();

        public HeadCoordinates(Coordinate currentCoordinate) {
            add(List.of(currentCoordinate));
        }

        public Coordinate add(Collection<Coordinate> coordinates) {
            this.coordinates.addAll(coordinates);
            return this.coordinates.get(this.coordinates.size() - 1);
        }

        public Collection<Coordinate> coordinates() {
            return coordinates;
        }

        @Override
        public String toString() {
            return "HeadCoordinates{" +
                    "coordinates=" + coordinates +
                    '}';
        }
    }

    private static class Motion {

        protected final int steps;
        private final Direction direction;

        private Motion(int steps, Direction direction) {
            this.steps = steps;
            this.direction = direction;
        }

        public static Motion build(String code) {
            var components = code.split(" ");
            var directionCode = components[0];
            var steps = Integer.parseInt(components[1]);
            return switch (directionCode) {
                case "U" -> new Motion(steps, Direction.UP);
                case "D" -> new Motion(steps, Direction.DOWN);
                case "L" -> new Motion(steps, Direction.LEFT);
                case "R" -> new Motion(steps, Direction.RIGHT);
                default -> throw new IllegalArgumentException("Unknown input '%s'".formatted(code));
            };
        }

        public Collection<Coordinate> getCoordinates(Coordinate initialCoordinate) {
            var coordinates = new ArrayList<Coordinate>();
            Coordinate currentCoordinate = initialCoordinate;
            for (var i = 0; i < steps; i++) {
                currentCoordinate = currentCoordinate.nextCoordinate(direction);
                coordinates.add(currentCoordinate);
            }
            return coordinates;
        }
    }

    private static class Coordinate {
        public static final Coordinate START = new Coordinate(0, 0);
        private final int x;
        private final int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coordinate nextCoordinate(Direction direction) {
            return new Coordinate(x + direction.x(), y + direction.y());
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coordinate that)) return false;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public int distanceTo(Coordinate other) {
            return max(abs(other.x - x), abs(other.y - y));
        }

        public Direction directionTo(Coordinate head) {
            return new Direction(maxAbs1(head.x - x), maxAbs1(head.y - y));
        }

        private int maxAbs1(int value) {
            return value > 1 ? 1 : max(value, -1);
        }
    }

    private record Direction(int x, int y) {
        public static Direction UP = new Direction(0, 1);
        public static Direction DOWN = new Direction(0, -1);
        public static Direction RIGHT = new Direction(1, 0);
        public static Direction LEFT = new Direction(-1, 0);
    }

    private static class TailCoordinates {
        private final Collection<Coordinate> coordinates;

        public TailCoordinates(Collection<Coordinate> coordinates) {
            this.coordinates = coordinates;
        }

        public static TailCoordinates build(HeadCoordinates headCoordinates) {
            var coordinates = new ArrayList<Coordinate>();
            var currentPosition = Coordinate.START;
            coordinates.add(currentPosition);

            for (var head : headCoordinates.coordinates()) {
                if(currentPosition.distanceTo(head) > 1) {
                    var direction = currentPosition.directionTo(head);
                    System.out.println("direction = " + direction);
                    currentPosition = currentPosition.nextCoordinate(direction);
                    coordinates.add(currentPosition);
                }
            }
            System.out.println("coordinates = " + coordinates);
            return new TailCoordinates(coordinates);
        }

        public int amountOfUniqueCoordinates() {
            return coordinates.stream().distinct().toList().size();
        }
    }
}
