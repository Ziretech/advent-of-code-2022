import java.util.Collection;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day6 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var line = scanner.nextLine();
        var bufferOf4 = DatastreamBuffer.build(4, line);
        System.out.println("bufferOf4.firstMarker().endCharacterIndex() = " + bufferOf4.firstMarker().endCharacterIndex());
        var bufferOf14 = DatastreamBuffer.build(14, line);
        System.out.println("bufferOf14.firstMarker().endCharacterIndex() = " + bufferOf14.firstMarker().endCharacterIndex());
    }

    private static class DatastreamBuffer {
        private final Collection<Sequence> sequences;

        public DatastreamBuffer(String line, int sequenceLength) {
            sequences = IntStream
                    .range(0, line.length() - sequenceLength - 1)
                    .mapToObj(startIndex -> {
                        var endIndex = startIndex + sequenceLength;
                        return new Sequence(endIndex, line.substring(startIndex, endIndex));
                    })
                    .toList();
        }

        public static DatastreamBuffer build(int sequenceLength, String line) {
            return new DatastreamBuffer(line, sequenceLength);
        }

        public Sequence firstMarker() {
            return sequences.stream()
                    .filter(Sequence::areAllDifferent)
                    .findFirst().orElseThrow();
        }
    }

    private static class Sequence {
        private final int endCharacterIndex;
        private final String sequence;

        public Sequence(int endCharacterIndex, String sequence) {
            this.endCharacterIndex = endCharacterIndex;
            this.sequence = sequence;
        }

        public int endCharacterIndex() {
            return endCharacterIndex;
        }

        public boolean areAllDifferent() {
            var allDifferent = true;
            for (int i = 0; i < sequence.length() - 1; i++) {
                for (int j = i + 1; j < sequence.length(); j++) {
                    allDifferent &= !same(i, j);
                }
            }
            return allDifferent;
        }

        private boolean same(int index, int otherIndex) {
            return character(index).equals(character(otherIndex));
        }

        private String character(int index) {
            return sequence.substring(index, index + 1);
        }
    }
}
