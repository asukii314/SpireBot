package SpireBot.info;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import java.util.HashMap;

public class BetterMetrics {
    public HashMap<Object, Object> params = new HashMap();

    private void addData(Object key, Object value) {
        this.params.put(key, value);
    }

    public void build() {
        //this.addData("play_id", UUID.randomUUID().toString());
        this.addData("build_version", CardCrawlGame.TRUE_VERSION_NUM);
        this.addData("seed_played", Settings.seed.toString());
        this.addData("chose_seed", Settings.seedSet);
        this.addData("seed_source_timestamp", Settings.seedSourceTimestamp);
        this.addData("is_daily", Settings.isDailyRun);
        this.addData("special_seed", Settings.specialSeed);
        if (ModHelper.enabledMods.size() > 0) {
            this.addData("daily_mods", ModHelper.getEnabledModIDs());
        }
        this.addData("is_trial", Settings.isTrial);
        this.addData("is_endless", Settings.isEndless);
//        if (death) {
//            AbstractPlayer player = AbstractDungeon.player;
//            CardCrawlGame.metricData.current_hp_per_floor.add(player.currentHealth);
//            CardCrawlGame.metricData.max_hp_per_floor.add(player.maxHealth);
//            CardCrawlGame.metricData.gold_per_floor.add(player.gold);
//        }
        this.addData("is_ascension_mode", AbstractDungeon.isAscensionMode);
        this.addData("ascension_level", AbstractDungeon.ascensionLevel);
        this.addData("neow_bonus", CardCrawlGame.metricData.neowBonus);
        this.addData("neow_cost", CardCrawlGame.metricData.neowCost);
        this.addData("is_beta", Settings.isBeta);
        this.addData("is_prod", Settings.isDemo);
//        this.addData("victory", !death);
        this.addData("floor_reached", AbstractDungeon.floorNum);
//        if (trueVictor) {
//            this.addData("score", VictoryScreen.calcScore(!death));
//        } else {
//            this.addData("score", DeathScreen.calcScore(!death));
//        }

//        this.lastPlaytimeEnd = System.currentTimeMillis() / 1000L;
//        this.addData("timestamp", this.lastPlaytimeEnd);
//        this.addData("local_time", timestampFormatter.format(Calendar.getInstance().getTime()));

        this.addData("playtime", (long)CardCrawlGame.playtime);
        this.addData("player_experience", Settings.totalPlayTime);
        this.addData("master_deck", AbstractDungeon.player.masterDeck.getCardIdsForMetrics());
        this.addData("relics", AbstractDungeon.player.getRelicNames());
        this.addData("gold", AbstractDungeon.player.gold);
        this.addData("campfire_rested", CardCrawlGame.metricData.campfire_rested);
        this.addData("campfire_upgraded", CardCrawlGame.metricData.campfire_upgraded);
        this.addData("purchased_purges", CardCrawlGame.metricData.purchased_purges);
        this.addData("potions_floor_spawned", CardCrawlGame.metricData.potions_floor_spawned);
        this.addData("potions_floor_usage", CardCrawlGame.metricData.potions_floor_usage);
        this.addData("current_hp_per_floor", CardCrawlGame.metricData.current_hp_per_floor);
        this.addData("max_hp_per_floor", CardCrawlGame.metricData.max_hp_per_floor);
        this.addData("gold_per_floor", CardCrawlGame.metricData.gold_per_floor);
        this.addData("path_per_floor", CardCrawlGame.metricData.path_per_floor);
        this.addData("path_taken", CardCrawlGame.metricData.path_taken);
        this.addData("items_purchased", CardCrawlGame.metricData.items_purchased);
        this.addData("item_purchase_floors", CardCrawlGame.metricData.item_purchase_floors);
        this.addData("items_purged", CardCrawlGame.metricData.items_purged);
        this.addData("items_purged_floors", CardCrawlGame.metricData.items_purged_floors);
        this.addData("character_chosen", AbstractDungeon.player.chosenClass.name());
        this.addData("card_choices", CardCrawlGame.metricData.card_choices);
        this.addData("event_choices", CardCrawlGame.metricData.event_choices);
        this.addData("boss_relics", CardCrawlGame.metricData.boss_relics);
        this.addData("damage_taken", CardCrawlGame.metricData.damage_taken);
        this.addData("potions_obtained", CardCrawlGame.metricData.potions_obtained);
        this.addData("relics_obtained", CardCrawlGame.metricData.relics_obtained);
        this.addData("campfire_choices", CardCrawlGame.metricData.campfire_choices);
        this.addData("circlet_count", AbstractDungeon.player.getCircletCount());
        Prefs pref = AbstractDungeon.player.getPrefs();
        int numVictory = pref.getInteger("WIN_COUNT", 0);
        int numDeath = pref.getInteger("LOSE_COUNT", 0);
        if (numVictory <= 0) {
            this.addData("win_rate", Float.valueOf(0.0f));
        } else {
            this.addData("win_rate", numVictory / (numDeath + numVictory));
        }
//        if (death && monsters != null) {
//            this.addData("killed_by", AbstractDungeon.lastCombatMetricKey);
//        } else {
//            this.addData("killed_by", null);
//        }
    }
}
