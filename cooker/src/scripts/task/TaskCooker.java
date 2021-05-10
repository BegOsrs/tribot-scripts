package scripts.task;

import java.io.Serial;
import java.util.Locale;

public class TaskCooker implements Task {

	@Serial
	private static final long serialVersionUID = 1L;

	private final String keybind;
	private String cookingType, product, firstSupplyName, secondSupplyName, logsName;
	private int stopLevel, amountToProcess, amountLeft;

	public TaskCooker() {
		this("", "", "", "", "", -1, -1, "");
	}

	public TaskCooker(String type, String firstSupply, String secondSupply, String product,
					  String logs, int amount, int stopLevel, String keybind) {
		this.product = product;
		this.firstSupplyName = firstSupply;
		this.secondSupplyName = secondSupply;
		this.logsName = logs;
		this.cookingType = type;
		this.stopLevel = stopLevel;
		this.amountToProcess = amount;
		this.amountLeft = amountToProcess;
		this.keybind = keybind;
	}

	@Override
	public String getCookingType() {
		return cookingType;
	}

	@Override
	public void setCookingType(String type) {
		this.cookingType = type;
	}

	@Override
	public String getProduct() {
		return product;
	}

	@Override
	public void setProduct(String product) {
		this.product = product;
	}

	@Override
	public String getFirstSupply() {
		return firstSupplyName;
	}

	@Override
	public void setFirstSupply(String firstSupply) {
		this.firstSupplyName = firstSupply;
	}

	@Override
	public String getSecondSupply() {
		return secondSupplyName;
	}

	@Override
	public void setSecondSupply(String secondSupply) {
		this.secondSupplyName = secondSupply;
	}

	@Override
	public String getLogs() {
		return logsName;
	}

	@Override
	public void setLogs(String logs) {
		this.logsName = logs;
	}

	@Override
	public int getAmount() {
		return amountToProcess;
	}

	@Override
	public void setAmount(int amount) {
		this.amountToProcess = amount;
		this.amountLeft = amount;
	}

	@Override
	public int getAmountLeft() {
		return amountLeft;
	}

	@Override
	public void setAmountLeft(int amountLeft) {
		this.amountLeft = amountLeft;
	}

	@Override
	public int getStopLevel() {
		return stopLevel;
	}

	@Override
	public void setStopLevel(int level) {
		this.stopLevel = level;
	}

	@Override
	public void decreaseAmountLeft(int amount) {
		this.amountLeft -= amount;
	}

	@Override
	public String getKeybind() {
		return this.keybind;
	}

	@Override
	public String toString() {
		String type = this.getCookingType();
		String firstSupply = this.getFirstSupply();
		String secondSupply = this.getSecondSupply();
		String product = this.getProduct();
		String logs = this.getLogs();
		String amount = this.getAmountLeft() > 1000000000 ? "Undefined" : String.valueOf(this.getAmountLeft());
		String stopLevel = this.getStopLevel() <= 0 ? "Undefined" : String.valueOf(this.getStopLevel());

		switch (type) {
			case FIRE_COOKING:
				//"Type="+FIRE_COOKING+", Supply=%s, Product=%s, Logs=%s, Amount=%s, Stop level=%s";
				return String.format(Locale.US, FIRE_COOKING_FORMAT, firstSupply, product, logs, amount, stopLevel);
			case ROGUES_DEN_COOKING:
				//"Type="+ROGUES_DEN_COOKING+", Supply=%s, Product=%s, Amount=%s, Stop level=%s";
				return String.format(Locale.US, ROGUES_DEN_COOKING_FORMAT, firstSupply, product, amount, stopLevel);
			case RANGE_COOKING:
				//"Type="+RANGE_COOKING+", Supply=%s, Product=%s, Amount=%s, Stop level=%s";
				return String.format(Locale.US, RANGE_COOKING_FORMAT, firstSupply, product, amount, stopLevel);
			case MAKE_ITEM_COOKING:
				//"Type="+MAKE_ITEM_COOKING+", First supply=%s, Second supply=%s, Product=%s, Amount=%s, Stop level=%s";
				return String.format(Locale.US, MAKE_ITEM_COOKING_FORMAT, firstSupply, secondSupply, product, amount, stopLevel);
			default:
				return "Invalid!";
		}
	}

}
