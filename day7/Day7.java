package day7;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

//        var lines = Arrays.stream("""
//                $ cd /
//                $ ls
//                dir a
//                14848514 b.txt
//                8504156 c.dat
//                dir d
//                $ cd a
//                $ ls
//                dir e
//                29116 f
//                2557 g
//                62596 h.lst
//                $ cd e
//                $ ls
//                584 i
//                $ cd ..
//                $ cd ..
//                $ cd d
//                $ ls
//                4060174 j
//                8033020 d.log
//                5626152 d.ext
//                7214296 k
//                """.split("\n")).toList();

//        var lines = Arrays.stream("""
//                $ cd /
//                $ ls
//                dir a
//                14848514 b.txt
//                8504156 c.dat
//                dir d
//                $ cd a
//                """.split("\n")).toList();

        var commands = Commands.build(lines);
        var directory = Directory.build(commands);
        System.out.println(directory.toString(0));
        var sum = directory.sumOfAllDirectoriesWithSizeUpTo(100000);
        System.out.println("sum = " + sum);
        var delete = directory.findSmallestDirectoryThatWillFreeAtLeast(30000000);
        System.out.println("delete.size() = " + delete.size());
    }

    public static class Commands implements Iterable<Command> {
        private final Collection<Command> commands;

        public Commands(Collection<Command> commands) {
            this.commands = commands;
        }

        public static Commands build(Collection<String> lines) {
            var commands = new ArrayList<Command>();
            for (var line : lines) {
                if (line.trim().startsWith("$")) {
                    commands.add(Command.build(line.substring(2)));
                } else {
                    commands.get(commands.size() - 1).addResult(Result.build(line));
                }
            }
            return new Commands(commands);
        }

        @Override
        public String toString() {
            return "Commands{" +
                    "commands=" + commands +
                    '}';
        }

        @Override
        public Iterator<Command> iterator() {
            return commands.iterator();
        }

        @Override
        public void forEach(Consumer<? super Command> action) {
            commands.forEach(action);
        }

        @Override
        public Spliterator<Command> spliterator() {
            return commands.spliterator();
        }
    }

    private static abstract class Command {
        public static Command build(String line) {
            var component = line.split(" ");
            return switch (component[0]) {
                case "cd" -> ChangeDirectoryCommand.build(component[1]);
                case "ls" -> new ListContentCommand();
                default -> throw new IllegalArgumentException("unknown command '%s'".formatted(component[0]));
            };
        }

        public abstract void addResult(Result result);

        public abstract Directory executeIn(Directory currentDirectory);

        private static abstract class ChangeDirectoryCommand extends Command {

            public static ChangeDirectoryCommand build(String argument) {
                return switch (argument) {
                    case "/" -> new ChangeDirectoryRootCommand();
                    case ".." -> new ChangeDirectoryToParentCommand();
                    default -> new ChangeDirectoryToSubdirectoryCommand(argument);
                };
            }

            @Override
            public void addResult(Result result) {
                throw new IllegalStateException("cd commands has no output results");
            }

            @Override
            public abstract Directory executeIn(Directory currentDirectory);

            private static class ChangeDirectoryRootCommand extends ChangeDirectoryCommand {
                @Override
                public Directory executeIn(Directory currentDirectory) {
                    return currentDirectory.getRootDirectory();
                }
            }

            private static class ChangeDirectoryToParentCommand extends ChangeDirectoryCommand {
                @Override
                public Directory executeIn(Directory currentDirectory) {
                    return currentDirectory.getParentDirectory();
                }
            }

            private static class ChangeDirectoryToSubdirectoryCommand extends ChangeDirectoryCommand {
                private final String argument;

                public ChangeDirectoryToSubdirectoryCommand(String argument) {
                    this.argument = argument;
                }

                @Override
                public Directory executeIn(Directory currentDirectory) {
                    if(!currentDirectory.hasSubdirectory(argument)) {
                        currentDirectory.createDirectory(argument);
                    }
                    return currentDirectory.getSubdirectory(argument);
                }

                @Override
                public String toString() {
                    return "ChangeDirectoryToSubdirectoryCommand{" +
                            "argument='" + argument + '\'' +
                            '}';
                }
            }
        }

        private static class ListContentCommand extends Command {
            private final ArrayList<Result> results = new ArrayList<>();

            @Override
            public void addResult(Result result) {
                results.add(result);
            }

            @Override
            public Directory executeIn(Directory currentDirectory) {
                results.forEach(result -> result.executeIn(currentDirectory));
                return currentDirectory;
            }

            @Override
            public String toString() {
                return "ListContentCommand{" +
                        "results=" + results +
                        '}';
            }
        }
    }

    private static abstract class Result {
        public static Result build(String line) {
            var components = line.split(" ");
            if(components[0].equals("dir")) {
                return new DirectoryResult(components[1]);
            } else {
                return new FileResult(components[0], components[1]);
            }
        }

        public abstract void executeIn(Directory directory);


        private static class DirectoryResult extends Result {
            private final String directoryName;

            public DirectoryResult(String directoryName) {
                this.directoryName = directoryName;
            }

            @Override
            public String toString() {
                return "DirectoryResult{" +
                        "directoryName='" + directoryName + '\'' +
                        '}';
            }

            @Override
            public void executeIn(Directory directory) {

            }
        }

        private static class FileResult extends Result {
            private final String filename;
            private final int size;

            public FileResult(String sizeString, String filename) {
                this.filename = filename;
                this.size = Integer.parseInt(sizeString);
            }

            @Override
            public String toString() {
                return "FileResult{" +
                        "filename='" + filename + '\'' +
                        ", size=" + size +
                        '}';
            }

            @Override
            public void executeIn(Directory directory) {
                directory.addFile(new File(filename, size));
            }
        }
    }

    private static class File {
        @SuppressWarnings("FieldCanBeLocal")
        private final String filename;
        private final int size;

        public File(String filename, int size) {
            this.filename = filename;
            this.size = size;
        }

        public int size() {
            return size;
        }

        @Override
        public String toString() {
            return "File{" +
                    "filename='" + filename + '\'' +
                    ", size=" + size +
                    '}';
        }

        public String toString(int indentLevel) {
            return "  ".repeat(indentLevel) + "- %s (file, size=%d)".formatted(filename, size());
        }
    }

    private static class Directory {
        private final String directoryName;
        private final ArrayList<Directory> subdirectories = new ArrayList<>();
        private final Directory parent;
        private final ArrayList<File> files = new ArrayList<>();

        private Directory(String directoryName, Directory parent) {
            this.directoryName = directoryName;
            this.parent = parent;
        }

        public static Directory build(Commands commands) {
            var rootDirectory = new Directory("", null);
            var currentDirectory = rootDirectory;
            for (var command : commands) {
                currentDirectory = command.executeIn(currentDirectory);
            }
            return rootDirectory;
        }

        public Stream<Directory> getAllDirectories() {
            return Stream.concat(Stream.of(this), subdirectories.stream().flatMap(Directory::getAllDirectories));
        }

        public int size() {
            return subdirectoriesSize() + filesSize();
        }

        private int filesSize() {
            return files.stream().mapToInt(File::size).sum();
        }

        private int subdirectoriesSize() {
            return subdirectories.stream().mapToInt(Directory::size).sum();
        }


        public Directory getRootDirectory() {
            return parent == null ? this : parent.getRootDirectory();
        }

        public Directory getParentDirectory() {
            if(parent != null) {
                return parent;
            }
            throw new IllegalStateException("%s has no parent directory");
        }

        public boolean hasSubdirectory(String directoryName) {
            return subdirectories.stream().anyMatch(d -> d.hasName(directoryName));
        }

        private boolean hasName(String directoryName) {
            return this.directoryName.equals(directoryName);
        }

        public void createDirectory(String directoryName) {
            this.subdirectories.add(new Directory(directoryName, this));
        }

        public Directory getSubdirectory(String directoryName) {
            return subdirectories.stream().filter(d -> d.hasName(directoryName)).findFirst().orElseThrow();
        }

        public int sumOfAllDirectoriesWithSizeUpTo(int maxSize) {
            return getAllDirectories().mapToInt(Directory::size).filter(size -> size <= maxSize).sum();
        }

        public void addFile(File file) {
            files.add(file);
        }

        @Override
        public String toString() {
            return "Directory{" +
                    "directoryName='" + directoryName + '\'' +
                    ", subdirectories=" + subdirectories +
                    ", parent=" + (parent == null ? "null" : parent.name()) +
                    ", files=" + files +
                    '}';
        }

        public String toString(int indentLevel) {
            var stringBuilder = new StringBuilder("  ".repeat(indentLevel) + "- %s (dir, size=%d)".formatted(directoryName, size()));
            for (var subdirectory : subdirectories) {
                stringBuilder.append("\n");
                stringBuilder.append(subdirectory.toString(indentLevel + 1));
            }
            for (var file : files) {
                stringBuilder.append("\n");
                stringBuilder.append(file.toString(indentLevel + 1));
            }
            return stringBuilder.toString();
        }

        public String name() {
            return "\"" + directoryName + "\"";
        }

        public Directory findSmallestDirectoryThatWillFreeAtLeast(int requiredFreeSpace) {
            var toRemoveSize = size() - requiredFreeSpace;
            return getAllDirectories().sorted(Comparator.comparingInt(Directory::size)).filter(d -> d.size() >= toRemoveSize).peek(d -> System.out.println(d.size())).findFirst().orElseThrow();
        }
    }
}
