import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var guide = new StrategyGuide(scanner);
        var totalScore = guide.totalScore();
        System.out.println("totalScore = " + totalScore);
    }

    private static class StrategyGuide {
        private final Scanner scanner;

        public StrategyGuide(Scanner scanner) {
            this.scanner = scanner;
        }

        public int totalScore() {
            return rounds().map(Round::score).mapToInt(Integer::intValue).sum();
        }

        private Stream<Round> rounds() {
            var rounds = new ArrayList<Round>();
            while (scanner.hasNextLine()) {
                var round = Round.build(scanner.nextLine());
                if(round != null) {
                    rounds.add(round);
                }
            }
            return rounds.stream();
        }
    }


    private static class Round {

        private final Shape opponentShape;
        private final Shape myShape;

        public Round(String opponentCode, String myCode) {
            opponentShape = Shape.build(opponentCode);
            myShape = Shape.build(myCode);
        }

        public static Round build(String line) {
            System.out.println("line = " + line);
            if(line.length() > 1) {
                return new Round(line.substring(0, 1), line.substring(2, 3));
            }
            return null;
        }

        public int score() {
            return myShape.score() + myShape.outcome(opponentShape).score();
        }
    }

    private static abstract class Shape {
        public static Shape build(String code) {
            switch (code) {
                case "A":
                case "X":
                    return new Rock();
                case "B":
                case "Y":
                    return new Paper();
                case "C":
                case "Z":
                    return new Scissor();
            }
            throw new IllegalArgumentException("Non valid input '%s'".formatted(code));
        }

        public abstract int score();

        public abstract Outcome outcome(Shape opponentShape);

        public abstract boolean isRock();
        public abstract boolean isPaper();
        public abstract boolean isScissor();

        private static class Rock extends Shape {

            @Override
            public int score() {
                return 1;
            }
            @Override
            public Outcome outcome(Shape opponentShape) {
                if(opponentShape.isScissor()) {
                    return new Outcome.Win();
                } else if(opponentShape.isPaper()) {
                    return new Outcome.Lose();
                } else {
                    return new Outcome.Draw();
                }
            }

            @Override
            public boolean isRock() {
                return true;
            }

            @Override
            public boolean isPaper() {
                return false;
            }

            @Override
            public boolean isScissor() {
                return false;
            }

        }

        private static class Paper extends Shape {
            @Override
            public int score() {
                return 2;
            }

            @Override
            public Outcome outcome(Shape opponentShape) {
                if(opponentShape.isRock()) {
                    return new Outcome.Win();
                } else if(opponentShape.isScissor()) {
                    return new Outcome.Lose();
                } else {
                    return new Outcome.Draw();
                }
            }

            @Override
            public boolean isRock() {
                return false;
            }

            @Override
            public boolean isPaper() {
                return true;
            }

            @Override
            public boolean isScissor() {
                return false;
            }
        }

        private static class Scissor extends Shape {
            @Override
            public int score() {
                return 3;
            }

            @Override
            public Outcome outcome(Shape opponentShape) {
                if(opponentShape.isPaper()) {
                    return new Outcome.Win();
                } else if(opponentShape.isRock()) {
                    return new Outcome.Lose();
                } else {
                    return new Outcome.Draw();
                }
            }

            @Override
            public boolean isRock() {
                return false;
            }

            @Override
            public boolean isPaper() {
                return false;
            }

            @Override
            public boolean isScissor() {
                return true;
            }
        }
    }

    public static abstract class Outcome {

        public abstract int score();

        public static class Draw extends Outcome {

            @Override
            public int score() {
                return 3;
            }
        }

        public static class Win extends Outcome {

            @Override
            public int score() {
                return 6;
            }
        }

        public static class Lose extends Outcome {

            @Override
            public int score() {
                return 0;
            }
        }
    }
}
