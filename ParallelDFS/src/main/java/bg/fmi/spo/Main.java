package bg.fmi.spo;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;

public class Main {

    // Declare some default values
    // Where should the value of e be written
    private static String outFile = "output.txt";
    // Should we print some more info?
    private static boolean isQuiet = false;
    // how many members of the converging row should we include in the calculation?
    private static int precision = 8192;
    // and most important - how many workers should we have?
    private static int tasks = 1;

    public static void main(String args[]) throws InterruptedException {
        try {
            parseArgs(args);
        } catch(IllegalArgumentException e) {
            System.err.println(String.format("CLI options error: %s", e.getMessage()));
            return;
        }
        calculateE();
    }

    private static void calculateE() throws InterruptedException {
//        ECalculator calc = new ParallelECalculator(tasks, precision, isQuiet);
//
//        calc.calculate();
//
//        writeToFile(calc.getResult());
    }

    private static void writeToFile(BigDecimal result) {
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"))) {
            writer.write(result.toString());
        } catch (IOException e) {
            System.err.println(String.format("Couldnt write to output file '%s': %s", outFile, e.getMessage()));
        }
    }

    private static void parseArgs(String[] args) throws IllegalArgumentException {
        for(int i = 0; i < args.length; ++i) {
            String currentArg = args[i];
            switch(currentArg) {
                case "-q":
                    isQuiet = true;
                    break;
                case "-t":
                    try {
                        tasks = Integer.parseInt(args[++i]);
                        if (tasks < 1 ) {
                            throw new IllegalArgumentException(args[i]);
                        }
                    } catch(ArrayIndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("-t");
                    }
                    break;
                case "-p":
                    try {
                        precision = Integer.parseInt(args[++i]);
                        if (precision < 1 ) {
                            throw new IllegalArgumentException(args[i]);
                        }
                    } catch(ArrayIndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("-p");
                    }
                    break;
                case "-o":
                    try {
                        outFile = args[++i];
                    } catch(ArrayIndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("-o");
                    }
                    break;
            }
        }
    }
}
