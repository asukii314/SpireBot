package SpireBot;

import SpireBot.utils.Credentials;
import SpireBot.utils.ThreadHelper;
import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

@SpireInitializer
public class SpireBot implements PostInitializeSubscriber {
    public static void initialize() { new SpireBot(); }

    public SpireBot() {
        BaseMod.subscribe(this);
    }

    @Override
    public void receivePostInitialize() {
        if (ThreadHelper.start(Credentials.loadFromJSON("mods/spirebot_cred.json"))) {
            System.out.println("Successfully launched thread");
        }
    }
}
