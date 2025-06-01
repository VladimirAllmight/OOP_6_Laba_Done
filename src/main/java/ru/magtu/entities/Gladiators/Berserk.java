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
    public double attack() {
        double requiredStamina = weapon.getStaminaCost();

        // Если стамина меньше или равна 0, пропускаем ход
        if (stamina <= 0) {
            System.out.printf("%s слишком устал и пропускает ход, чтобы восстановиться.%n", name);
            recoverStamina(); // Восстановление стамины после пропуска хода
            return 0; // Никакой атаки не будет
        }

        double dmg = weapon.getDamage();

        if (enraged) {
            dmg *= 1.5;
            rageTurns--;
            if (rageTurns == 0) enraged = false;
        }

        if (stamina < requiredStamina) {
            stamina = 0;
            System.out.printf("%s слишком устал и наносит слабый удар.%n", name);
            return applyCritical(dmg * 0.5);
        }

        stamina -= requiredStamina;
        return applyCritical(dmg);
    }

//    @Override
//    public double attack() {
//        double requiredStamina = weapon.getStaminaCost();
//
//        if (stamina <= 0) {
//            System.out.printf("%s слишком устал и пропускает ход, чтобы восстановиться.%n", name);
//            recoverStamina();
//            return 0;
//        }
//
//        double dmg = weapon.getDamage();
//
//        if (enraged) {
//            dmg *= 1.5;
//            rageTurns--;
//            if (rageTurns == 0) enraged = false;
//        }
//
//        if (stamina < requiredStamina) {
//            stamina = 0;
//            System.out.printf("%s слишком устал и наносит слабый удар.%n", name);
//            return applyCritical(dmg * 0.5);
//        }
//
//        stamina -= requiredStamina;
//        return applyCritical(dmg);
//    }


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

