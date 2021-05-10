package scripts.task;

import java.io.Serializable;

public interface Task extends Serializable {

	String FIRE_COOKING = "Fire";
	String RANGE_COOKING = "Range";
	String MAKE_ITEM_COOKING = "Make item";
	String ROGUES_DEN_COOKING = "Rogues den";

	String FIRE_COOKING_FORMAT = "Type=" + FIRE_COOKING + ", Supply=%s, Product=%s, Logs=%s, Amount=%s, Stop level=%s";
	String RANGE_COOKING_FORMAT = "Type=" + RANGE_COOKING + ", Supply=%s, Product=%s, Amount=%s, Stop level=%s";
	String MAKE_ITEM_COOKING_FORMAT = "Type=" + MAKE_ITEM_COOKING + ", First supply=%s, Second supply=%s, Product=%s, Amount=%s, Stop level=%s";
	String ROGUES_DEN_COOKING_FORMAT = "Type=" + ROGUES_DEN_COOKING + ", Supply=%s, Product=%s, Amount=%s, Stop level=%s";

	String getCookingType();

	void setCookingType(String type);

	String getProduct();

	void setProduct(String product);

	String getFirstSupply();

	void setFirstSupply(String firstSupply);

	String getSecondSupply();

	void setSecondSupply(String secondSupply);

	String getLogs();

	void setLogs(String logs);

	int getAmount();

	void setAmount(int amount);

	int getAmountLeft();

	void setAmountLeft(int amountLeft);

	int getStopLevel();

	void setStopLevel(int level);

	void decreaseAmountLeft(int amount);

	String getKeybind();

}
