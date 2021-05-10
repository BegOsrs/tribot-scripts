package scripts.cooker;

import scripts.nodes.bank.RoguesDenBanker;
import scripts.nodes.cook.RoguesDenCooker;
import scripts.nodes.walk.RoguesDenWalker;

public class CookingOnRoguesDen extends Cooking {

	public CookingOnRoguesDen() {
		super.addNodes(new RoguesDenBanker(), new RoguesDenWalker(), new RoguesDenCooker());
	}

}
