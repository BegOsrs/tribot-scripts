package scripts.data;

public enum ItemCombinerInterfaceIds {

	____COOKING____(-1, -1),
	COOKING_ITEMS(270, 14),
	BREAD_DOUGH(270, 14),
	PASTRY_DOUGH(270, 15),
	PIZZA_DOUGH(270, 16),
	PITTA_DOUGH(270, 17),
	CUT_PINEAPPLE_RINGS(270, 14),
	CUT_PINEAPPLE_CHUNKS(270, 15),

	____CRAFTING____(-1, -1),
	DHIDE_BODY(270, 14),
	DHIDE_VAMBS(270, 15),
	DHIDE_CHAPS(270, 16),
	GEMS(270, 14),
	HARDLEATHER_BODY(270, 14), // TODO UNTESTED
	BATTLESTAFF(270, 14),
	LEATHER_ARMOUR(154, 90),
	LEATHER_GLOVES(154, 93),
	LEATHER_BOOTS(154, 96),
	LEATHER_VAMBS(154, 99),
	LEATHER_CHAPS(154, 102),
	LEATHER_COIF(154, 105),
	LEATHER_COWL(154, 107),

	____FLETCHING____(-1, -1),
	ARROW_SHAFT(270, 14),
	SHORTBOW_U(270, 15), // 16 child for regular shortbow u
	SHORTBOW(270, 14),
	LONGBOW_U(270, 16), // 17 child for regular longbow u
	LONGBOW(270, 14),

	____HERBLORE____(-1, -1),
	POTIONS(270, 14);

	public final int masterId;
	public final int childId;
	public final int componentId;

	ItemCombinerInterfaceIds(int masterId, int childId) {
		this(masterId, childId, -1);
	}

	ItemCombinerInterfaceIds(int masterId, int childId, int componentId) {
		this.masterId = masterId;
		this.childId = childId;
		this.componentId = componentId;
	}

}
