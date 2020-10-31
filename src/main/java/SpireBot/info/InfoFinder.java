package SpireBot.info;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoFinder {
    public static String getSeed() {
        if (CardCrawlGame.isInARun())
            return "The seed is: " + SeedHelper.getUserFacingSeedString();
        else
            return "Not currently in a run!";
    }

    public static String getHelp(String prefix) {
        return "SpireBot is a mod to report Slay the Spire information to Twitch chat. You can query the bot using two word pairs - a prefix '" + prefix + "' followed by the actual command. Try '" + prefix + "list' for a list of valid commands.";
    }

//    public static String getHelp() {
//        return "SpireBot is a brand new mod for reporting Slay the Spire information to Twitch chat. You can query the bot using two word pairs - a prefix '!spire' followed by the actual command. Try '!spire list' for a list of commands.";
//    }

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

    public static String getRelics() {
        if (CardCrawlGame.isInARun()) {
            ArrayList<String> relics = AbstractDungeon.player.getRelicNames();
            return relics.toString();
        }

        return "Not currently in a run!";
    }

    public static String getAct1() {
        if (CardCrawlGame.isInARun()) {
            BetterMetrics metrics = new BetterMetrics();
            metrics.build();

            System.out.println("Better metrics consists of: ");
            for (Object key : metrics.params.keySet()) {
                System.out.println(" [*] " + key.toString());
                System.out.println(metrics.params.get(key).toString());
                System.out.println();
            }
            System.out.println("--------------");

            StringBuilder sb = new StringBuilder();
            // card_choices | damage_taken | path_per_floor

            // NOTE:
            // path_per_floor shows what we had on each tile, e.g. [M, ?, M, ?]       <- notice the 3rd floor was a ? floor that rolled a monster
            // path_taken shows what we pathed through originally, e.g. [M, ?, ?, ?]

            // event_choices is really really complicated map, but is definitely useful. probably start off with just .event_name and .player_choice maybe?
            // card_choices is also really interesting to look at


            ArrayList<HashMap> dt = CardCrawlGame.metricData.damage_taken;
            for (HashMap floor : dt) {
                if (floor.containsKey("floor")) {
                    String floor_num = floor.get("floor").toString();

                    if (floor.containsKey("damage")) {
                        String damage = floor.get("damage").toString();

                        if (floor.containsKey("enemies")) {
                            String enemies = floor.get("enemies").toString();

                            sb.append("On floor " + floor_num + ", we took " + damage + " damage to " + enemies + ". ");
                        }
                    }
                }
            }

            //System.out.println(sb.toString());
            return sb.toString();

//            Metrics metrics = new Metrics();
//            //ReflectionHacks
//            try {
//                Method method = Metrics.class.getDeclaredMethod("gatherAllData", boolean.class, boolean.class, MonsterGroup.class);
//                method.setAccessible(true);
//                method.invoke(metrics);
//
//                metrics.
//            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
            //private void gatherAllData(boolean death, boolean trueVictor, MonsterGroup monsters) {

            //return "";
        }
        else {
            return "Not currently in a run!";
        }
    }
}
