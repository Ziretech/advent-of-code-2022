import java.util.*;

public class Day9 {
    public static void main(String[] args) {
        var lines = read();

        var motions = Motions.build(lines);
        var headCoordinates = motions.getHeadCoordinates();
        System.out.println("headCoordinates = " + headCoordinates);
        var tailCoordinates = TailCoordinates.build(headCoordinates);
        System.out.println("tailCoordinates.amountOfUniqueCoordinates() = " + tailCoordinates.amountOfUniqueCoordinates());
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

        @Override
        public String toString() {
            return "HeadCoordinates{" +
                    "coordinates=" + coordinates +
                    '}';
        }
    }

    private static abstract class Motion {

        protected final int steps;

        private Motion(int steps) {
            this.steps = steps;
        }

        public static Motion build(String code) {
            System.out.println("code = " + code);
            var components = code.split(" ");
            System.out.println("components = " + Arrays.stream(components).toList());
            var directionCode = components[0];
            System.out.println("components[1] = " + components[1]);
            var steps = Integer.parseInt(components[1]);
            System.out.println("steps = " + steps);
            return new Up(steps);
//            return switch (directionCode) {
//                case Up.CODE -> new Up(steps);
//                case Down.CODE -> new Down(steps);
//                case Left.CODE -> new Left(steps);
//                case Right.CODE -> new Right(steps);
//                default -> throw new IllegalArgumentException("Unknown input '%s'".formatted(code));
//            };
        }

        public abstract Collection<Coordinate> getCoordinates(Coordinate currentCoordinate);

        private static class Up extends Motion {
            public static final String CODE = "U";

            public Up(int steps) {
                super(steps);
            }

            @Override
            public Collection<Coordinate> getCoordinates(Coordinate currentCoordinate) {
                var coordinates = new ArrayList<Coordinate>();
                Coordinate next;
                for (var i = 0; i < steps; i++) {
                    next = currentCoordinate.up();
                    coordinates.add(next);
                }
                return coordinates;
            }
        }

        private static class Down extends Motion {
            public static final String CODE = "D";

            public Down(int steps) {
                super(steps);
            }

            @Override
            public Collection<Coordinate> getCoordinates(Coordinate currentCoordinate) {
                var coordinates = new ArrayList<Coordinate>();
                Coordinate next;
                for (var i = 0; i < steps; i++) {
                    next = currentCoordinate.down();
                    coordinates.add(next);
                }
                return coordinates;
            }
        }

        private static class Left extends Motion{
            public static final String CODE = "L";

            public Left(int steps) {
                super(steps);
            }

            @Override
            public Collection<Coordinate> getCoordinates(Coordinate currentCoordinate) {
                var coordinates = new ArrayList<Coordinate>();
                Coordinate next;
                for (var i = 0; i < steps; i++) {
                    next = currentCoordinate.left();
                    coordinates.add(next);
                }
                return coordinates;
            }
        }

        private static class Right extends Motion {
            public static final String CODE = "R";

            public Right(int steps) {
                super(steps);
            }

            @Override
            public Collection<Coordinate> getCoordinates(Coordinate currentCoordinate) {
                var coordinates = new ArrayList<Coordinate>();
                Coordinate next;
                for (var i = 0; i < steps; i++) {
                    next = currentCoordinate.right();
                    coordinates.add(next);
                }
                return coordinates;
            }
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

        public Coordinate up() {
            return new Coordinate(x, y + 1);
        }

        public Coordinate down() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private static class TailCoordinates {
        public static TailCoordinates build(HeadCoordinates headCoordinates) {
            return null;
        }

        public int amountOfUniqueCoordinates() {
            return 0;
        }
    }
}
