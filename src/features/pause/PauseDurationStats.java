package features.pause;

public class PauseDurationStats {	
	protected long m_durOfPauses;
	protected long m_durOfWholeText;
	protected double m_timeRateOfPauses;
	
	public PauseDurationStats() {
		m_durOfPauses = 0;
		m_durOfWholeText = 0;
		m_timeRateOfPauses = 0;
	}
	
	public PauseDurationStats(long durOfPauses, long durOfWholeText, double timeRateOfPauses) {
		m_durOfPauses = durOfPauses;
		m_durOfWholeText = durOfWholeText;
		m_timeRateOfPauses = timeRateOfPauses;
	}
}
