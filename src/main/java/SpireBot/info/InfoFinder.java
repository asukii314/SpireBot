package SpireBot.info;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;

import java.util.ArrayList;

public class InfoFinder {
    public static String getSeed() {
        if (CardCrawlGame.isInARun())
            return "The seed is: " + SeedHelper.getUserFacingSeedString();
        else
            return "Not currently in a run so cannot determine seed.";
    }

    public static String getHelp() {
        return "SpireBot is a brand new mod for reporting Slay the Spire information to Twitch chat. You can query the bot using two word pairs - a prefix '!spire' followed by the actual command. Try '!spire list' for a list of commands.";
    }

    public static String getInfo() {
        if (CardCrawlGame.isInARun()) {
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

            return sb.toString();
        }
        else
            return "Not currently in a run!";
    }

    public static String getError() {
        return "ERROR: Not a valid command. Try '!spire list' for a list of possible commands, or '!spire help' for information about this bot.";
    }

    public static String getList() {
        return "SpireBot recognizes the following commands: [ help, list, info, seed ]. Use them following the bot prefix e.g. '!spire info'.";
    }
}
