package scripts.data;

import org.tribot.api2007.types.RSTile;
import scripts.api.game.areas.RSMultipleArea;
import scripts.api.beg.constants.Place;

public enum RangeSettings {

	EDGEVILLE(Place.Bank.EDGEVILLE, new RSTile(3079, 3497, 0),
		new RSMultipleArea(
			new RSTile(3078, 3495, 0), new RSTile(3078, 3495, 0), new RSTile(3078, 3494, 0),
			new RSTile(3079, 3494, 0), new RSTile(3079, 3495, 0), new RSTile(3080, 3495, 0),
			new RSTile(3079, 3496, 0), new RSTile(3080, 3496, 0), new RSTile(3081, 3496, 0),
			new RSTile(3081, 3495, 0), new RSTile(3080, 3494, 0), new RSTile(3077, 3494, 0),
			new RSTile(3078, 3493, 0), new RSTile(3079, 3493, 0), new RSTile(3077, 3492, 0),
			new RSTile(3078, 3492, 0), new RSTile(3079, 3492, 0), new RSTile(3080, 3492, 0),
			new RSTile(3081, 3491, 0), new RSTile(3080, 3491, 0), new RSTile(3079, 3491, 0),
			new RSTile(3078, 3491, 0), new RSTile(3078, 3490, 0), new RSTile(3079, 3490, 0),
			new RSTile(3080, 3490, 0), new RSTile(3079, 3489, 0)),
		new RSMultipleArea(
			new RSTile(3080, 3500, 0), new RSTile(3079, 3500, 0), new RSTile(3079, 3499, 0),
			new RSTile(3080, 3499, 0), new RSTile(3080, 3498, 0), new RSTile(3079, 3498, 0),
			new RSTile(3079, 3497, 0), new RSTile(3080, 3497, 0), new RSTile(3081, 3497, 0),
			new RSTile(3081, 3498, 0), new RSTile(3078, 3497, 0), new RSTile(3078, 3498, 0))
	),
	FALADOR(Place.Bank.FALADOR_EAST, new RSTile(3038, 3361, 0),
		new RSMultipleArea(
			new RSTile(3038, 3362, 0), new RSTile(3037, 3362, 0), new RSTile(3037, 3363, 0),
			new RSTile(3038, 3363, 0), new RSTile(3039, 3363, 0), new RSTile(3039, 3362, 0),
			new RSTile(3040, 3362, 0), new RSTile(3040, 3363, 0), new RSTile(3041, 3363, 0),
			new RSTile(3041, 3364, 0), new RSTile(3040, 3364, 0), new RSTile(3040, 3365, 0),
			new RSTile(3040, 3366, 0), new RSTile(3041, 3366, 0), new RSTile(3041, 3367, 0),
			new RSTile(3039, 3366, 0), new RSTile(3038, 3366, 0), new RSTile(3038, 3367, 0),
			new RSTile(3037, 3367, 0), new RSTile(3037, 3366, 0)),
		new RSMultipleArea(
			new org.tribot.api2007.types.RSArea(new RSTile(3037, 3360, 0), new RSTile(3039, 3361, 0)).getAllTiles())
	),
	LUMBRIDGE(Place.Bank.LUMBRIDGE,
		new RSMultipleArea(
			new RSTile(3208, 3212, 0), new RSTile(3208, 3213, 0), new RSTile(3209, 3213, 0),
			new RSTile(3209, 3212, 0), new RSTile(3210, 3212, 0), new RSTile(3210, 3213, 0),
			new RSTile(3211, 3213, 0), new RSTile(3211, 3214, 0), new RSTile(3211, 3215, 0),
			new RSTile(3210, 3215, 0), new RSTile(3210, 3216, 0), new RSTile(3211, 3216, 0))
	),
	AL_KHARID(Place.Bank.AL_KHARID, new RSTile(3275, 3180, 0),
		new RSMultipleArea(
			new RSTile(3275, 3179, 0), new RSTile(3275, 3181, 0), new RSTile(3274, 3181, 0),
			new RSTile(3274, 3180, 0), new RSTile(3274, 3179, 0), new RSTile(3273, 3179, 0),
			new RSTile(3273, 3180, 0), new RSTile(3272, 3179, 0), new RSTile(3271, 3179, 0),
			new RSTile(3272, 3180, 0), new RSTile(3272, 3181, 0), new RSTile(3272, 3182, 0)),
		new RSMultipleArea(
			new RSTile(3275, 3180, 0), new RSTile(3276, 3181, 0), new RSTile(3277, 3181, 0),
			new RSTile(3277, 3180, 0), new RSTile(3276, 3180, 0), new RSTile(3277, 3179, 0),
			new RSTile(3276, 3179, 0), new RSTile(3276, 3178, 0), new RSTile(3277, 3178, 0),
			new RSTile(3278, 3179, 0), new RSTile(3275, 3178, 0))
	),
	COOKING_GUILD(Place.Bank.COOKING_GUILD,
		new RSMultipleArea(
			new RSTile(3147, 3452, 0), new RSTile(3146, 3452, 0), new RSTile(3145, 3452, 0),
			new RSTile(3144, 3452, 0), new RSTile(3148, 3451, 0), new RSTile(3147, 3451, 0),
			new RSTile(3146, 3451, 0), new RSTile(3145, 3451, 0), new RSTile(3144, 3451, 0),
			new RSTile(3146, 3450, 0), new RSTile(3145, 3450, 0))
	),
	VARROCK_EAST(Place.Bank.VARROCK_EAST, new RSTile(3242, 3412, 0),
		new RSMultipleArea(
			new RSTile(3238, 3413, 0), new RSTile(3239, 3413, 0), new RSTile(3240, 3413, 0),
			new RSTile(3241, 3413, 0), new RSTile(3241, 3412, 0), new RSTile(3240, 3412, 0),
			new RSTile(3239, 3412, 0), new RSTile(3238, 3412, 0), new RSTile(3237, 3411, 0),
			new RSTile(3238, 3411, 0), new RSTile(3239, 3411, 0), new RSTile(3240, 3411, 0),
			new RSTile(3239, 3410, 0), new RSTile(3238, 3410, 0), new RSTile(3237, 3410, 0),
			new RSTile(3236, 3410, 0), new RSTile(3236, 3409, 0)),
		new RSMultipleArea(
			new org.tribot.api2007.types.RSArea(new RSTile(3244, 3411, 0), new RSTile(3242, 3416, 0)).getAllTiles())
	),
	CATHERBY(Place.Bank.CATHERBY,
		new RSMultipleArea(
			new RSTile(2816, 3443, 0), new RSTile(2817, 3443, 0), new RSTile(2818, 3443, 0),
			new RSTile(2816, 3442, 0), new RSTile(2818, 3442, 0), new RSTile(2818, 3441, 0),
			new RSTile(2818, 3440, 0), new RSTile(2816, 3441, 0), new RSTile(2815, 3440, 0),
			new RSTile(2816, 3440, 0), new RSTile(2815, 3439, 0), new RSTile(2816, 3439, 0))
	),
	NARDAH(Place.Bank.NARDAH,
		new RSMultipleArea(
			new RSTile(3433, 2887, 0), new RSTile(3434, 2887, 0), new RSTile(3434, 2888, 0),
			new RSTile(3433, 2888, 0), new RSTile(3432, 2888, 0), new RSTile(3435, 2886, 0),
			new RSTile(3432, 2886, 0))
	),
	HOSIDIUS(Place.Bank.HOSIDIUS,
		new RSMultipleArea(
			new RSTile(1654, 3606, 0), new RSTile(1655, 3606, 0), new RSTile(1656, 3606, 0),
			new RSTile(1657, 3606, 0), new RSTile(1654, 3607, 0), new RSTile(1655, 3607, 0),
			new RSTile(1656, 3607, 0), new RSTile(1657, 3607, 0), new RSTile(1654, 3608, 0),
			new RSTile(1655, 3608, 0), new RSTile(1656, 3608, 0), new RSTile(1657, 3608, 0),
			new RSTile(1658, 3608, 0), new RSTile(1654, 3609, 0), new RSTile(1655, 3609, 0),
			new RSTile(1656, 3609, 0), new RSTile(1657, 3609, 0), new RSTile(1658, 3609, 0),
			new RSTile(1654, 3610, 0), new RSTile(1655, 3610, 0), new RSTile(1656, 3610, 0),
			new RSTile(1657, 3610, 0), new RSTile(1658, 3610, 0), new RSTile(1653, 3611, 0),
			new RSTile(1654, 3611, 0), new RSTile(1655, 3611, 0), new RSTile(1656, 3611, 0),
			new RSTile(1657, 3611, 0), new RSTile(1658, 3611, 0), new RSTile(1653, 3612, 0),
			new RSTile(1654, 3612, 0), new RSTile(1655, 3612, 0), new RSTile(1656, 3612, 0),
			new RSTile(1657, 3612, 0), new RSTile(1658, 3612, 0), new RSTile(1659, 3612, 0),
			new RSTile(1654, 3613, 0), new RSTile(1656, 3613, 0), new RSTile(1658, 3613, 0),
			new RSTile(1659, 3613, 0))
	);

	public final Place.Bank bank;
	public final RSTile doorTile;
	public final RSMultipleArea rangeArea;
	public final RSMultipleArea walkToArea;

	RangeSettings(Place.Bank bank, RSMultipleArea area) {
		this(bank, null, area, area);
	}

	RangeSettings(Place.Bank bank, RSTile doorTile, RSMultipleArea rangeArea, RSMultipleArea walkingArea) {
		this.bank = bank;
		this.doorTile = doorTile;
		this.rangeArea = rangeArea;
		this.walkToArea = walkingArea;
	}

}