package ngrammodel;

import static org.junit.Assert.*;

import org.junit.Test;

import ngrammodel.Bigram;
import ngrammodel.BigramModel;

public class COCA_ModelTest {

	@Test
	public void test() {
		BigramModel fileModel = BigramModel.readFromFile("LM-BigramModel-Files/COCA_WordModel.data");
		BigramModel bigramModel = new BigramModel(fileModel);
		System.out.println(bigramModel.getBigramProbability(new Bigram("she","looked")));
		System.out.println(bigramModel.getBigramProbability(new Bigram("she","leaped")));
		System.out.println(bigramModel.getBigramProbability(new Bigram("she","woop")));
	}

}
