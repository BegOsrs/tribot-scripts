package scripts.data;

import org.tribot.api2007.types.RSTile;
import scripts.api.game.areas.RSMultipleArea;
import scripts.api.beg.constants.Place;

public enum FireSettings {

	LUMBRIDGE(Place.Bank.LUMBRIDGE,
		new RSMultipleArea(
			new RSTile(3205, 3217, 2), new RSTile(3206, 3217, 2), new RSTile(3206, 3218, 2),
			new RSTile(3206, 3219, 2), new RSTile(3205, 3219, 2), new RSTile(3205, 3220, 2),
			new RSTile(3206, 3220, 2), new RSTile(3205, 3221, 2), new RSTile(3206, 3222, 2))
	),

	VARROCK_WEST(Place.Bank.VARROCK_WEST,
		new RSMultipleArea(
			new RSTile(3183, 3448, 0), new RSTile(3182, 3448, 0), new RSTile(3181, 3449, 0),
			new RSTile(3182, 3449, 0), new RSTile(3183, 3449, 0), new RSTile(3184, 3450, 0),
			new RSTile(3182, 3450, 0), new RSTile(3185, 3449, 0), new RSTile(3184, 3448, 0))
	),

	VARROCK_EAST(Place.Bank.VARROCK_EAST,
		new RSMultipleArea(
			new RSTile(3255, 3426, 0), new RSTile(3255, 3427, 0), new RSTile(3254, 3428, 0),
			new RSTile(3254, 3429, 0), new RSTile(3255, 3429, 0), new RSTile(3243, 3429, 0),
			new RSTile(3252, 3428, 0), new RSTile(3251, 3427, 0), new RSTile(3252, 3426, 0))
	),

	GRAND_EXCHANGE(Place.Bank.GRAND_EXCHANGE,
		new RSMultipleArea(
			new RSTile(3161, 3493, 0), new RSTile(3160, 3489, 0), new RSTile(3160, 3490, 0),
			new RSTile(3160, 3491, 0), new RSTile(3159, 3491, 0), new RSTile(3159, 3490, 0),
			new RSTile(3159, 3489, 0))
	),

	DUEL_ARENA(Place.Bank.DUEL_ARENA,
		new RSMultipleArea(
			new RSTile(3385, 3267, 0), new RSTile(3386, 3267, 0), new RSTile(3386, 3266, 0),
			new RSTile(3386, 3265, 0), new RSTile(3385, 3266, 0), new RSTile(3385, 3267, 0),
			new RSTile(3384, 3266, 0), new RSTile(3384, 3265, 0), new RSTile(3383, 3266, 0),
			new RSTile(3383, 3265, 0), new RSTile(3382, 3266, 0), new RSTile(3382, 3265, 0),
			new RSTile(3381, 3266, 0), new RSTile(3381, 3265, 0), new RSTile(3380, 3266, 0),
			new RSTile(3380, 3265, 0), new RSTile(3379, 3266, 0), new RSTile(3379, 3265, 0))
	),

	DRAYNOR_VILLAGE(Place.Bank.DRAYNOR_VILLAGE,
		new RSMultipleArea(
			new RSTile(3091, 3247, 0), new RSTile(3091, 3248, 0), new RSTile(3090, 3248, 0),
			new RSTile(3090, 3249, 0), new RSTile(3091, 3249, 0), new RSTile(3092, 3249, 0),
			new RSTile(3092, 3248, 0), new RSTile(3092, 3248, 0), new RSTile(3093, 3248, 0),
			new RSTile(3093, 3249, 0), new RSTile(3094, 3249, 0), new RSTile(3094, 3248, 0))
	),

	TZHAAR_CITY(Place.Bank.TZHAAR_CITY,
		new RSMultipleArea(
			new RSTile(2443, 5173, 0), new RSTile(2443, 5172, 0), new RSTile(2442, 5171, 0),
			new RSTile(2443, 5172, 0), new RSTile(2444, 5172, 0), new RSTile(2444, 5173, 0),
			new RSTile(2445, 5173, 0), new RSTile(2445, 5172, 0), new RSTile(2445, 5171, 0))
	),

	CASTLE_WARS(Place.Bank.CASTLE_WARS,
		new RSMultipleArea(
			new RSTile(2442, 3083, 0), new RSTile(2442, 3084, 0), new RSTile(2441, 3084, 0),
			new RSTile(2441, 3085, 0), new RSTile(2442, 3085, 0))
	),

	CLAN_WARS(Place.Bank.CLAN_WARS,
		new RSMultipleArea(
			new RSTile(3368, 3173, 0), new RSTile(3367, 3173, 0), new RSTile(3367, 3174, 0),
			new RSTile(3368, 3174, 0), new RSTile(3369, 3174, 0), new RSTile(3369, 3173, 0),
			new RSTile(3368, 3175, 0), new RSTile(3367, 3175, 0), new RSTile(3370, 3173, 0),
			new RSTile(3371, 3173, 0), new RSTile(3372, 3173, 0))
	),

