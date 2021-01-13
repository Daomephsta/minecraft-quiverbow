package com.domochevsky.quiverbow;

public class Notes
{
    /* b102 - Changed how QMobs aquire their weapons from QBow, to harden
     * against the possibility of them not getting ANY weapons. - Did various
     * internal optimizations [IN PROGRESS]
     * 
     * b101d - Fixed the block break event being accidentally send for the
     * client when hitting things with projectiles. - Possibly fixed the
     * Seedling not breaking upon running out of ammo.
     * 
     * b101c - Fixed a potential recipe crash (I was accidentally trying to
     * access crafting inventory slot 10, which some mods don't harden against.)
     * 
     * b101b - Fixed a server/client NPE crash. Hrm.
     * 
     * b101 - Fixed the Seedling being crafted with 0 ammunition. - Added the
     * "TARGET FLYING" command for the Arms Assistant. Keep your feet firmly
     * planted on the ground. - Adjusted the tooltip of the Thorn Spitter to
     * reflect its ammo usage. (A thorn magazine, not 8 thorn bundles) - Added
     * some more explosion protection for Dragonbox rockets and the powder
     * knuckles. They can now be set via config whether they damage terrain or
     * not. - Added some general block breaking protection for the remaining
     * weapons that break things. They now send a proper BlockBreak event (which
     * can be disabled in the config for pvp purposes). - Fixed the Four-headed
     * Dragonbox looking like it's cooling down despite being empty. - Visually
     * speaking you should now hold your weapon up when you fire it. Hopefully.
     * - Added a config option for the (Modified) Coin- and Potatosser to not
     * drop anything on missed shots - The Nether Bellows now won't create fire
     * if doFireTick is turned off.
     * 
     * 
     * b100 - Fixed hostile mobs and bosses not being targetable by the Arms
     * Assistant's black-/whitelist - Adjusted the default cooldown speed for
     * the Improved Auto-Crossbow from 4 to 8 ticks. 4 was a bit TOO fast for
     * arrows. (They tend to bounce off.) - Changed the sprites for the Bow with
     * Quiver and the Ender Bow, so they don't look like the vanilla bow anymore
     * 
     * b99 - Fixed the empty Improved Auto-Crossbow icon not having the proper
     * capitalization. - Fixed the Proximity Thorn Thrower and Improved
     * Auto-Crossbow models not looking for the right texture size.
     * 
     * b98 - Allowed the AA to reload the Soul Cairn - Fixed the Potatosser not
     * accepting charcoal (I had to use the ore dictionary wildcard value for
     * itemstacks. Silly stuff.) - Fixed a server crash by not using
     * MathHelper.truncateDoubleToInt() for the Arms Assistant. Turns out that
     * function is client-side only for some reason. - Added the Hidden Ender
     * Pistol - Added the Improved Auto-Crossbow - Added the Proximity Thorn
     * Thrower (PTT) - Did a couple code optimizations here and there, preparing
     * for a major internal overhaul. (Assuming I ever aquire the time/muse for
     * that.) - Also adjusted a model array to be absolutely client-only, so
     * BiomeTweaker doesn't try to crawl into it and crash. (Curse the damn
     * confusing client/server divide or lack thereof...)
     * 
     * b97 - Hopefully fixed the LAST zoom-related bug now. (It'd zoom you in if
     * someone next to you used the Ender Rifle's/Frost Lancer's scope. Turns
     * out clients tick all players they know about, as little sense as that
     * makes.)
     * 
     * b96 - Added config option to decide if any given weapon can be used by
     * QuiverMobs individually - Added "BLACKLIST" command to the Arms
     * Assistant, to let them shoot ONLY at things named in the book - Fixed
     * QuiverMobs rapidly firing with the rocket launchers. (Turns out I wasn't
     * setting a proper cooldown, since they're one-shot weapons in player
     * hands. Which these bastards don't care about.) - QuiverMobs should also
     * now respect the mobGriefing gamerule when shooting at glass and other
     * breakable things
     * 
     * b95 - Fixed packing up the Arms Assistant not saving the armor plating
     * and communication upgrades. - Adjusted the AA slightly, to not crawl at a
     * snail's pace when told to STAY and being fully loaded up on armor and
     * plating. - Put a limit of 5 blocks on how many blocks the OWR's
     * projectiles can punch through before running out of speed. Previously
     * they were just going until they hit something unbreakable or alive.
     * 
     * b94 - Allowed the AA to reload the Potatosser (Doesn't require coal for
     * now.) - Fixed the AA only loading up one arrow on the Double Crossbow -
     * Added the Communication Upgrade to the Arms Assistant. (This lets it
     * communicate with the owner and be aimed with the Targeting Helper.) ->
     * Added the Arms Assistant Targeting Helper. It's a handheld device that
     * lets you manually aim AAs. Using this required the Communication Upgrade.
     * -> Added the "REMOTE FIRE" command. This will cause the AA to aim and
     * shoot where you are pointing the Targeting Helper -> Added the
     * "HOLD FIRE" command to the AA. They will not shoot unless told to do so
     * via the Targeting Helper. -> Added the "TELL AMMO" command to the AA.
     * Only relevant with the Communication Upgrade. It'll tell the owner when
     * it has run out of ammunition. -> Added the "TELL HEALTH" command, same
     * conditions as above. It tells the owner when this thing's health is below
     * 30%. -> Added the "TELL DEATH" command, also the same as above. Tells the
     * owner when an AA was destroyed. - Added the Heavy Plating upgrade to the
     * AA. This provides 50% knockback resistance and 3 damage reduction vs
     * anything blockable, at the cost of 50% speed - Added the "SAFETY RANGE"
     * command, (communications-unrelated). This will cause the AA to at least
     * try to not get hurt by its own explosives. - Changed the ability to fire
     * the AAs weapons while riding them and tied it to the Targeting Helper
     * instead. - The AA will also now no longer attack players in creative
     * mode. (No point in doing so.) - I also hooked into some forge events for
     * block breaking, so protection plugins can actually do something about it.
     * - Adjusted the Ray of Hope reloading recipe to not create 1,8k recipe
     * permutations. Makes it a little easier on NEI. :P
     * 
     * b93 - Fixed the AA not remembering how to use the contents of its first
     * rail. - Fixed the Four-Headed Dragonbox only reloading 2 rockets per
     * bundle, instead of 8 - Fixed the Snow Cannon eating 4 blocks of snow with
     * every shot, instead of just 1 - Fixed the Seed Sweeper model still having
     * a jar on it, even when empty - Fixed reloading with magazines not
     * working. (That snuck in in the last build, I think.) - Fixed the Arms
     * Assistant not accepting replacing empty magazines with fresh ones. Tch. -
     * Fixed the AA not properly deducting ammo from certain slots and ammo
     * types. (Didn't update a bounds check to make use of the changed item
     * storage.) - Added a config option to the rocket launchers and the ERA to
     * not damage terrain when in player hand - Changed the Obsidian Splinter
     * Pistol to split every splint in half (Something that was lost when it was
     * changed to using magazines), giving it effectively double the ammunition
     * again. (Dropping the magazine does mean some loss, though. Can't put back
     * together what was split.) - Added the Riding Upgrade to the Arms
     * Assistant. You can now upgrade it with a saddle and ride it around
     * (assuming it's mobile; It's gonna be a short ride without that.) By
     * extension it will also fire its weapons if you attack with the QuiverBow
     * weapon in the first slot of your hotbar - Added the "STAGGER FIRE"
     * command, which can be written into the book. It'll cause the AA to wait
     * until the first rail is done with half its cooldown before firing the
     * second rail. That should help with not wasting two shots on the same
     * target
     * 
     * b92 - Fixed the Arms Assistant starting out with the second rail already
     * attached. (Forgot to remove a debug line there.) - Fixed the
     * FrostLancer/EnderRifle zoom getting stuck when trying to give those to
     * the AA on the second rail. Let's hope that this bug now goes the way of
     * Herobrine
     * 
     * b91 - Made a whole bunch of adjustments and enhancements to the Arms
     * Assistant -> This includes a better targeting module (it's now able to
     * see through glass and can fire with more accuracy at longer ranges) -> By
     * extension I limited their firing range via config to 32 blocks. You can
     * disable this at your own peril -> It does not yet lead targets, so circle
     * strafing continues to work for now -> They can also now hold onto any
     * item. Whether or not they can use it is an entirely different question.
     * -> Added a storage upgrade, which lets them hold 8 instead of 4 items ->
     * Added the ability to replace empty magazines by using a new magazine on
     * them, to make reloading them easier -> Only happens if there's no space
     * to add more items -> Completed their leg design, making them capable of
     * basic movement if upgraded -> The AA can be instructed to "STAY" (Will
     * try to compensate for recoil) or "FOLLOW" (the owner) via the written
     * book -> They can also now be instructed to "TARGET FRIENDLY" and
     * "INJURED ONLY". Use at your own risk -> Speaking of risk: A second weapon
     * rail has been added to enhance their fire power -> Added the Arrow and
     * Dragon Mortars to the list of items that can be reloaded -> Fixed it
     * using up items from players in creative mode - Fixed the Auto-Crossbow
     * not unchambering after firing
     * 
     * b90 - Fixed the Dragon and Arrow Mortars not adhering to their cooldown -
     * Fixed the Ray Of Hope accidentally being called Weapon Base by default
     * (You'll need to manually fix that in your config if you already have one.
     * Just delete the relevant line.) - Also fixed the Dragon Mortar not
     * consuming ammo - Modified the Nether Bellows slightly to also burn down
     * plants, instead of being stopped by it - Added the Arms Assistant
     * 
     * b87 - Changed internally how weapons, ammunition and projectiles are
     * named and registered, for some future (re)proofing. Turns out that having
     * a running number can break things if you slide something into their
     * middle ...same as with IDs in 1.6.4. So at some point I inadvertedly
     * reverted back to an ID system of my own making without noticing. :| As an
     * unfortunate side effect this will likely delete all existing gear due to
     * name/reg mismatching when you load this up. Be prepared! - Fixed the
     * Nether Bellows not melting snow like it should. No idea when that broke.
     * - Fixed the Lapis Coil not inflicting Weakness. No idea when that broke
     * either. - Modified the Lapis Coil to use Lapis Magazines. - Modified the
     * Lightning Red to use Redstone Magazines. - Modified the Redstone Sprayer
     * to use Large Redstone Magazines. - Modified the Nether Bellows to use
     * Large Netherrack Magazines. This also obsoletes the need for coal as
     * adhesive material. I removed Netherrack Bundles as extension of that. (No
     * need for them anymore.) - Fixed another bug in the ERA recipe. ._. (I had
     * a "Less Or Equal" instead of a "Greater Than" in there.) - Added the
     * Emerald Muzzle upgrade recipe to the Ender Rail Accelerator. That should
     * ease the shockwave on firing. By extension this also means that I can now
     * start adding upgrades to existing weapons if apropriate. :)
     * 
     * b86 - Reloading with magazines was seemingly asking for full weapons to
     * reload, instead of empty ones. That has been fixed - Also fixed being
     * able to put full weapons into the reloading process, causing the already
     * loaded magazine to be eaten
     * 
     * b85 - Hotfix: Recipes were broken; now they're (hopefully) not. Looks
     * like I missed some checks and details here and there, causing those
     * recipes to break all the things. >_> - On the plus side: My understand of
     * how Minecraft operates has been expanded by another critical part.
     * 
     * b84 - Fixed the ERA not burning out if it goes off in your pocket - Did
     * various internal code overhauls to clean things up and prepare a bunch of
     * weapons to use magazines (Be on the lookout for all new bugs. >_> ) -
     * Related, the Coin Tosser and Modified Coin Tosser now use Gold Magazines.
     * (Which are filled with gold nuggets.) - The Thorn Spitter also now uses
     * Needle Magazines (Filled with cacti.)
     * 
     * b83 - Added the Ender Rail Accelerator - Weapons should now keep their
     * custom names when reloading
     * 
     * b82 - Made the weapon models in third person about 25% smaller -
     * Increased the default projectile speed of all crossbows by about 25% (Put
     * some more perceived oomph behind them) - Increased the default projectile
     * speed of the Nether Bellows by about 50% (Might make it easier not to
     * burn yourself with them) - Increased the default damage of the
     * Auto-Crossbow to be closer to that of the regular crossbows
     * 
     * b81 - Hotfix: Expanded the weapons array. Turns out that I now have more
     * weapons and devices than I made room for initially. (40+) - Also found
     * out that the dev environment isn't caps sensitive, but regular Minecraft
     * is, when it comes to textures. :| - Fixed the Seed Sweeper not dropping a
     * jar when it runs out
     * 
     * b79 - Made some more adjustments, fixing burst fire not working for
     * (quiver)mobs - Improved Sugar Engine accuracy by 40% (turns out I wasn't
     * aligning the pistons correctly. They created too much vibration as a
     * result.) - Added the Seed Sweeper - Added the Ray of Hope - Modernized
     * the Obsidian Splinter Pistol, Spear Rifle and Wither Rifle. They now use
     * magazines. This also has the advantage of increasing their ammo capacity,
     * in addition to making reloading easier.
     * 
     * b78 - Changed some weapon function visibility, for use with Ancient
     * Warfare 2 NPCs
     * 
     * b77 - Added config option to remove disabled weapons from the creative
     * menu as well (By default they're on there, but uncraftable if disabled.)
     * 
     * 
     * MAYBE - Add model to the Powder Knuckles to have them wrap around your
     * hand instead of being held? - Add a disk launcher, for something that
     * ricochets? (No idea how to go about that yet) - Add long range single
     * fire homing rocket (Mix lapis into the fuel, also use a beacon for the
     * aiming system, plus eye of ender) <Jindra34> (Huh... just thought of
     * something, what would happen if we put ground powder, like say lapis, in
     * with the gunpowder of a rocket...) - Eggs? - Add Smoke grenade launcher
     * (particle effects, loads of them), loads 8 shots, each shot being crafted
     * as a container with coal and a fire charge
     * 
     * "this time it comes from the idea for a use for rotten flesh. ... flesh
     * is greasy and filled with decaying rot, it might actually be very
     * flamable. Add to that that it also contains enough weight to be lobbed a
     * long distance given enough power from a piston tech of some kind, I
     * predict that when added with netherack you could create a sort of long
     * distance �flame baller� some way."
     * 
     * Models: (Lapis Coil length: 18 barrel, 8 stock. A weapon should have at
     * least 3 width, for solid barrel width) */
}
