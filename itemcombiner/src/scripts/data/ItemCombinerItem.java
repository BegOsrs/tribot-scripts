package scripts.data;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString
public class ItemCombinerItem implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Getter
	private final String name;
	@Getter
	private final int invAmount;
	@Getter
	private final int restockAmount;
	@Getter
	private final int maxRestockPrice;
	@Getter
	private final int restockAtAmount;

	public ItemCombinerItem(String name, int invAmount, int maxRestockPrice, int restockAmount, int restockAtAmount) {
		this.name = name;
		this.invAmount = invAmount;
		this.restockAmount = restockAmount;
		this.maxRestockPrice = maxRestockPrice;
		this.restockAtAmount = restockAtAmount;
	}

}
