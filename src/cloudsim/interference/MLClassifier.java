package cloudsim.interference;

import java.io.*;
import java.nio.file.Paths;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import cloudsim.interference.Interference;
import cloudsim.interference.util;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.*;

public class MLClassifier {

	static class LoggingConsole implements RMainLoopCallbacks {
		private Logger log;

		LoggingConsole(Logger log) {
			this.log = log;
		}

		public void rWriteConsole(Rengine re, String text, int oType) {
			log.info(String.format("rWriteConsole: %s", text));
		}

		public void rBusy(Rengine re, int which) {
			log.info(String.format("rBusy: %s", which));
		}

		public void rShowMessage(Rengine re, String message) {
			log.info(String.format("rShowMessage: %s", message));
		}

		public String rReadConsole(Rengine re, String prompt, int addToHistory) {
			return null;
		}

		public String rChooseFile(Rengine re, int newFile) {
			return null;
		}

		public void rFlushConsole(Rengine re) {
		}

		public void rLoadHistory(Rengine re, String filename) {
		}

		public void rSaveHistory(Rengine re, String filename) {
		}
	}


	public Rengine re = new Rengine(new String[] {"--no-save"}, false, null);
	private int firstTime;
		
	public MLClassifier() {
		this.firstTime=0;
	}

	public MLCResult getMLClass(Interference interf, int start, int finish) {
		// usar R para classificar ....
		//String project_folder = "C:/Users/Nadia/eclipse-workspace/IntegrationRandJava/R/"; //windows
		String project_folder = "/home/vinicius/git/CloudSimInterference/R/"; //ubuntu
		//Rengine re = new Rengine(new String[] {"--no-save"}, false, null);

		// Logger log = Logger.getLogger("test");

		// Rengine re = new Rengine(new String[] {"--no-save"}, false, new
		// LoggingConsole(log));
		 //re.eval(".libPaths('c:/users/Nadia/Documents/R/win-library/3.5')"); //windows
		 re.eval(".libPaths('/home/vinicius/R/x86_64-pc-linux-gnu-library/3.6')");  //ubuntu

		// re.eval("install.packages('e1071')");
		// re.eval("install.packages('caret')");
		// re.eval("install.packages('stringr')");
		// re.eval("install.packages('dplyr')");
		// re.eval("install.packages('fossil')");

		re.eval("library(\"e1071\")");
		re.eval("library(\"caret\")");
		re.eval("library(\"stringr\")");
		re.eval("library(\"dplyr\")");
		re.eval("library(\"fossil\")");

		re.eval("firstTime<-"+firstTime);
		re.eval("project_folder_inside <- \"" + project_folder + "\"");
		re.eval("training_dataset_folder <- \"" + project_folder + "forced/\"");
		re.eval("source(\"" + project_folder + "input_dataset.R\")");
		re.eval("total <- input_dataset(training_dataset_folder)");
		re.eval("source(\"" + project_folder + "misc.R\")");
		re.eval("source(\"" + project_folder + "cpd.R\")");
		re.eval("source(\"" + project_folder + "kmeans.R\")");
		re.eval("source(\"" + project_folder + "svm.R\")");

		re.eval("teste <- as.data.frame(matrix(0, ncol = 7))");
		re.eval("teste <- setNames(teste, c(\"nets\",\"netp\",\"blk\",\"mbw\",\"llcmr\",\"llcocc\",\"cpu\"))");

		int[] aux = new int[7];
		for (int i = 1; i < interf.getSize(); i++) {

			for (int j = 0; j < 7; j++) {
				aux[j] = interf.getIntByLine(i)[j];
				// System.out.print(a.getIntByLine(i)[j] + " ");

			}
			re.eval("aux <- data.frame(as.integer(" + aux[0] + "),as.integer(" + aux[1] + "),as.integer(" + aux[2]
					+ "),as.integer(" + aux[3] + "),as.integer(" + aux[4] + "),as.integer(" + aux[5] + "),as.integer("
					+ aux[6] + "))");
			re.eval("aux <- setNames(aux, c(\"nets\",\"netp\",\"blk\",\"mbw\",\"llcmr\",\"llcocc\",\"cpu\"))");
			re.eval("teste <- rbind(teste, aux)");
			// System.out.print("\n");

		}

		REXP hh = re.eval("abc <-svm_classifier_level(teste," + start + "," + finish + ")");
		RVector ff = hh.asVector();
		
		Map<String, String> gg = new HashMap<String, String>();

		for (int i = 0; i < ff.at(1).asStringArray().length; i++) {
			gg.put(ff.at(1).asStringArray()[i], ff.at(2).asStringArray()[i]);
		}

		MLCResult result = new MLCResult(gg);

		firstTime=1;
		re.stop();
		return (result);

	}

