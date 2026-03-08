package CatanSimulator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GameStateExporter {
    private final Path outputPath;

    public GameStateExporter(Path outputPath) {
        this.outputPath = outputPath;
    }

    public void export(Board board) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"roads\": [");

        boolean firstRoad = true;
        for (Edge edge : board.getEdges()) {
            if (edge.getRoad() == null) {
                continue;
            }

            if (firstRoad) {
                json.append("\n");
                firstRoad = false;
            } else {
                json.append(",\n");
            }

            int nodeA = Math.min(edge.getNode1().getId(), edge.getNode2().getId());
            int nodeB = Math.max(edge.getNode1().getId(), edge.getNode2().getId());
            json.append("    { \"a\": ")
                .append(nodeA)
                .append(", \"b\": ")
                .append(nodeB)
                .append(", \"owner\": \"")
                .append(edge.getRoad().getOwner().getColor().name())
                .append("\" }");
        }

        if (!firstRoad) {
            json.append("\n");
        }
        json.append("  ],\n");
        json.append("  \"buildings\": [");

        boolean firstBuilding = true;
        for (Node node : board.getNodes()) {
            Building building = node.getBuilding();
            if (building == null) {
                continue;
            }

            if (firstBuilding) {
                json.append("\n");
                firstBuilding = false;
            } else {
                json.append(",\n");
            }

            String type = building instanceof City ? "CITY" : "SETTLEMENT";
            json.append("    { \"node\": ")
                .append(node.getId())
                .append(", \"owner\": \"")
                .append(building.getOwner().getColor().name())
                .append("\", \"type\": \"")
                .append(type)
                .append("\" }");
        }

        if (!firstBuilding) {
            json.append("\n");
        }
        json.append("  ]\n");
        json.append("}\n");

        writeAtomically(json.toString());
    }

    private void writeAtomically(String content) {
        try {
            Files.createDirectories(outputPath.getParent());
            Path tempPath = outputPath.resolveSibling(outputPath.getFileName() + ".tmp");
            Files.writeString(tempPath, content, StandardCharsets.UTF_8);

            try {
                Files.move(
                    tempPath,
                    outputPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempPath, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to export visualizer state.", e);
        }
    }
}
