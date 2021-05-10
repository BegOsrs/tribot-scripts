package scripts.nodes.bank;

import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import scripts.api.game.inventory.Inventory;
import static scripts.data.VariablesCooker.vars;

public class InventoryDoughBanker extends InventoryBanker {

	@Override
	public boolean validate() {
		return super.validate() ||
			(Login.getLoginState() == STATE.INGAME &&
				(Inventory.getCount(vars.firstSupply) > 9 || Inventory.getCount(vars.secondSupply) > 9));
	}

}
