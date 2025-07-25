/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.songoftheelves;

import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestUtil;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.List;

public class CrwysLightPuzzle extends ConditionalStep
{
	Zone f0, f1, f2;

	ItemRequirement handMirrorHighlighted, redCrystalHighlighted, fracturedCrystalHighlighted,
		greenCrystalHighlighted, cyanCrystalHighlighted;

	DetailedQuestStep resetPuzzle, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, p1Pillar8, p1Pillar9,
		p1Pillar10, p1Pillar11, p1Pillar12, p1Pillar13, collectMirrors, talkToAmlodd;

	DetailedQuestStep goF0ToF1, goF1ToF0, goF2ToF1, goF1ToF2;

	ConditionalStep goToF0, goToF1, goToF2;

	Requirement hasMirrorsAndCrystal, onF1, onF2, onF0, notResetCadarn, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12;

	public CrwysLightPuzzle(QuestHelper questHelper,ConditionalStep goToF1Steps)
	{
		super(questHelper, goToF1Steps);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		collectMirrors.addSubSteps(goToF1Steps);

		addStep(new Conditions(onF1, notResetCadarn), resetPuzzle);
		addStep(new Conditions(notResetCadarn), goToF1Steps);

		addStep(new Conditions(onF1, r12), p1Pillar13);
		addStep(new Conditions(r12), goToF1);

		addStep(new Conditions(onF2, r11), p1Pillar12);
		addStep(new Conditions(onF2, r10), p1Pillar11);
		addStep(new Conditions(r10), goToF2);

		addStep(new Conditions(onF1, r9), p1Pillar10);
		addStep(new Conditions(r9), goToF1);

		addStep(new Conditions(onF0, r8), p1Pillar9);
		addStep(new Conditions(onF0, r7), p1Pillar8);
		addStep(new Conditions(r7), goToF0);

		addStep(new Conditions(onF1, r6), p1Pillar7);
		addStep(new Conditions(onF1, r5), p1Pillar6);
		addStep(new Conditions(onF1, r4), p1Pillar5);
		addStep(new Conditions(onF1, r3), p1Pillar4);
		addStep(new Conditions(onF1, r2), p1Pillar3);
		addStep(new Conditions(onF1, r1), p1Pillar2);
		addStep(new Conditions(onF1, hasMirrorsAndCrystal), p1Pillar1);
		addStep(new Conditions(onF1, notResetCadarn), resetPuzzle);
		addStep(onF1, collectMirrors);
	}

