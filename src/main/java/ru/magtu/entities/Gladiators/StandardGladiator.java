package ru.magtu.entities.Gladiators;

import ru.magtu.entities.Armour.Armor;
import ru.magtu.entities.Armour.HeavyArmor;
import ru.magtu.entities.Armour.LightArmor;
import ru.magtu.entities.Weapon.Weapon;

public class StandardGladiator extends Gladiator {
    public StandardGladiator(String name, Weapon weapon, Armor armor) {
        super(name, 100, weapon, armor);
        if (armor instanceof LightArmor) this.evadeChance = 0.25;
        if (armor instanceof HeavyArmor) this.evadeChance = 0.02;
    }

    @Override
    public void takeDamage(double damage) {
        if (tryEvade()) {
            System.out.printf("%s УКЛОНИЛСЯ от атаки!%n", name);
            return;
        }
        double reduced = (armor != null) ? armor.absorbDamage(damage) : damage;
        health -= reduced;
        stamina -= 7;
        System.out.printf("%s получает %.1f урона.%n", name, reduced);
    }
}