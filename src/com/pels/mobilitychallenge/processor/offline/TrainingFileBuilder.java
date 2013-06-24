package com.pels.mobilitychallenge.processor.offline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.pels.mobilitychallenge.base.data.FramedReadings;

public class TrainingFileBuilder {
	private static final String EXT = ".csv";
	private static final String INPUT_PREFIX = "processed_";
	private static final String OUTPUT_PREFIX = "training_";
	private static final Charset ENCODING = StandardCharsets.UTF_8;
	
	private File inputDir;
	private String outputPath;
	private int windowLength;

	public TrainingFileBuilder(String inputPath, String outputPath, int windowLength) {
		this.outputPath = outputPath;
		inputDir = new File(inputPath);
		this.windowLength = windowLength;

	
	}
	
	public void writeTrainingFile() throws FileNotFoundException{
		if (inputDir.exists() && inputDir.isDirectory()) {
			File[] filtered = inputDir.listFiles(new ProcessedFileFilter(windowLength));
			System.out.println("Found " + filtered.length + " processed files for window length: " + windowLength);
			writeTrainingFile(filtered);
		}else{
			throw new FileNotFoundException("Input directory not found!");
		}
	}

	private void writeTrainingFile(File[] filtered) {
		List<String> allLines = new ArrayList<String>();
		allLines.add(FramedReadings.HEADER);
		try {
			for (File f : filtered) {
				List<String> temp = Files.readAllLines(f.toPath(), ENCODING);
				temp.remove(0);
				System.out.println(temp.size() + " lines in: " + f.getName() );
				allLines.addAll(temp);
			}
			writeTrainingFile(allLines);
		} catch (IOException e) {
			System.out.println("Error: Error creating training input file.");
			e.printStackTrace();
		}
	}

	private void writeTrainingFile(List<String> allLines) throws IOException {
		File output = new File(outputPath + OUTPUT_PREFIX + windowLength + EXT);
		output.getParentFile().mkdirs();
	    Path path = output.toPath();
	    Files.write(path, allLines, ENCODING);
		System.out.println("Success: Training input file created.");
	}

	class ProcessedFileFilter implements FilenameFilter {
		private String prefix;

		public ProcessedFileFilter(int wlen) {
			this.prefix = INPUT_PREFIX + wlen;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(prefix);
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO Change to cmd args
		// String inPath = args[1];
		// String outPath = args[2];
		String inPath = "/Users/cpels/Documents/workspace/fraunhofer/data/processed/";
		String outPath = "/Users/cpels/Documents/workspace/fraunhofer/data/training/";
		int wLen = 20;
		try {
			new TrainingFileBuilder(inPath, outPath, wLen).writeTrainingFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
