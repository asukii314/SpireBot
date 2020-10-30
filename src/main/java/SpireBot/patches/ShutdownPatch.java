package SpireBot.patches;

import SpireBot.ThreadHelper;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;

@SpirePatch(
        clz = CardCrawlGame.class,
        method = "dispose"
)
public class ShutdownPatch {
    @SpirePrefixPatch
    public static void Prefix(CardCrawlGame __instance) {
        // TODO: this isn't working as intended, so this entire patch is useless (no easy way to shutdown threads)
        ThreadHelper.kill();
    }

}
