package features.pause;

public class PauseCountStats {
	protected int m_numOfPauses;
	protected int m_numOfNonPauses;
	protected double m_cntRateOfPauses;
	
	public PauseCountStats() {
		m_numOfPauses = 0;
		m_numOfNonPauses = 0;
		m_cntRateOfPauses = 0;
	}
	
	public PauseCountStats(int numOfPauses, int numOfNonPauses, double cntRateOfPauses) {
		m_numOfPauses = numOfPauses;
		m_numOfNonPauses = numOfNonPauses;
		m_cntRateOfPauses = cntRateOfPauses;
	}
}
