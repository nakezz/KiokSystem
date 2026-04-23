import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ClipsEngine {
    private static final String CLIPS_EXE = "C:\\Program Files\\SSS\\CLIPS 6.4.2\\CLIPSDOS.exe";
    private static final String WORKING_DIR = System.getProperty("user.dir");

    public static List<String> executeAction(String actionFact) {
        File batchFile = new File(WORKING_DIR, "batch.clp");
        File outFile = new File(WORKING_DIR, "out.dat");
        File stateFile = new File(WORKING_DIR, "state.dat");

        // Clear output file
        if (outFile.exists()) outFile.delete();

        try (PrintWriter pw = new PrintWriter(new FileWriter(batchFile))) {
            pw.println("(load \"kiosk_rules.clp\")");

            if (stateFile.exists()) {
                pw.println("(load-facts \"state.dat\")");
            } else {
                pw.println("(load \"initial_data.clp\")");
                pw.println("(reset)");
            }

            if (actionFact != null && !actionFact.isEmpty()) {
                pw.println("(assert " + actionFact + ")");
            }

            pw.println("(run)");
            pw.println("(save-facts \"state.dat\")");
            pw.println("(exit)");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(CLIPS_EXE);
            pb.directory(new File(WORKING_DIR));
            pb.redirectInput(batchFile);
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            process.waitFor();
            
            if (batchFile.exists()) batchFile.delete();

            if (outFile.exists()) {
                return Files.readAllLines(outFile.toPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // Method to query data without running action rules (just to fetch GUI info)
    // We do this by asserting a query fact and letting CLIPS write to out.dat
    // But since the project is in Java, we can also just parse state.dat for simplicity!
    public static List<String> parseStateDat(String targetTemplate) {
        List<String> results = new ArrayList<>();
        File stateFile = new File(WORKING_DIR, "state.dat");
        if (!stateFile.exists()) {
            executeAction(null); // Force creation of state.dat
        }

        try (BufferedReader br = new BufferedReader(new FileReader(stateFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("(" + targetTemplate + " ")) {
                    results.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
