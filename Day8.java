import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day8 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var lines = new ArrayList<String>();
        while(scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

//        var lines = Arrays.stream("""
//                30373
//                25512
//                65332
//                33549
//                35390
//                """.split("\n")).toList();

        var grid = TreeGrid.build(lines);
        System.out.println("grid.amountOfVisibleTrees = " + grid.amountOfVisibleTrees());
        System.out.println("grid.highestScenicScore() = " + grid.highestScenicScore());
    }

    private static class TreeGrid {

        public static final int ASCII_VALUE_FOR_ZERO = 48;
        private final int width;
        private final List<Integer> treeHeights;

        public TreeGrid(int width, List<Integer> treeHeights) {
            this.width = width;
            this.treeHeights = treeHeights;
        }

        public int height() {
            return treeHeights.size() / width;
        }

        public static TreeGrid build(Collection<String> lines) {
            var width = lines.stream().findFirst().orElseThrow().length();
            var trees = String.join("", lines).chars().map(TreeGrid::convertToHeight).boxed().toList();
            return new TreeGrid(width, trees);
        }

        private static int convertToHeight(int code) {
            return code - ASCII_VALUE_FOR_ZERO;
        }

        public int amountOfVisibleTrees() {
            return getAllTrees().filter(Tree::isVisible).toList().size();
        }

        private Stream<Tree> getAllTrees() {
            return getAllTreeCoordinates().map(this::getTree);
        }

        private Tree getTree(Coordinate coordinate) {
            return new Tree(getTreeHeight(coordinate), getTreeHeightsUp(coordinate), getTreeHeightsDown(coordinate), getTreeHeightsLeft(coordinate), getTreeHeightsRight(coordinate));
        }

        private Stream<Integer> getTreeHeightsRight(Coordinate coordinate) {
            return coordinate.rangeRight(width).map(this::getTreeHeight);
        }

        private Stream<Integer> getTreeHeightsLeft(Coordinate coordinate) {
            return coordinate.rangeLeft().map(this::getTreeHeight);
        }

        private Stream<Integer> getTreeHeightsDown(Coordinate coordinate) {
            return coordinate.rangeDown(height()).map(this::getTreeHeight);
        }

        private Stream<Integer> getTreeHeightsUp(Coordinate coordinate) {
            return coordinate.rangeUp().map(this::getTreeHeight);
        }

        private int getTreeHeight(Coordinate coordinate) {
            return treeHeights.get(asIndex(coordinate));
        }

        private Stream<Coordinate> getAllTreeCoordinates() {
            return IntStream.range(0, treeHeights.size()).mapToObj(this::asCoordinate);
        }

        private Coordinate asCoordinate(int index) {
            return new Coordinate(index % width, index / width);
        }

        private int asIndex(Coordinate coordinate) {
            return coordinate.asIndex(width);
        }

        public int highestScenicScore() {
            return getAllTrees().mapToInt(Tree::scenicScore).max().orElseThrow();
        }

        private static class Tree {
            private final int treeHeight;
            private final Collection<Integer> treesUp;
            private final Collection<Integer> treesDown;
            private final Collection<Integer> treesLeft;
            private final Collection<Integer> treesRight;

            public Tree(int height, Stream<Integer> treesUp, Stream<Integer> treesDown, Stream<Integer> treesLeft, Stream<Integer> treesRight) {
                treeHeight = height;
                this.treesUp = treesUp.toList();
                this.treesDown = treesDown.toList();
                this.treesLeft = treesLeft.toList();
                this.treesRight = treesRight.toList();
            }

            public boolean isVisible() {
                return isVisible(treesUp) || isVisible(treesDown) || isVisible(treesLeft) || isVisible(treesRight);
            }

            private boolean isVisible(Collection<Integer> treeHeights) {
                return treeHeights.isEmpty() || treeHeights.stream().noneMatch(height -> height >= treeHeight);
            }

            public int scenicScore() {
                return amountOfVisibleTrees(treesUp) * amountOfVisibleTrees(treesDown) * amountOfVisibleTrees(treesLeft) * amountOfVisibleTrees(treesRight);
            }

            private int amountOfVisibleTrees(Collection<Integer> treeHeights) {
                var visibleTrees = 0;
                for (var height : treeHeights) {
                    visibleTrees++;
                    if(height >= treeHeight) {
                        return visibleTrees;
                    }
                }
                return visibleTrees;
            }
        }
    }

    private static class Coordinate {

        private final int x;
        private final int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int asIndex(int width) {
            return x + width * y;
        }

        public Stream<Coordinate> rangeUp() {
            return IntStream.rangeClosed(0, y - 1).boxed().sorted(Comparator.reverseOrder()).map(otherY -> new Coordinate(x, otherY));
        }

        public Stream<Coordinate> rangeDown(int height) {
            return IntStream.range(y + 1, height).mapToObj(otherY -> new Coordinate(x, otherY));
        }

        public Stream<Coordinate> rangeLeft() {
            return IntStream.rangeClosed(0, x - 1).boxed().sorted(Comparator.reverseOrder()).map(otherX -> new Coordinate(otherX, y));
        }

        public Stream<Coordinate> rangeRight(int width) {
            return IntStream.range(x + 1, width).mapToObj(otherX -> new Coordinate(otherX, y));
        }
    }
}
