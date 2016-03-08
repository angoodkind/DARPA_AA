package extractors.data;

import java.util.Comparator;

/**
 * Allows List Sorting of Answer objects by their OrderId's
 *  
 * @author Patrick
 */
public class ByOrderComparator implements Comparator<Answer> {

	@Override
	public int compare(Answer arg0, Answer arg1) {
		return arg0.getOrderID() - arg1.getOrderID();
	}

}