	AL_KHARID(Place.Bank.AL_KHARID,
		new RSMultipleArea(
			new RSTile(3273, 3168, 0), new RSTile(3275, 3165, 0), new RSTile(3274, 3167, 0),
			new RSTile(3274, 3168, 0), new RSTile(3274, 3165, 0), new RSTile(3273, 3165, 0),
			new RSTile(3273, 3166, 0), new RSTile(3273, 3167, 0), new RSTile(3274, 3169, 0))
	),

	YANILLE(Place.Bank.YANILLE,
		new RSMultipleArea(
			new RSTile(2607, 3090, 0), new RSTile(2608, 3091, 0), new RSTile(2608, 3092, 0),
			new RSTile(2608, 3093, 0), new RSTile(2608, 3094, 0), new RSTile(2607, 3093, 0),
			new RSTile(2607, 3093, 0), new RSTile(2607, 3092, 0), new RSTile(2606, 3093, 0))
	),

	ARDOUGNE_WEST(Place.Bank.ARDOUGNE_WEST,
		new RSMultipleArea(
			new RSTile(2615, 3335, 0), new RSTile(2616, 3335, 0), new RSTile(2617, 3335, 0),
			new RSTile(2618, 3335, 0), new RSTile(2618, 3336, 0), new RSTile(2617, 3336, 0),
			new RSTile(2616, 3336, 0), new RSTile(2615, 3336, 0), new RSTile(2617, 3337, 0))
	),

	ARDOUGNE_EAST(Place.Bank.ARDOUGNE_EAST,
		new RSMultipleArea(
			new RSTile(2648, 3284, 0), new RSTile(2648, 3283, 0), new RSTile(2648, 3282, 0),
			new RSTile(2647, 3283, 0), new RSTile(2647, 3284, 0), new RSTile(2646, 3283, 0))
	),

	SEERS(Place.Bank.SEERS,
		new RSMultipleArea(
			new RSTile(2724, 3486, 0), new RSTile(2725, 3486, 0), new RSTile(2726, 3486, 0),
			new RSTile(2726, 3485, 0), new RSTile(2725, 3485, 0), new RSTile(2726, 3484, 0))
	),

	CATHERBY(Place.Bank.CATHERBY,
		new RSMultipleArea(
			new RSTile(2809, 3437, 0), new RSTile(2803, 3437, 0), new RSTile(2803, 3436, 0),
			new RSTile(2809, 3436, 0), new RSTile(2810, 3436, 0), new RSTile(2809, 3435, 0))
	),

	FALADOR_WEST(Place.Bank.FALADOR_WEST,
		new RSMultipleArea(
			new RSTile(2945, 3374, 0), new RSTile(2946, 3374, 0), new RSTile(2947, 3374, 0),
			new RSTile(2946, 3375, 0), new RSTile(2945, 3375, 0), new RSTile(2947, 3375, 0),
			new RSTile(2946, 3376, 0))
	),

	FALADOR_EAST(Place.Bank.FALADOR_EAST,
		new RSMultipleArea(
			new RSTile(3012, 3359, 0), new RSTile(3013, 3359, 0), new RSTile(3014, 3359, 0),
			new RSTile(3013, 3360, 0), new RSTile(3012, 3360, 0), new RSTile(3012, 3361, 0),
			new RSTile(3011, 3360, 0))
	),

	EDGEVILLE(Place.Bank.EDGEVILLE,
		new RSMultipleArea(
			new RSTile(3090, 3492, 0), new RSTile(3090, 3491, 0), new RSTile(3090, 3490, 0),
			new RSTile(3089, 3491, 0), new RSTile(3089, 3490, 0), new RSTile(3089, 3489, 0))
	),

	NARDAH(Place.Bank.NARDAH,
		new RSMultipleArea(
			new RSTile(3431, 2892, 0), new RSTile(3431, 2891, 0), new RSTile(3432, 2892, 0),
			new RSTile(3432, 2891, 0), new RSTile(3432, 2890, 0), new RSTile(3432, 2893, 0),
			new RSTile(3433, 2892, 0), new RSTile(3433, 2891, 0), new RSTile(3434, 2891, 0)));

	private final Place.Bank bank;
	private final RSMultipleArea fireArea;

	FireSettings(Place.Bank bank, RSMultipleArea fireArea) {
		this.bank = bank;
		this.fireArea = fireArea;
	}

	public Place.Bank getBank() {
		return bank;
	}

	public RSMultipleArea getFireArea() {
		return fireArea;
	}
}
