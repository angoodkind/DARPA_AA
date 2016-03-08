package features.bursts;

import keystroke.KeyStroke;

public class PauseTimeDelimiter extends BurstDelimiter {

	private long time;
	
	public PauseTimeDelimiter (long time){
		this.time=time;
	}

	@Override
	public boolean isDelimiter(KeyStroke k, KeyStroke s) throws NullPointerException {
		//if (s == null)
		if (s.getWhen() - k.getWhen() >= time)
			return true;
		return false;
	}
	
}
