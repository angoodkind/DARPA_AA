package ngrammodel;

import java.io.*;
import java.util.*;

public class Fourgram implements Serializable {
	
	private static final long serialVersionUID = 6529685098267757694L;
	public static final String START = "_START_";
	public static final String STOP = "_STOP_";
	protected String gram1;
	protected String gram2;
	protected String gram3;
	protected String gram4;
	
	public Fourgram(String gram1, String gram2, String gram3, String gram4) {
        this.gram1 = gram1;
        this.gram2 = gram2;
        this.gram3 = gram3;
        this.gram4 = gram4;
    }
	
	/**
	 * For a length 4 String
	 * @param ngram	String of length 4
	 */
	public Fourgram(String ngram) {
		this.gram1 = ngram.substring(0,1);
		this.gram2 = ngram.substring(1,2);
		this.gram3 = ngram.substring(2,3);
		this.gram4 = ngram.substring(3,4);
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
    
    public String getGram4() {
		return gram4;
}

    public List<String> toList() {
        List<String> toRet = new ArrayList<String>();
        toRet.add(gram1);
        toRet.add(gram2);
        toRet.add(gram3);
        toRet.add(gram4);
        return toRet;
	}

    public String toCharString() {
        return "("+gram1+","+gram2+","+gram3+","+gram4+")";
    }
    
  @Override
      public boolean equals(Object other) {
        if (! (other instanceof Fourgram)) {
          return false;
        }
        Fourgram otherFourgram = (Fourgram) other;
        return (otherFourgram.getGram1().equals(this.gram1) && otherFourgram.getGram2().equals(this.gram2) &&
        		otherFourgram.getGram3().equals(this.gram3) && otherFourgram.getGram4().equals(this.gram4));
      }

  @Override
  public int hashCode() {
	  return (this.gram1.hashCode()+this.gram2.hashCode()+this.gram3.hashCode()+this.gram4.hashCode());
  }
  
}
