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
import java.util.Map;
import java.util.TreeMap;

public class InfoFinder {
    private static final String notRun = "Not currently in a run!";

    public static String getSeed() {
        return (CardCrawlGame.isInARun()) ? "The seed is: " + SeedHelper.getUserFacingSeedString() : notRun;
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
            return notRun;
    }

    public static String getRelics() {
        if (CardCrawlGame.isInARun()) {
            ArrayList<String> relics = AbstractDungeon.player.getRelicNames();
            return relics.toString();
        }

        return notRun;
    }

    private static String buildActData(int minFloor, int maxFloor) {
        // DEBUG ONLY (can comment out)
        BetterMetrics metrics = new BetterMetrics();
        metrics.build();
        metrics.print();
        //-------------

        StringBuilder sb = new StringBuilder();
        TreeMap<Integer, String> floor_data = new TreeMap<>();

        // FIGHTS
        ArrayList<HashMap> dt = CardCrawlGame.metricData.damage_taken;
        for (HashMap floor : dt) {
            if (floor.containsKey("floor")) {
                int floor_num = Math.round(Float.parseFloat(floor.get("floor").toString()));

                // Only take events in bounds
                if (floor_num < minFloor || floor_num > maxFloor)
                    continue;

                if (floor.containsKey("damage")) {
                    int damage = Math.round(Float.parseFloat(floor.get("damage").toString()));

                    if (floor.containsKey("enemies")) {
                        String enemies = floor.get("enemies").toString();
                        floor_data.put(floor_num, "Floor " + floor_num + ": " + damage + " damage to " + enemies + ". ");
                    }
                }
            }
        }

        // EVENTS
        ArrayList<HashMap> ec = CardCrawlGame.metricData.event_choices;
        for (HashMap floor : ec) {
            if (floor.containsKey("floor")) {
                int floor_num = Math.round(Float.parseFloat(floor.get("floor").toString()));

                // Only take events in bounds
                if (floor_num < minFloor || floor_num > maxFloor)
                    continue;

                if (floor.containsKey("event_name")) {
                    String name = floor.get("event_name").toString();

                    if (floor.containsKey("player_choice")) {
                        String choice = floor.get("player_choice").toString();

                        floor_data.put(floor_num, "Floor " + floor_num + ": " + name + " Event (" + choice + "). ");
                    }
                }
            }
        }

        // Combine them in order of floor
        for (String s : floor_data.values())
            sb.append(s);

        return sb.toString();
    }

    public static String getAct1() {
        return (CardCrawlGame.isInARun()) ? buildActData(0, 17) : notRun;
    }

    public static String getAct2() {
        return (CardCrawlGame.isInARun()) ? buildActData(17, 34) : notRun;
    }

    public static String getAct3() {
        return (CardCrawlGame.isInARun()) ? buildActData(34, 51) : notRun;
    }

    public static String getAct4() {
        return (CardCrawlGame.isInARun()) ? buildActData(51, 55) : notRun;
    }

    public static String bossRelics() {
        ArrayList<HashMap> boss_relics = CardCrawlGame.metricData.boss_relics;
        StringBuilder sb = new StringBuilder();

        // boss_relics: [{not_picked=[Busted Crown, Coffee Dripper], picked=Pandora's Box}]
        int act = 1;
        for (HashMap k : boss_relics) {
            if (k.containsKey("not_picked")) {
                ArrayList<String> notPicked = (ArrayList<String>) k.get("not_picked");
                if (notPicked.size() < 2)
                    continue;

                if (k.containsKey("picked")) {
                    String picked = k.get("picked").toString();

                    sb.append("At the end of act " + act++ + ", we chose " + picked + " over " + notPicked.get(0) + " and " + notPicked.get(1) + ". ");
                }
                else if (notPicked.size() == 3) {
                    sb.append("At the end of act " + act++ + ", we took nothing and skipped " + notPicked.get(0) + ", " + notPicked.get(1) + ", and " + notPicked.get(2) + ". ");
                }
            }
        }

        return sb.toString();
    }

    public static String hp() {
        ArrayList<Integer> curr = CardCrawlGame.metricData.current_hp_per_floor;
        ArrayList<Integer> max = CardCrawlGame.metricData.max_hp_per_floor;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < curr.size() && i < max.size(); ++i) {
            sb.append("Floor " + (i+1) + ": [" + curr.get(i) + " / " + max.get(i) + "]. ");
        }

        return sb.toString();
    }
}