	public MLCResult getMLClass(Interference interf) {

		// usar R para classificar ....
		//String project_folder = "C:/Users/Nadia/eclipse-workspace/IntegrationRandJava/R/"; //windows
		String project_folder = "/home/vinicius/git/CloudSimInterference/R/"; //ubuntu
		//Rengine re = new Rengine(new String[] {"--no-save"}, false, null);

		// Logger log = Logger.getLogger("test");

		// Rengine re = new Rengine(new String[] {"--no-save"}, false, new
		// LoggingConsole(log));
		 //re.eval(".libPaths('c:/users/Nadia/Documents/R/win-library/3.5')"); //windows
		 re.eval(".libPaths('/home/vinicius/R/x86_64-pc-linux-gnu-library/3.6')");  //ubuntu

		 
		// re.eval("install.packages('e1071')");
		// re.eval("install.packages('caret')");
		// re.eval("install.packages('stringr')");
		// re.eval("install.packages('dplyr')");
		// re.eval("install.packages('fossil')");

		re.eval("library(\"e1071\")");
		re.eval("library(\"caret\")");
		re.eval("library(\"stringr\")");
		re.eval("library(\"dplyr\")");
		re.eval("library(\"fossil\")");

		re.eval("project_folder_inside <- \"" + project_folder + "\"");
		re.eval("training_dataset_folder <- \"" + project_folder + "forced/\"");
		re.eval("source(\"" + project_folder + "input_dataset.R\")");
		re.eval("total <- input_dataset(training_dataset_folder)");
		re.eval("source(\"" + project_folder + "misc.R\")");
		re.eval("source(\"" + project_folder + "cpd.R\")");
		re.eval("source(\"" + project_folder + "kmeans.R\")");
		re.eval("source(\"" + project_folder + "svm.R\")");

		re.eval("teste <- as.data.frame(matrix(0, ncol = 7))");
		re.eval("teste <- setNames(teste, c(\"nets\",\"netp\",\"blk\",\"mbw\",\"llcmr\",\"llcocc\",\"cpu\"))");

		int[] aux = new int[7];
		for (int i = 1; i < interf.getSize(); i++) {

			for (int j = 0; j < 7; j++) {
				aux[j] = interf.getIntByLine(i)[j];
				// System.out.print(a.getIntByLine(i)[j] + " ");

			}
			re.eval("aux <- data.frame(as.integer(" + aux[0] + "),as.integer(" + aux[1] + "),as.integer(" + aux[2]
					+ "),as.integer(" + aux[3] + "),as.integer(" + aux[4] + "),as.integer(" + aux[5] + "),as.integer("
					+ aux[6] + "))");
			re.eval("aux <- setNames(aux, c(\"nets\",\"netp\",\"blk\",\"mbw\",\"llcmr\",\"llcocc\",\"cpu\"))");
			re.eval("teste <- rbind(teste, aux)");
			// System.out.print("\n");

		}

		REXP hh = re.eval("abc <-svm_classifier_level(teste,1,nrow(teste))");
		RVector ff = hh.asVector();

		Map<String, String> gg = new HashMap<String, String>();
		
		for (int i = 0; i < ff.at(1).asStringArray().length; i++) {
			gg.put(ff.at(1).asStringArray()[i], ff.at(2).asStringArray()[i]);
		}
		
		MLCResult result = new MLCResult(gg);

		re.stop();
		return (result);

	}

}