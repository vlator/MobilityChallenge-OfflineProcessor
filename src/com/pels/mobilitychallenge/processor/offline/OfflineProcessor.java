package com.pels.mobilitychallenge.processor.offline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.pels.mobilitychallenge.base.common.CustomFileFilterByExt;
import com.pels.mobilitychallenge.base.data.FramedReadings;
import com.pels.mobilitychallenge.base.data.FramesExtractor;
import com.pels.mobilitychallenge.base.data.Label;
import com.pels.mobilitychallenge.base.data.Reading;
import com.pels.mobilitychallenge.base.data.ReadingsLoader;

public class OfflineProcessor {

	private static final String PROCESSED = "processed_";
	private static final String PROCESSED_OUTPUT_PATH = "/Users/cpels/Documents/workspace/fraunhofer/data/processed/";
	File inputDir;

	ReadingsLoader rLoader;
	FramesExtractor sw;
	int wLen;

	public OfflineProcessor(String inputDirPath, int windowLength) {
		this.inputDir = new File(inputDirPath);
		this.wLen = windowLength;
		if (inputDir.exists() && inputDir.isDirectory()) {
			sw = new FramesExtractor(windowLength);
		} else {
			System.out.println("Input path error.");
		}

	}

	private void runProcessing() {
		File[] allInputFiles = inputDir.listFiles(new CustomFileFilterByExt(
				"csv"));
		for (File inputFile : allInputFiles) {
			System.out.println("\nProcessing: " + inputFile.getName());
			process(inputFile);
		}
	}

	private void process(File inputFile) {
		String filename = inputFile.getName();
		try {
			rLoader = new ReadingsLoader(inputFile);
			for (Label label : Label.values()) {
				List<Reading> allFrames = rLoader.filterByLabel(label);
				List<FramedReadings> framed = sw.getFramedReadings(allFrames);

				setLabelAndWriteFrame(label, filename, framed);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setLabelAndWriteFrame(Label label, String filename,
			List<FramedReadings> framed) {
		String path = PROCESSED_OUTPUT_PATH + PROCESSED + wLen + "_"
				+ filename;
//		System.out.println(path);
		System.out.println(label + " Frames: " +  framed.size());
		
		File outputFile = new File(path);
		BufferedWriter writer;
		try {
			if (!outputFile.exists()) {
				writer = new BufferedWriter(new FileWriter(outputFile, true));
				writer.write(FramedReadings.HEADER + "\n");
			} else {
				writer = new BufferedWriter(new FileWriter(outputFile, true));
			}
			for (FramedReadings r : framed) {
				r.setLabel(label);
				if (r.toString() == null) {
					System.out.println("why???");
				}
				writer.write(r.toString() + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inPath = "/Users/cpels/Documents/workspace/fraunhofer/data/raw_input";
		int wLen = 20;
		new OfflineProcessor(inPath, wLen).runProcessing();

	}

}
