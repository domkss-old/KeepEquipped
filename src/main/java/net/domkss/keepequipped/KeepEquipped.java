package net.domkss.keepequipped;

import net.fabricmc.api.ModInitializer;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeepEquipped implements ModInitializer {

    public static Logger logger;


    @Override
    public void onInitialize() {
        initLogger(true);
    }

    public static void initLogger(boolean debug) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tT] [%4$s] [KeepEquipped] %5$s %n");
        logger = Logger.getLogger("KeepEquipped");
        if (debug) logger.setLevel(Level.CONFIG);
        else logger.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(logger.getLevel());
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.info("Successfully loaded!");
        logger.config("Debug Mode Enabled!");
    }
}
