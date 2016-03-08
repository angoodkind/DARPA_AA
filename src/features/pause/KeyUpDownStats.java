package features.pause;

//Helper class to return multiple values from a method.
public class KeyUpDownStats {
	protected int m_numOfDownKey;
	protected int m_numOfUpKey;
	protected int m_upDownDiff;
	protected double m_upDownDiffRatio;
	
	public KeyUpDownStats() {
		m_numOfDownKey = 0;
		m_numOfUpKey = 0;
		m_upDownDiff = 0;
		m_upDownDiffRatio = 0.0;
	}
	
	public KeyUpDownStats(	int numOfDownKey, int numOfUpKey, int upDownDiff,
							double upDownDiffRatio) {
		m_numOfDownKey = numOfDownKey;
		m_numOfUpKey =  numOfUpKey;
		m_upDownDiff = upDownDiff;
		m_upDownDiffRatio = upDownDiffRatio;
	}
}