	protected void setupSteps()
	{
		talkToAmlodd = new NpcStep(getQuestHelper(), NpcID.SOTE_LORD_AMLODD_VIS, new WorldPoint(2353, 3179, 0), "Talk to Lord Amlodd in Lletya.");
		talkToAmlodd.addDialogStep("Yes.");

		collectMirrors = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Collect all the items from the dispenser in the central room.");
		collectMirrors.addDialogStep("Take everything.");

		resetPuzzle = new ObjectStep(getQuestHelper(), ObjectID.SOTE_LIBRARY_DISPENSER, new WorldPoint(2623, 6118, 1), "Pull the lever in the dispenser in the central room.");
		resetPuzzle.addDialogSteps("Pull the lever.", "Pull it.");

		p1Pillar1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_D_4, new WorldPoint(2609, 6144, 1),
			"Add a mirror to a pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar1.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar2 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_4, new WorldPoint(2595, 6144, 1),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar2.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar3 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_C_5, new WorldPoint(2595, 6158, 1),
			"Add a mirror to the pillar to the north. Rotate it to point the light west.", handMirrorHighlighted);
		p1Pillar3.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar4 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_B_5, new WorldPoint(2581, 6158, 1),
			"Add a fractured crystal to the pillar to the west.", fracturedCrystalHighlighted);
		p1Pillar4.addIcon(ItemID.SOTE_CRYSTAL_FRACTURED);

		p1Pillar5 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_A_5, new WorldPoint(2567, 6158, 1),
			"Add a mirror to the pillar to the west. Rotate it to point the light north.", handMirrorHighlighted);
		p1Pillar5.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar6 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_A_6, new WorldPoint(2567, 6172, 1),
			"Add a green crystal to the pillar to the north.", greenCrystalHighlighted);
		p1Pillar6.addIcon(ItemID.SOTE_CRYSTAL_GREEN);

		p1Pillar7 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_A_8, new WorldPoint(2567, 6200, 1),
			"Run around to the north and add a mirror to the pillar there. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar7.addIcon(ItemID.SOTE_MIRROR);

		// Go downstairs

		p1Pillar8 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_A_8, new WorldPoint(2567, 6200, 0),
			"Add a mirror to the pillar in the north west. Rotate it to point the light east.", handMirrorHighlighted);
		p1Pillar8.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar9 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_0_B_8, new WorldPoint(2581, 6200, 0),
			"Add a mirror to the pillar to the east. Rotate it to point the light up.", handMirrorHighlighted);
		p1Pillar9.addIcon(ItemID.SOTE_MIRROR);

		// Go upstairs

		p1Pillar10 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_B_8, new WorldPoint(2581, 6200, 1),
			"Add the cyan crystal to the pillar next to the stairs.", cyanCrystalHighlighted);
		p1Pillar10.addIcon(ItemID.SOTE_CRYSTAL_CYAN);

		// Go upstairs f2

		p1Pillar11 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_B_8, new WorldPoint(2581, 6200, 2),
			"Add a mirror to the pillar next to the stairs. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar11.addIcon(ItemID.SOTE_MIRROR);

		p1Pillar12 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_2_B_7, new WorldPoint(2581, 6186, 2),
			"Add a mirror to the pillar to the south. Rotate it to point the light down.", handMirrorHighlighted);
		p1Pillar12.addIcon(ItemID.SOTE_MIRROR);

		// Go downstairs

		p1Pillar13 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_PILLAR_1_B_7, new WorldPoint(2581, 6186, 1),
			"Add a mirror to the pillar to the south. Rotate it to point the light south.", handMirrorHighlighted);
		p1Pillar13.addIcon(ItemID.SOTE_MIRROR);

		goF0ToF1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_UP, new WorldPoint(2581, 6203, 0), "");

		goF1ToF0 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2581, 6203, 1), "");
		goF1ToF0.addDialogStep("Climb down.");

		goF2ToF1 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_DOWN, new WorldPoint(2581, 6203, 2), "");

		goF1ToF2 = new ObjectStep(getQuestHelper(), ObjectID.SOTE_WARPED_LIBRARY_TELEPORTER_BOTH, new WorldPoint(2581, 6203, 1), "");
		goF1ToF2.addDialogStep("Climb up.");
	}

	protected void setupItemRequirements()
	{
		handMirrorHighlighted = new ItemRequirement("Hand mirror", ItemID.SOTE_MIRROR);
		handMirrorHighlighted.setHighlightInInventory(true);

		redCrystalHighlighted = new ItemRequirement("Red crystal", ItemID.SOTE_CRYSTAL_RED);
		redCrystalHighlighted.setHighlightInInventory(true);

		fracturedCrystalHighlighted = new ItemRequirement("Fractured crystal", ItemID.SOTE_CRYSTAL_FRACTURED);
		fracturedCrystalHighlighted.setHighlightInInventory(true);

		greenCrystalHighlighted = new ItemRequirement("Green crystal", ItemID.SOTE_CRYSTAL_GREEN);
		greenCrystalHighlighted.setHighlightInInventory(true);

		cyanCrystalHighlighted = new ItemRequirement("Cyan crystal", ItemID.SOTE_CRYSTAL_CYAN);
		cyanCrystalHighlighted.setHighlightInInventory(true);
	}

	protected void setupConditionalSteps()
	{
		goToF0 = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the bottom floor of the library.");
		goToF0.addStep(onF1, goF1ToF0);
		goToF0.addStep(onF2, goF2ToF1);

		goToF1 = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the middle floor of the library.");
		goToF1.addStep(onF2, goF2ToF1);
		goToF1.addStep(onF0, goF0ToF1);

		goToF2 = new ConditionalStep(getQuestHelper(), talkToAmlodd, "Go to the top floor of the library.");
		goToF2.addStep(onF1, goF1ToF2);
		goToF2.addStep(onF0, goF0ToF1);
	}

	protected void setupZones()
	{
		f0 = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6204, 0));
		f1 = new Zone(new WorldPoint(2565, 6080, 1), new WorldPoint(2740, 6204, 1));
		f2 = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2740, 6204, 2));
	}

	protected void setupConditions()
	{
		hasMirrorsAndCrystal = new Conditions(
			handMirrorHighlighted,
			redCrystalHighlighted,
			fracturedCrystalHighlighted
		);

		onF0 = new ZoneRequirement(f0);
		onF1 = new ZoneRequirement(f1);
		onF2 = new ZoneRequirement(f2);

		int WHITE = 1;
		int CYAN = 2;
		int MAGENTA = 4;

		notResetCadarn = new VarbitRequirement(8971, MAGENTA);
		r1 = new VarbitRequirement(8947, MAGENTA);
		r2 = new VarbitRequirement(8948, MAGENTA);
		r3 = new VarbitRequirement(8957, MAGENTA);

		r4 = new Conditions(
			new VarbitRequirement(8958, MAGENTA),
			new VarbitRequirement(8959, MAGENTA)
		);
		r5 = new Conditions(new VarbitRequirement(8961, MAGENTA), r4);
		r6 = new Conditions(new VarbitRequirement(9011, WHITE), r4);
		r7 = new Conditions(new VarbitRequirement(8579, WHITE), r4);

		r8 = new Conditions(new VarbitRequirement(8873, WHITE), r4);
		r9 = new Conditions(new VarbitRequirement(8583, WHITE), r4);

		r10 = new Conditions(new VarbitRequirement(8605, CYAN), r4);

		r11 = new Conditions(new VarbitRequirement(8787, CYAN), r4);
		r12 = new Conditions(new VarbitRequirement(8604, CYAN), r4);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(resetPuzzle, collectMirrors, p1Pillar1, p1Pillar2, p1Pillar3, p1Pillar4, p1Pillar5, p1Pillar6, p1Pillar7, goToF0,
			p1Pillar8, p1Pillar9, goToF1, p1Pillar10, goToF2, p1Pillar11, p1Pillar12, goToF1, p1Pillar13);
	}
}
