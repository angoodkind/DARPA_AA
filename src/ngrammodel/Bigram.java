package ngrammodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

import keystroke.KeyStroke;

public class Bigram implements Serializable {
	
	private static final long serialVersionUID = 6529685098267757692L;
	public static final String START = "_START_";
	public static final String STOP = "_STOP_";
	protected String gram1;
	protected String gram2;
	
	public Bigram(String gram1, String gram2) {
        this.gram1 = gram1;
        this.gram2 = gram2;
    }
	
	/**
	 * For a length 2 String
	 * @param ngram	String of length 2
	 */
	public Bigram(String ngram) {
		this.gram1 = ngram.substring(0,1);
		this.gram2 = ngram.substring(1,2);
	}
        
    public String getGram1() {
        return gram1;
    }
    
    public String getGram2() {
        return gram2;
    }

    public List<String> toList() {
        List<String> toRet = new ArrayList<String>();
        toRet.add(gram1);
        toRet.add(gram2);
        return toRet;
	}

    /**
     * Pretty print of the bigram
     * 
     */
    public String toCharString() {
        return "("+gram1+","+gram2+")";
    }
    
  @Override
      public boolean equals(Object other) {
        if (! (other instanceof Bigram)) {
          return false;
        }
        Bigram otherBigram = (Bigram) other;
        return (otherBigram.getGram1().equals(this.gram1) && otherBigram.getGram2().equals(this.gram2));
      }

  @Override
  public int hashCode() {
	  return (this.gram1.hashCode()+this.gram2.hashCode());
  }
  
}
