package scripts.cooker;

import static scripts.data.VariablesCooker.vars;
import scripts.nodes.bank.InventoryBanker;
import scripts.nodes.cook.InventoryCooker;

public class CookingOnInventory extends Cooking {

	public CookingOnInventory() {
		super.addNodes(new InventoryBanker(), new InventoryCooker());
		if (vars.firstSupply.contains("Part")) {
			this.setPieID();
		}
	}

	private void setPieID() {
		int id = -1;
		switch (vars.product) {
			case "Part mud pie 2":
				id = 7164;
				break;
			case "Raw mud pie":
				id = 7166;
				break;
			case "Part garden pie 2":
				id = 7172;
				break;
			case "Raw garden pie":
				id = 7174;
				break;
			case "Part fish pie 2":
				id = 7182;
				break;
			case "Raw fish pie":
				id = 7184;
				break;
			case "Part admiral pie 2":
				id = 7192;
				break;
			case "Raw admiral pie":
				id = 7194;
				break;
			case "Part wild pie 2":
				id = 7202;
				break;
			case "Raw wild pie":
				id = 7204;
				break;
			case "Part summer pie 2":
				id = 7212;
				break;
			case "Raw summer pie":
				id = 7214;
				break;
			default:
				break;
		}

		vars.firstSupply = null;
		vars.firstSupplyID = id;
	}

}
