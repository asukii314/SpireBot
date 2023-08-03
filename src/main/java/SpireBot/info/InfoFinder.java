package SpireBot.info;

import basemod.BaseMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.localization.PotionStrings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class InfoFinder {

    private static final String notRun = "Not currently in a run!";

    public static String getSeed() {
        return (CardCrawlGame.isInARun()) ? "The seed is: " + SeedHelper.getUserFacingSeedString() : notRun;
    }

    public static String getHelp(String prefix) {
        return "SpireBot is a mod to report Slay the Spire information to Twitch chat. You can query the bot using two word pairs - a prefix '" + prefix + "' followed by the actual command. Try '" + prefix + "list' for a list of valid commands.";
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
        } else
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
        // DEBUG ONLY (TODO: comment out)
//        BetterMetrics metrics = new BetterMetrics();
//        metrics.build();
//        metrics.print();
        //-------------

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
        StringBuilder sb = new StringBuilder();
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
        if (!CardCrawlGame.isInARun())
            return notRun;

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
                    String picked = RelicLibrary.getRelic(k.get("picked").toString()).name;

                    sb.append("At the end of act " + act++ + ", we chose " + picked + " over " + RelicLibrary.getRelic(notPicked.get(0)).name + " and " + RelicLibrary.getRelic(notPicked.get(1)).name + ". ");
                } else if (notPicked.size() == 3) {
                    sb.append("At the end of act " + act++ + ", we took nothing and skipped " + RelicLibrary.getRelic(notPicked.get(0)).name + ", " + RelicLibrary.getRelic(notPicked.get(1)).name + ", and " + RelicLibrary.getRelic(notPicked.get(2)).name + ". ");
                }
            }
        }

        return sb.toString();
    }

    // TODO: this is far too long for late game. Need to split it by act perhaps?
    public static String hp() {
        if (!CardCrawlGame.isInARun())
            return notRun;

        ArrayList<Integer> curr = CardCrawlGame.metricData.current_hp_per_floor;
        ArrayList<Integer> max = CardCrawlGame.metricData.max_hp_per_floor;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < curr.size() && i < max.size(); ++i) {
            sb.append("Floor " + (i + 1) + ": [" + curr.get(i) + " / " + max.get(i) + "]. ");
        }

        return sb.toString();
    }

    // card_choices: [{not_picked=[Anger, Headbutt], picked=Pommel Strike, floor=1.0}, {not_picked=[Iron Wave, Bloodletting], picked=Inflame, floor=3.0}, {not_picked=[Dropkick, Intimidate, Iron Wave], picked=SKIP, floor=6.0}, {not_picked=[Clash, True Grit], picked=Evolve, floor=7.0}, {not_picked=[Clash, Evolve], picked=Dual Wield, floor=7.0}, {not_picked=[Reckless Charge, Dropkick], picked=Anger, floor=7.0}, {not_picked=[Anger, Perfected Strike], picked=Clothesline, floor=7.0}, {not_picked=[Searing Blow, Heavy Blade], picked=Thunderclap, floor=7.0}, {not_picked=[Warcry, Burning Pact, Whirlwind], picked=SKIP, floor=11.0}, {not_picked=[Pommel Strike, Anger], picked=Limit Break, floor=12.0}, {not_picked=[Headbutt, Perfected Strike, Whirlwind], picked=SKIP, floor=14.0}, {not_picked=[Double Tap, Barricade], picked=Limit Break, floor=16.0}, {not_picked=[Anger, Inflame], picked=Heavy Blade, floor=18.0}, {not_picked=[Twin Strike, Rampage, Burning Pact], picked=SKIP, floor=21}, {not_picked=[Searing Blow, Intimidate, Iron Wave], picked=SKIP, floor=25}]
    // TODO: probably doesn't work for shops / orrery but idk
    public static String slicesSkipped() {
        if (!CardCrawlGame.isInARun())
            return notRun;

        int timesSeen = 0;
        int timesPickedOther = 0;
        int timesPicked = 0;

        ArrayList<HashMap> choices = CardCrawlGame.metricData.card_choices;
        for (HashMap m : choices) {
            boolean skippedSlice = false;

            if (m.containsKey("not_picked")) {
                ArrayList<String> notPicked = (ArrayList<String>) m.get("not_picked");
                if (notPicked.contains("Slice")) {
                    skippedSlice = true;
                }
            }

            if (m.containsKey("picked")) {
                String picked = m.get("picked").toString();

                if (picked == "Slice") {
                    timesSeen++;
                    timesPicked++;
                } else if (skippedSlice) {
                    timesSeen++;
                    timesPickedOther++;
                }
            }
        }

        if (timesSeen == 0)
            return "We haven't seen any Slices FeelsBadMan";
        else {
            String res = "We have seen " + timesSeen + " Slices and picked " + timesPicked + " of them.";
            if (timesPickedOther > 0)
                res += " We took something else " + timesPickedOther + " times. For shame!";
            else
                res += " We took them all! PogChamp";
            return res;
        }

    }

    public static String neowOptions() {
        if (!CardCrawlGame.isInARun())
            return notRun;

        StringBuilder sb = new StringBuilder();
        sb.append("We chose: ");
        sb.append(formatNeowInfo(CardCrawlGame.metricData.neowBonus, CardCrawlGame.metricData.neowCost));
        sb.append(formatNeowInfoDetail(CardCrawlGame.metricData.neowBonus, CardCrawlGame.metricData.neowCost));
        JsonArray bonusesSkipped = BaseMod.getSaveFields().get("NeowBonusesSkippedLog").onSaveRaw().getAsJsonArray();
        JsonArray costsSkipped = BaseMod.getSaveFields().get("NeowCostsSkippedLog").onSaveRaw().getAsJsonArray();

        sb.append(" We skipped:");
        for (int i = 0; i < bonusesSkipped.size(); i++) {
            sb.append(" (");
            sb.append(i+1);
            sb.append(") ");
            sb.append(formatNeowInfo(bonusesSkipped.get(i).toString(), costsSkipped.get(i).toString()));
        }

        return sb.toString();
    }

   private static String formatNeowInfo(String neowBonusRaw, String neowCostRaw){
        final HashMap<String, String> neow_bonuses = new HashMap<String, String>() {{
            put("THREE_CARDS", "Choose one of three cards to obtain.");
            put("RANDOM_COLORLESS", "Choose an uncommon Colorless card to obtain.");
            put("RANDOM_COMMON_RELIC", "Obtain a random common relic.");
            put("REMOVE_CARD", "Remove a card.");
            put("TRANSFORM_CARD", "Transform a card.");
            put("UPGRADE_CARD", "Upgrade a card.");
            put("THREE_ENEMY_KILL", "Enemies in the first three combats have 1 HP.");
            put("THREE_SMALL_POTIONS", "Gain 3 random potions.");
            put("TEN_PERCENT_HP_BONUS", "Gain 10% Max HP.");
            put("ONE_RANDOM_RARE_CARD", "Gain a random Rare card.");
            put("HUNDRED_GOLD", "Gain 100 gold.");
            put("TWO_FIFTY_GOLD", "Gain 250 gold.");
            put("TWENTY_PERCENT_HP_BONUS", "Gain 20% Max HP.");
            put("RANDOM_COLORLESS_2", "Choose a Rare colorless card to obtain.");
            put("THREE_RARE_CARDS", "Choose a Rare card to obtain.");
            put("REMOVE_TWO", "Remove two cards.");
            put("TRANSFORM_TWO_CARDS", "Transform two cards.");
            put("ONE_RARE_RELIC", "Obtain a random Rare relic.");
            put("BOSS_RELIC", "Lose your starter relic. Obtain a random Boss relic.");
        }};

        final HashMap<String, String> neow_costs = new HashMap<String, String>() {{
            put("CURSE", "Gain a curse.");
            put("NO_GOLD", "Lose all gold.");
            put("TEN_PERCENT_HP_LOSS", "Lose 10% Max HP.");
            put("PERCENT_DAMAGE", "Take damage.");
            put("TWO_STARTER_CARDS", "Add two starter cards.");
            put("ONE_STARTER_CARD", "Add a starter card.");
        }};

        if (neowBonusRaw == null) {
            return "No neow option picked yet.";
        }

       String neowBonus = neow_bonuses.get(clean(neowBonusRaw));
       String neowCost = neow_costs.get(clean(neowCostRaw));

       if (neowCost == null) {
            return neowBonus;
        } else {
            return neowCost + " " + neowBonus;
        }
    }

    private static String clean(String s){
        // sometimes strings will appear with quotation marks around them, other times they won't
        // normalize here so keys are consistent
        return s.replace("\"", "");
    }

    private static String formatNeowInfoDetail(String neowBonusRaw, String neowCostRaw){
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        JsonObject bonusData = BaseMod.getSaveFields().get("NeowBonusLog").onSaveRaw().getAsJsonObject();
        JsonArray cardList = bonusData.get("cardsObtained").getAsJsonArray();

        if(Objects.equals(neowCostRaw, "CURSE")) {
            sb.append("We got ");
            sb.append(CardLibrary.getCard(clean(cardList.get(0).toString())));
            sb.append(". ");
        }

        switch(neowBonusRaw){
            case "TEN_PERCENT_HP_BONUS":
            case "TWENTY_PERCENT_HP_BONUS":
            case "THREE_ENEMY_KILL":
            case "HUNDRED_GOLD":
            case "TWO_FIFTY_GOLD":
                if(Objects.equals(neowCostRaw, "CURSE")) {
                    return sb.toString();
                }
                return "";

            case "THREE_SMALL_POTIONS":
                ArrayList<String> potions = new ArrayList();
                ArrayList<HashMap> allPotionData = CardCrawlGame.metricData.potions_obtained;
                for(int i = 0; i < allPotionData.size(); i++) {
                    if((Integer) allPotionData.get(i).get("floor") == 0) {
                        potions.add(allPotionData.get(i).get("key").toString());
                    }
                }
                if(potions.size() == 0) {
                    sb.append("We didn't take any of the three potions");
                    break;
                }

                sb.append("We obtained ");
                sb.append(PotionHelper.getPotion(potions.get(0)).name);

                if(potions.size() == 2) {
                    sb.append(" and ");
                    sb.append(PotionHelper.getPotion(potions.get(1)).name);
                } else if(potions.size() == 3) {
                    sb.append(", ");
                    sb.append(PotionHelper.getPotion(potions.get(1)).name);
                    sb.append(", and ");
                    sb.append(PotionHelper.getPotion(potions.get(2)).name);
                }
                break;

            case "THREE_CARDS":
            case "THREE_RARE_CARDS":
            case "RANDOM_COLORLESS":    // choose uncommon colorless
            case "RANDOM_COLORLESS_2":  // choose rare colorless
                ArrayList<HashMap> cardChoices = CardCrawlGame.metricData.card_choices;
                if(cardChoices.size() > 0) {
                    HashMap firstCardChoice = cardChoices.get(0);
                    if ((Integer) firstCardChoice.get("floor") == 0) {
                        String picked = firstCardChoice.get("picked").toString();
                        ArrayList<String> notPicked = (ArrayList<String>) firstCardChoice.get("not_picked");

                        // skip all
                        if(Objects.equals(picked, "SKIP") || (notPicked != null && notPicked.size() == 3)) {
                            sb.append("We were offered ");
                            sb.append(CardLibrary.getCard(notPicked.get(0)).name);
                            sb.append(", ");
                            sb.append(CardLibrary.getCard(notPicked.get(1)).name);
                            sb.append(", and ");
                            sb.append(CardLibrary.getCard(notPicked.get(2)).name);
                            sb.append(", but skipped them all.");
                            break;
                        }

                        // choose one
                        sb.append("We picked ");
                        sb.append(CardLibrary.getCard(picked).name);
                        if (notPicked != null && notPicked.size() == 2) {
                            sb.append(" over ");
                            sb.append(CardLibrary.getCard(notPicked.get(0)).name);
                            sb.append(" and ");
                            sb.append(CardLibrary.getCard(notPicked.get(1)).name);
                        }
                        break;
                    }
                }
                sb.append("We didn't pick any of the three cards offered");
                break;

            case "RANDOM_COMMON_RELIC":
            case "ONE_RARE_RELIC":
            case "BOSS_RELIC":   // TODO: see if there's transform stats for pbox/astrolabe
                sb.append("We got ");
                JsonArray relicList = bonusData.get("relicsObtained").getAsJsonArray();
                sb.append(RelicLibrary.getRelic(clean(relicList.get(0).toString())));

                break;

            case "TRANSFORM_CARD":
            case "TRANSFORM_TWO_CARDS":
                sb.append("We transformed ");
                JsonArray transformedList = bonusData.get("cardsTransformed").getAsJsonArray();
                sb.append(CardLibrary.getCard(clean(transformedList.get(0).toString())));
                if(transformedList.size() == 2) {
                    sb.append(" and ");
                    sb.append(CardLibrary.getCard(clean(transformedList.get(1).toString())));
                }
                sb.append(" into ");
                if(Objects.equals(neowCostRaw, "CURSE")) {
                    sb.append(CardLibrary.getCard(clean(cardList.get(1).toString())));
                    sb.append(" and ");
                    sb.append(CardLibrary.getCard(clean(cardList.get(2).toString())));
                } else {
                    sb.append(CardLibrary.getCard(clean(cardList.get(0).toString())));
                    if (cardList.size() == 2) {
                        sb.append(" and ");
                        sb.append(CardLibrary.getCard(clean(cardList.get(1).toString())));
                    }
                }
                break;

            case "ONE_RANDOM_RARE_CARD":
                sb.append("We got ");
                sb.append(CardLibrary.getCard(clean(cardList.get(0).toString())));
                break;

            case "REMOVE_CARD":
            case "REMOVE_TWO":
                sb.append("We removed ");
                JsonArray removedList = bonusData.get("cardsRemoved").getAsJsonArray();
                sb.append(CardLibrary.getCard(clean(removedList.get(0).toString())));
                if(removedList.size() == 2) {
                    sb.append(" and ");
                    sb.append(CardLibrary.getCard(clean(removedList.get(1).toString())));
                }
                break;

            case "UPGRADE_CARD":
                sb.append("We upgraded ");
                JsonArray upgradeList = bonusData.get("cardsUpgraded").getAsJsonArray();
                sb.append(CardLibrary.getCard(clean(upgradeList.get(0).toString())));
                break;

        }
        sb.append(".");
        return sb.toString();
    }

    public static String bossInfo() {
        if (!CardCrawlGame.isInARun())
            return notRun;

        // probably doesn't work with downfall and stuff but idc
        final int actBossFloors[] = {0,16,33,50,56};
        StringBuilder sb = new StringBuilder();
        int floorsAway = actBossFloors[AbstractDungeon.actNum] - AbstractDungeon.floorNum;

        if(floorsAway <= 0) {
            sb.append("We are currently in a boss fight.");

        } else {
            sb.append("We are fighting ");
            ArrayList<String> bosses = AbstractDungeon.bossList;
            if (bosses.isEmpty()) {
                sb.append("an unknown boss");
            } else {
                sb.append(bosses.get(0));
            }
            sb.append(" in ");
            sb.append(floorsAway);
            sb.append(" floor");
            if(floorsAway != 1)
                sb.append("s");
            sb.append(".");
        }

        return sb.toString();
    }
}