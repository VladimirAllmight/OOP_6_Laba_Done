package ru.magtu.entities.Gladiators;

import ru.magtu.entities.Armour.Armor;
import ru.magtu.entities.Armour.HeavyArmor;
import ru.magtu.entities.Armour.LightArmor;
import ru.magtu.entities.Weapon.Weapon;

public class Berserk extends Gladiator {
    private boolean enraged = false;
    private int rageTurns = 0;

    public Berserk(String name, Weapon weapon, Armor armor) {
        super(name, 100, weapon, armor);
        if (armor instanceof LightArmor) this.evadeChance = 0.25;
        if (armor instanceof HeavyArmor) this.evadeChance = 0.02;
    }

    @Override
    public double calculateBaseDamage() {
        double baseDamage = super.calculateBaseDamage();

        if (enraged) {
            baseDamage *= 1.5;
        }

        return baseDamage;
    }

    @Override
    public double attack() {
        if (isResting) {
            System.out.printf("%s отдыхает и не атакует.%n", name);
            return 0;
        }

        double requiredStamina = weapon.getStaminaCost();

        if (stamina <= requiredStamina * 0.3 && !enraged) {
            System.out.printf("%s слишком устал и впадает в ярость!%n", name);
            enraged = true;
            rageTurns = 2;
            startResting(1);
            return 0;
        }

        double damage = calculateBaseDamage();
        damage = applyCritical(damage);

        // Расход стамины после расчета урона
        stamina -= requiredStamina;
        stamina = Math.max(0, stamina);

        if (enraged) {
            rageTurns--;
            if (rageTurns <= 0) {
                enraged = false;
            }
        }

        return damage;
    }

    @Override
    public void takeDamage(double damage) {
        if (tryEvade()) {
            System.out.printf("%s УКЛОНИЛСЯ от атаки!%n", name);
            return;
        }

        double reduced = (armor != null) ? armor.absorbDamage(damage) : damage;
        if (enraged) reduced *= 0.8;
        health -= reduced;
        System.out.printf("%s получает %.1f урона.%n", name, reduced);

        if (!enraged && health <= 20 && isAlive()) {
            enraged = true;
            rageTurns = 3;
            System.out.println(name + "\u001B[31m" +" в ЯРОСТИ!" + "\u001B[0m");
        }
    }
}

