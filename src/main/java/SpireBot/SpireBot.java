package SpireBot;

import SpireBot.utils.Credentials;
import SpireBot.utils.ThreadHelper;
import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;

import java.util.ArrayList;

@SpireInitializer
public class SpireBot implements PostInitializeSubscriber, StartGameSubscriber {
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

    private void updateSeed() {
        String seed = SeedHelper.getUserFacingSeedString();
        System.out.println("Seed is: " + seed);
    }

    private void updateInfo() {
        StringBuilder sb = new StringBuilder("Currently playing as ");
        sb.append(AbstractDungeon.player.title);
        sb.append(". We're in ");
        sb.append(AbstractDungeon.name);
        sb.append(", floor ");
        sb.append(AbstractDungeon.floorNum);

        ArrayList<String> bosses = AbstractDungeon.bossList;
        if (!bosses.isEmpty()) {
            sb.append(", with the upcoming boss being ");
            sb.append(bosses.get(0));
        }
        sb.append(". Type \"!spire list\" in chat for more commands.");

        String res = sb.toString();
    }

    @Override
    public void receiveStartGame() {
        System.out.println("OJB: started game");
        if (CardCrawlGame.isInARun()) {
            updateSeed();
            updateInfo();
        }
    }
}
