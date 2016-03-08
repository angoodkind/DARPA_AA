package ngrammodel;

import java.io.*;
import java.util.*;

public class Trigram implements Serializable {
	
	private static final long serialVersionUID = 6529685098267757693L;
	public static final String START = "_START_";
	public static final String STOP = "_STOP_";
	protected String gram1;
	protected String gram2;
	protected String gram3;
	
	public Trigram(String gram1, String gram2, String gram3) {
        this.gram1 = gram1;
        this.gram2 = gram2;
        this.gram3 = gram3;
    }
	
	/**
	 * For a length 3 String
	 * @param ngram	String of length 3
	 */
	public Trigram(String ngram) {
		this.gram1 = ngram.substring(0,1);
		this.gram2 = ngram.substring(1,2);
		this.gram3 = ngram.substring(2,3);
	}
        
    public String getGram1() {
        return gram1;
    }
    
    public String getGram2() {
        return gram2;
    }
    
    public String getGram3() {
    		return gram3;
    }

    public List<String> toList() {
        List<String> toRet = new ArrayList<String>();
        toRet.add(gram1);
        toRet.add(gram2);
        toRet.add(gram3);
        return toRet;
	}

    public String toCharString() {
        return "("+gram1+","+gram2+","+gram3+")";
    }
    
  @Override
      public boolean equals(Object other) {
        if (! (other instanceof Trigram)) {
          return false;
        }
        Trigram otherTrigram = (Trigram) other;
        return (otherTrigram.getGram1().equals(this.gram1) && otherTrigram.getGram2().equals(this.gram2) && otherTrigram.getGram3().equals(this.gram3));
      }

  @Override
  public int hashCode() {
	  return (this.gram1.hashCode()+this.gram2.hashCode()+this.gram3.hashCode());
  }
  
}
