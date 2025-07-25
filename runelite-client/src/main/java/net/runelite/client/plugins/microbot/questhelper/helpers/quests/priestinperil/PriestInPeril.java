/*
 * Copyright (c) 2020, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.priestinperil;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirements;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class PriestInPeril extends BasicQuestHelper
{
	//Items Required
	ItemRequirement runeEssence, lotsOfRuneEssence, bucket, weaponAndArmour, goldenKey, rangedMagedGear,
		murkyWater, ironKey, blessedWaterHighlighted, bucketHighlighted, goldenKeyHighlighted;

	//Items Recommended
	ItemRequirement runePouches, varrockTeleport;

	Requirement inUnderground, hasGoldenOrIronKey, inTempleGroundFloor, inTemple, inTempleFirstFloor, inTempleSecondFloor;

	QuestStep talkToRoald, goToTemple, goDownToDog, killTheDog, climbUpAfterKillingDog, returnToKingRoald, returnToTemple, killMonk, talkToDrezel,
		goUpToFloorTwoTemple, goUpToFloorOneTemple, goDownToFloorOneTemple, goDownToGroundFloorTemple, enterUnderground, fillBucket, useKeyForKey,
		openDoor, useBlessedWater, blessWater, goUpWithWaterToSurface, goUpWithWaterToFirstFloor, goUpWithWaterToSecondFloor, talkToDrezelAfterFreeing,
		goDownToFloorOneAfterFreeing, goDownToGroundFloorAfterFreeing, enterUndergroundAfterFreeing, talkToDrezelUnderground, bringDrezelEssence;

	//Zones
	Zone underground, temple1, temple2, temple3, temple4, temple5, temple6, templeFloorOne, templeFloorTwo;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRoald);
		steps.put(1, goToTemple);

		ConditionalStep goDownAndKillDog = new ConditionalStep(this, goDownToDog);
		goDownAndKillDog.addStep(inUnderground, killTheDog);

		steps.put(2, goDownAndKillDog);

		ConditionalStep reportKillingDog = new ConditionalStep(this, returnToKingRoald);
		reportKillingDog.addStep(inUnderground, climbUpAfterKillingDog);
		steps.put(3, reportKillingDog);

		ConditionalStep goTalkToDrezel = new ConditionalStep(this, returnToTemple);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleSecondFloor), talkToDrezel);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleFirstFloor), goUpToFloorTwoTemple);
		goTalkToDrezel.addStep(new Conditions(hasGoldenOrIronKey, inTempleGroundFloor), goUpToFloorOneTemple);
		goTalkToDrezel.addStep(inTemple, killMonk);
		steps.put(4, goTalkToDrezel);

		ConditionalStep goGetKey = new ConditionalStep(this, returnToTemple);
		goGetKey.addStep(new Conditions(ironKey, murkyWater, inTempleSecondFloor), openDoor);
		goGetKey.addStep(new Conditions(ironKey, murkyWater, inTempleFirstFloor), goUpWithWaterToSecondFloor);
		goGetKey.addStep(new Conditions(ironKey, murkyWater, inUnderground), goUpWithWaterToSurface);
		goGetKey.addStep(new Conditions(ironKey, murkyWater), goUpWithWaterToFirstFloor);
		goGetKey.addStep(new Conditions(ironKey, inUnderground), fillBucket);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inUnderground), useKeyForKey);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inTempleSecondFloor), goDownToFloorOneTemple);
		goGetKey.addStep(new Conditions(hasGoldenOrIronKey, inTempleFirstFloor), goDownToGroundFloorTemple);
		goGetKey.addStep(hasGoldenOrIronKey, enterUnderground);
		goGetKey.addStep(inTemple, killMonk);
		steps.put(5, goGetKey);

		ConditionalStep goGetWater = new ConditionalStep(this, enterUnderground);
		goGetWater.addStep(new Conditions(blessedWaterHighlighted, inTempleSecondFloor), useBlessedWater);
		goGetWater.addStep(new Conditions(murkyWater, inTempleSecondFloor), blessWater);
		goGetWater.addStep(new Conditions(murkyWater, inTempleFirstFloor), goUpWithWaterToSecondFloor);
		goGetWater.addStep(new Conditions(murkyWater, inUnderground), goUpWithWaterToSurface);
		goGetWater.addStep(murkyWater, goUpWithWaterToFirstFloor);
		goGetWater.addStep(inUnderground, fillBucket);
		goGetWater.addStep(inTempleSecondFloor, goDownToFloorOneTemple);
		goGetWater.addStep(inTempleFirstFloor, goDownToGroundFloorTemple);
		steps.put(6, goGetWater);

		ConditionalStep goTalkToDrezelAfterFreeing = new ConditionalStep(this, goUpWithWaterToFirstFloor);
		goTalkToDrezelAfterFreeing.addStep(inTempleSecondFloor, talkToDrezelAfterFreeing);
		goTalkToDrezelAfterFreeing.addStep(inTempleFirstFloor, goUpWithWaterToSecondFloor);
		steps.put(7, goTalkToDrezelAfterFreeing);

		ConditionalStep goDownToDrezel = new ConditionalStep(this, enterUndergroundAfterFreeing);
		goDownToDrezel.addStep(inUnderground, talkToDrezelUnderground);
		goDownToDrezel.addStep(inTempleFirstFloor, goDownToGroundFloorAfterFreeing);
		goDownToDrezel.addStep(inTempleSecondFloor, goDownToFloorOneAfterFreeing);
		steps.put(8, goDownToDrezel);
		steps.put(9, goDownToDrezel);

		steps.put(10, bringDrezelEssence);
		steps.put(11, bringDrezelEssence);
		steps.put(12, bringDrezelEssence);
		steps.put(13, bringDrezelEssence);
		steps.put(14, bringDrezelEssence);
		steps.put(15, bringDrezelEssence);
		steps.put(16, bringDrezelEssence);
		steps.put(17, bringDrezelEssence);
		steps.put(18, bringDrezelEssence);
		steps.put(19, bringDrezelEssence);
		steps.put(20, bringDrezelEssence);
		steps.put(21, bringDrezelEssence);
		steps.put(22, bringDrezelEssence);
		steps.put(23, bringDrezelEssence);
		steps.put(24, bringDrezelEssence);
		steps.put(25, bringDrezelEssence);
		steps.put(26, bringDrezelEssence);
		steps.put(27, bringDrezelEssence);
		steps.put(28, bringDrezelEssence);
		steps.put(29, bringDrezelEssence);
		steps.put(30, bringDrezelEssence);
		steps.put(31, bringDrezelEssence);
		steps.put(32, bringDrezelEssence);
		steps.put(33, bringDrezelEssence);
		steps.put(34, bringDrezelEssence);
		steps.put(35, bringDrezelEssence);
		steps.put(36, bringDrezelEssence);
		steps.put(37, bringDrezelEssence);
		steps.put(38, bringDrezelEssence);
		steps.put(39, bringDrezelEssence);
		steps.put(40, bringDrezelEssence);
		steps.put(41, bringDrezelEssence);
		steps.put(42, bringDrezelEssence);
		steps.put(43, bringDrezelEssence);
		steps.put(44, bringDrezelEssence);
		steps.put(45, bringDrezelEssence);
		steps.put(46, bringDrezelEssence);
		steps.put(47, bringDrezelEssence);
		steps.put(48, bringDrezelEssence);
		steps.put(49, bringDrezelEssence);
		steps.put(50, bringDrezelEssence);
		steps.put(51, bringDrezelEssence);
		steps.put(52, bringDrezelEssence);
		steps.put(53, bringDrezelEssence);
		steps.put(54, bringDrezelEssence);
		steps.put(55, bringDrezelEssence);
		steps.put(56, bringDrezelEssence);
		steps.put(57, bringDrezelEssence);
		steps.put(58, bringDrezelEssence);
		steps.put(59, bringDrezelEssence);
		// There is a 60th step before the final 61, but the 'Quest Completed!' message pops up prior to it

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		runeEssence = new ItemRequirement("Rune or Pure Essence", ItemID.BLANKRUNE, 50);
		runeEssence.addAlternates(ItemID.BLANKRUNE_HIGH);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY).isNotConsumed();
		bucketHighlighted = bucket.highlighted();
		runePouches = new ItemRequirements(LogicType.OR, "Essence pouches for carrying essence",
			new ItemRequirement("Small pouch", ItemID.RCU_POUCH_SMALL),
			new ItemRequirement("Medium pouch", ItemID.RCU_POUCH_MEDIUM),
			new ItemRequirement("Large pouch", ItemID.RCU_POUCH_LARGE),
			new ItemRequirement("Giant pouch", ItemID.RCU_POUCH_GIANT)
		).isNotConsumed();
		varrockTeleport = new ItemRequirement("Varrock teleports", ItemID.POH_TABLET_VARROCKTELEPORT, 3);
		weaponAndArmour = new ItemRequirement("Ranged or melee weapon + armour", -1, -1).isNotConsumed();
		weaponAndArmour.setDisplayItemId(BankSlotIcons.getCombatGear());
		goldenKey = new ItemRequirement("Golden key", ItemID.PIPKEY_GOLD);
		goldenKeyHighlighted = new ItemRequirement("Golden key", ItemID.PIPKEY_GOLD);
		goldenKeyHighlighted.setHighlightInInventory(true);
		rangedMagedGear = new ItemRequirement("Combat gear, ranged or mage to safespot", -1, -1).isNotConsumed();
		rangedMagedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		lotsOfRuneEssence = new ItemRequirement("As much essence as you can carry, you'll need to bring 50 UNNOTED in total", ItemID.BLANKRUNE_HIGH, -1);
		murkyWater = new ItemRequirement("Murky water", ItemID.BUCKET_MURKYWATER);
		murkyWater.addAlternates(ItemID.BUCKET_BLESSEDWATER);
		ironKey = new ItemRequirement("Iron key", ItemID.PIPKEY_IRON);
		blessedWaterHighlighted = new ItemRequirement("Blessed water", ItemID.BUCKET_BLESSEDWATER);
		blessedWaterHighlighted.setHighlightInInventory(true);
	}

	@Override
	protected void setupZones()
	{
		underground = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		temple1 = new Zone(new WorldPoint(3409, 3483, 0), new WorldPoint(3411, 3494, 0));
		temple2 = new Zone(new WorldPoint(3408, 3485, 0), new WorldPoint(3408, 3486, 0));
		temple3 = new Zone(new WorldPoint(3408, 3491, 0), new WorldPoint(3408, 3492, 0));
		temple4 = new Zone(new WorldPoint(3412, 3484, 0), new WorldPoint(3415, 3493, 0));
		temple5 = new Zone(new WorldPoint(3416, 3483, 0), new WorldPoint(3417, 3494, 0));
		temple6 = new Zone(new WorldPoint(3418, 3484, 0), new WorldPoint(3418, 3493, 0));
		templeFloorOne = new Zone(new WorldPoint(3408, 3483, 1), new WorldPoint(3419, 3494, 1));
		templeFloorTwo = new Zone(new WorldPoint(3408, 3483, 2), new WorldPoint(3419, 3494, 2));
	}

	public void setupConditions()
	{
		inUnderground = new ZoneRequirement(underground);
		inTempleGroundFloor = new ZoneRequirement(temple1, temple2, temple3, temple4, temple5, temple6);
		inTempleFirstFloor = new ZoneRequirement(templeFloorOne);
		inTempleSecondFloor = new ZoneRequirement(templeFloorTwo);
		inTemple = new ZoneRequirement(temple1, temple2, temple3, temple4, temple5, temple6, templeFloorOne, templeFloorTwo);

		hasGoldenOrIronKey = new Conditions(LogicType.OR, goldenKey, ironKey);
	}

	public void setupSteps()
	{
		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_CUTSCENE, new WorldPoint(3222, 3473, 0), "Speak to King Roald in Varrock Castle.");
		talkToRoald.addDialogStep("I'm looking for a quest!");
		talkToRoald.addDialogStep("Yes.");
		goToTemple = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORR, new WorldPoint(3408, 3488, 0),
			"Go to the temple east of Varrock by the river and click on the large door.", weaponAndArmour);
		goToTemple.addDialogSteps("I'll get going.", "Roald sent me to check on Drezel.", "Sure. I'm a helpful person!");
		goDownToDog = new ObjectStep(this, ObjectID.TRAPDOOR, new WorldPoint(3405, 3507, 0), "Go down the ladder north of the temple.");
		goDownToDog.addDialogStep("Yes.");
		((ObjectStep) (goDownToDog)).addAlternateObjects(ObjectID.TRAPDOOR_OPEN);
		killTheDog = new NpcStep(this, NpcID.PRIESTPERIL_GUARDIAN_MODEL, new WorldPoint(3405, 9901, 0),
			"Kill the Temple Guardian (level 30). It is immune to magic so you will need to use either ranged or melee.");
		climbUpAfterKillingDog = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0),
			"Climb back up the ladder and return to King Roald.");
		returnToKingRoald = new NpcStep(this, NpcID.KING_ROALD_CUTSCENE, new WorldPoint(3222, 3473, 0),
			"Return to King Roald.");
		returnToKingRoald.addSubSteps(climbUpAfterKillingDog);

		returnToTemple = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORR, new WorldPoint(3408, 3488, 0),
			"Return to the temple.", bucket, lotsOfRuneEssence, rangedMagedGear);
		killMonk = new NpcStep(this, NpcID.PRIESTPERILEVILMONK3, new WorldPoint(3412, 3488, 0), "Kill a Monk of Zamorak (level 30) for a golden key. You can safespot using the pews.", true, goldenKey);

		goUpToFloorOneTemple = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(3418, 3493, 0), "Go upstairs.");
		goUpToFloorTwoTemple = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(3410, 3485, 1), "Climb up the ladder.");
		talkToDrezel = new NpcStep(this, NpcID.PRIESTPERILTRAPPEDMONK_VIS, new WorldPoint(3418, 3489, 2), "Talk to Drezel on the top floor of the temple.");
		talkToDrezel.addDialogSteps("So, what now?", "Yes, of course.");
		talkToDrezel.addSubSteps(goUpToFloorOneTemple, goUpToFloorTwoTemple);

		fillBucket = new ObjectStep(this, ObjectID.PRIESTPERIL_WELL, new WorldPoint(3423, 9890, 0), "Use the bucket on the well in the central room.", bucketHighlighted);
		fillBucket.addIcon(ItemID.BUCKET_EMPTY);

		useKeyForKey = new DetailedQuestStep(this, "Go to the central room, and study the monuments to find which has a key on it. Use the Golden Key on it.", goldenKeyHighlighted);
		useKeyForKey.addIcon(ItemID.PIPKEY_GOLD);

		goDownToFloorOneTemple = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(3410, 3485, 2), "Go down to the underground of the temple.", bucket);
		goDownToGroundFloorTemple = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP, new WorldPoint(3417, 3485, 0), "Go down to the underground of the temple.", bucket);
		enterUnderground = new ObjectStep(this, ObjectID.TRAPDOOR, new WorldPoint(3405, 3507, 0), "Go down to the underground of the temple.", bucket);
		enterUnderground.addSubSteps(goDownToFloorOneTemple, goDownToGroundFloorTemple);
		((ObjectStep) (enterUnderground)).addAlternateObjects(ObjectID.TRAPDOOR_OPEN);

		goUpWithWaterToSurface = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToFirstFloor = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(3418, 3493, 0),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToSecondFloor = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(3410, 3485, 1),
			"Go back up to the top floor of the temple.");
		goUpWithWaterToSecondFloor.addSubSteps(goUpWithWaterToSurface, goUpWithWaterToFirstFloor);

		openDoor = new ObjectStep(this, ObjectID.PIP_PRISONDOOR, new WorldPoint(3415, 3489, 2), "Open the cell door.", ironKey);
		blessWater = new NpcStep(this, NpcID.PRIESTPERILTRAPPEDMONK_VIS, new WorldPoint(3418, 3489, 2), "Talk to Drezel to bless the water.", murkyWater);
		useBlessedWater = new ObjectStep(this, ObjectID.PRIESTPERIL_COFFIN_NOANIM, new WorldPoint(3413, 3487, 2), "Use the blessed water on the coffin.", blessedWaterHighlighted);
		useBlessedWater.addIcon(ItemID.BUCKET_BLESSEDWATER);

		talkToDrezelAfterFreeing = new NpcStep(this, NpcID.PRIESTPERILTRAPPEDMONK_VIS, new WorldPoint(3418, 3489, 2), "Talk to Drezel again.");

		goDownToFloorOneAfterFreeing = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(3410, 3485, 2), "Go down to the underground of the temple.", lotsOfRuneEssence);
		goDownToGroundFloorAfterFreeing = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP, new WorldPoint(3417, 3485, 0), "Go down to the underground of the temple.", lotsOfRuneEssence);
		enterUndergroundAfterFreeing = new ObjectStep(this, ObjectID.TRAPDOOR, new WorldPoint(3405, 3507, 0), "Go down to the underground of the temple.", lotsOfRuneEssence);
		((ObjectStep) (enterUndergroundAfterFreeing)).addAlternateObjects(ObjectID.TRAPDOOR_OPEN);
		talkToDrezelUnderground = new NpcStep(this, NpcID.PRIESTPERILTRAPPEDMONK_VIS, new WorldPoint(3439, 9896, 0), "Talk to Drezel in the east of the underground temple area.", lotsOfRuneEssence);
		talkToDrezelUnderground.addSubSteps(goDownToFloorOneAfterFreeing, goDownToGroundFloorAfterFreeing, enterUndergroundAfterFreeing);

		bringDrezelEssence = new BringDrezelPureEssenceStep(this);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(runePouches);

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(runeEssence);
		reqs.add(bucket);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Temple Guardian (level 30). You cannot use Magic. Rings of recoil will not award the kill.",  "Monk of Zamorak (level 30)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.PRAYER, 1406));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Wolfbane Dagger", ItemID.DAGGER_WOLFBANE, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Canifis and Morytania"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", Collections.singletonList(talkToRoald)));
		allSteps.add(new PanelDetails("Go to the temple", Arrays.asList(goToTemple, killTheDog, returnToKingRoald), weaponAndArmour));
		allSteps.add(new PanelDetails("Return to the temple", Arrays.asList(returnToTemple, killMonk, talkToDrezel), weaponAndArmour, bucket, lotsOfRuneEssence));
		allSteps.add(new PanelDetails("Freeing Drezel", Arrays.asList(enterUnderground, useKeyForKey, fillBucket, goUpWithWaterToSecondFloor, openDoor, blessWater, useBlessedWater, talkToDrezelAfterFreeing), weaponAndArmour, bucket, lotsOfRuneEssence));
		allSteps.add(new PanelDetails("Curing the Salve", Arrays.asList(talkToDrezelUnderground, bringDrezelEssence), runeEssence));

		return allSteps;
	}
}
