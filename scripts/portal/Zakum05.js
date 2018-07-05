function enter(pi) {
	var playa = pi.getPlayer();
	if (playa.getMap().getReactorByName("gate").getState() != 1) {
		//pi.warp(211042400, "west00");
		if (playa.getZakumLevel() > 0 && pi.haveItem(4001017, 1)) {
			pi.warp(211042400, "west00");
			//pi.playerMessage(5, "You have been warped to Zakum's Entrance.");
			return true;
		} else {
			pi.playerMessage(5, "You must have cleared stage three to proceed. You also need to be in possession of an Eye of Fire.");
			return false;
		}
		return false;
	} else {
		pi.playerMessage(5, "The fight with Zakum has already started. Please change channels, or try again later.");
		return false;
	}
}