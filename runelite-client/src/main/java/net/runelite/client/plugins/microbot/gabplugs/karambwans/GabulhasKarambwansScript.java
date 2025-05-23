package net.runelite.client.plugins.microbot.gabplugs.karambwans;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.Notifier;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.gabplugs.karambwans.GabulhasKarambwansInfo.*;
import static net.runelite.client.plugins.microbot.gabplugs.karambwans.GabulhasKarambwansInfo.botStatus;

@Slf4j
public class GabulhasKarambwansScript extends Script {
    public static double version = 1.0;
    @Inject
    private Notifier notifier;

    private WorldPoint zanarisRing = new WorldPoint(2412, 4434, 0);

    private WorldPoint bankPoint = new WorldPoint(2381, 4455, 0);

    public boolean run(GabulhasKarambwansConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                switch (botStatus) {
                    case FISHING:
                        fishingLoop();
                        botStatus = states.WALKING_TO_RING_TO_BANK;
                        sleep(10000, 20000);
                        break;
                    case WALKING_TO_RING_TO_BANK:
                        walkToRingToBank();
                        botStatus = states.WALKING_TO_BANK;
                        sleep(100, 3000);

                        break;
                    case WALKING_TO_BANK:
                        doBank();
                        botStatus = states.BANKING;
                        sleep(100, 3000);

                        break;
                    case BANKING:
                        useBank();
                        botStatus = states.WALKING_TO_RING_TO_FISH;
                        sleep(100, 3000);
                        break;
                    case WALKING_TO_RING_TO_FISH:
                        walkToRingToFish();
                        botStatus = states.FISHING;
                        sleep(100, 3000);
                        break;
                }

            } catch (Exception ex) {
                Microbot.logStackTrace(this.getClass().getSimpleName(), ex);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }


    private void fishingLoop() {
        while (!Rs2Inventory.isFull() && super.isRunning()) {
            sleep(100, 3000);
            if (!Rs2Player.isInteracting() || !Rs2Player.isAnimating()) {
                if (Rs2Inventory.contains("Raw karambwanji")) {
                    interactWithFishingSpot();
                } else {
                    while (true) {
                    }
                }
            }
        }
    }

    private void walkToRingToBank() {
        Rs2GameObject.interact(29495, "Zanaris");
        Rs2Bank.walkToBank(BankLocation.ZANARIS);

        sleepUntil(() -> Rs2Player.getWorldLocation().equals(zanarisRing));
    }

    private void doBank() {
        Rs2Bank.openBank();
    }

    private void useBank() {
        Rs2Bank.depositAll(3142);
    }

    private void interactWithFishingSpot() {
        Rs2Npc.interact(4712, "Fish");
    }

    private void walkToRingToFish() {
        Rs2Walker.walkTo(2412,4435,0);
        var fairyRing = Rs2GameObject.findObjectById(29560);
        if(!Objects.isNull(fairyRing)) {
            Rs2GameObject.interact(fairyRing, "Last-destination (DKP)");
        }

        Rs2Walker.walkTo(2899,3118,0);
    }
}

