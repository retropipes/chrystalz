/*  TallerTower: An RPG
Copyright (C) 2011-2012 Eric Ahnell

Any questions should be directed to the author via email at: products@puttysoftware.com
 */
package studio.ignitionigloogames.chrystalz.battle.damageengines;

import studio.ignitionigloogames.chrystalz.creatures.AbstractCreature;
import studio.ignitionigloogames.chrystalz.creatures.StatConstants;
import studio.ignitionigloogames.common.random.RandomRange;

class HardDamageEngine extends AbstractDamageEngine {
    private static final int MULTIPLIER_MIN = 7000;
    private static final int MULTIPLIER_MAX = 12500;
    private static final int MULTIPLIER_MIN_CRIT = 15000;
    private static final int MULTIPLIER_MAX_CRIT = 25000;
    private static final int FUMBLE_CHANCE = 750;
    private static final int PIERCE_CHANCE = 750;
    private static final int CRIT_CHANCE = 750;
    private boolean dodged = false;
    private boolean missed = false;
    private boolean crit = false;
    private boolean pierce = false;
    private boolean fumble = false;

    @Override
    public int computeDamage(final AbstractCreature enemy,
            final AbstractCreature acting) {
        // Compute Damage
        final double attack = acting.getEffectedAttack();
        final double defense = enemy
                .getEffectedStat(StatConstants.STAT_DEFENSE);
        final int power = acting.getItems().getTotalPower();
        this.didFumble();
        if (this.fumble) {
            // Fumble!
            return CommonDamageEngineParts.fumbleDamage(power);
        } else {
            this.didPierce();
            this.didCrit();
            double rawDamage;
            if (this.pierce) {
                rawDamage = attack;
            } else {
                rawDamage = attack - defense;
            }
            final int rHit = CommonDamageEngineParts.chance();
            int aHit = acting.getHit();
            if (this.crit || this.pierce) {
                // Critical hits and piercing hits
                // always connect
                aHit = CommonDamageEngineParts.ALWAYS;
            }
            if (rHit > aHit) {
                // Weapon missed
                this.missed = true;
                this.dodged = false;
                this.crit = false;
                return 0;
            } else {
                final int rEvade = CommonDamageEngineParts.chance();
                final int aEvade = enemy.getEvade();
                if (rEvade < aEvade) {
                    // Enemy dodged
                    this.missed = false;
                    this.dodged = true;
                    this.crit = false;
                    return 0;
                } else {
                    // Hit
                    this.missed = false;
                    this.dodged = false;
                    RandomRange rDamage;
                    if (this.crit) {
                        rDamage = new RandomRange(
                                HardDamageEngine.MULTIPLIER_MIN_CRIT,
                                HardDamageEngine.MULTIPLIER_MAX_CRIT);
                    } else {
                        rDamage = new RandomRange(
                                HardDamageEngine.MULTIPLIER_MIN,
                                HardDamageEngine.MULTIPLIER_MAX);
                    }
                    final int multiplier = rDamage.generate();
                    return (int) (rawDamage * multiplier
                            / CommonDamageEngineParts.MULTIPLIER_DIVIDE);
                }
            }
        }
    }

    @Override
    public boolean enemyDodged() {
        return this.dodged;
    }

    @Override
    public boolean weaponMissed() {
        return this.missed;
    }

    @Override
    public boolean weaponCrit() {
        return this.crit;
    }

    @Override
    public boolean weaponPierce() {
        return this.pierce;
    }

    @Override
    public boolean weaponFumble() {
        return this.fumble;
    }

    private void didPierce() {
        this.pierce = CommonDamageEngineParts
                .didSpecial(HardDamageEngine.PIERCE_CHANCE);
    }

    private void didCrit() {
        this.crit = CommonDamageEngineParts
                .didSpecial(HardDamageEngine.CRIT_CHANCE);
    }

    private void didFumble() {
        this.fumble = CommonDamageEngineParts
                .didSpecial(HardDamageEngine.FUMBLE_CHANCE);
    }
}