package lunastic.erosion_era;

import lunastic.erosion_era.init.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ErosionEraMod implements ModInitializer {

	public static final String ID = "erosion_era";
	public static final String NAME = "Erosion Era";
	public static final String VERSION = "${version}";

	public static final Logger LOGGER = LogManager.getLogger(ID);



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ErErGroups.init();
		ErErItems.init();
		ErErBlocks.init();
		ErErBlockItems.init();

	}

	public static Identifier identifier(String id){
		return new Identifier(ErosionEraMod.ID, id);
	}
}