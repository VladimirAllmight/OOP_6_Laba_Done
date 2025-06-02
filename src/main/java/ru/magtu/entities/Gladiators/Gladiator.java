package ru.magtu.entities.Gladiators;

import ru.magtu.entities.Armour.Armor;
import ru.magtu.entities.Armour.HeavyArmor;
import ru.magtu.entities.Armour.LightArmor;
import ru.magtu.entities.Weapon.Weapon;

import java.util.Random;

public abstract class Gladiator {
    protected String name;
    protected double health;
    protected Weapon weapon;
    protected Armor armor;
    protected double evadeChance;
    protected double stamina;
    protected final double MAX_STAMINA = 100;
    protected Random rand = new Random();

    public boolean isResting = false;
    protected int restTurns = 0;


    public Gladiator(String name, double health, Weapon weapon, Armor armor) {
        this.name = name;
        this.health = health;
        this.weapon = weapon;
        this.armor = armor;
        this.stamina = MAX_STAMINA;

        if (armor == null) {
            this.evadeChance = 0.35;
        } else {
            if (armor instanceof LightArmor) {
                this.evadeChance = 0.25;
            } else if (armor instanceof HeavyArmor) {
                this.evadeChance = 0.02;
            } else {
                this.evadeChance = 0.05;
            }
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean tryEvade() {
        double effectiveEvade = (stamina >= 20) ? evadeChance : evadeChance * 0.5;
        if (rand.nextDouble() < effectiveEvade) {
            stamina -= 25;
            return true;
        }
        return false;
    }

    public double applyCritical(double damage) {
        double critChance = 0.2;
        if (rand.nextDouble() <= critChance) {
            System.out.println("КРИТИЧЕСКИЙ УДАР!");
            return damage * 2;
        }
        return damage;
    }

    // Модифицируем метод attack()
    public double attack() {
        if (isResting) {
            System.out.printf("%s отдыхает и не атакует.%n", name);
            return 0;
        }

        double requiredStamina = weapon.getStaminaCost();

        if (stamina <= requiredStamina * 0.3) {
            System.out.printf("%s слишком устал и начинает отдых.%n", name);
            startResting(2);
            return 0;
        }

        double damage = calculateBaseDamage();
        damage = applyCritical(damage);

        // Расход стамины после расчета урона
        stamina -= requiredStamina;
        stamina = Math.max(0, stamina);

        return damage;
    }

    public abstract void takeDamage(double damage);

    public String getName() {
        return name;
    }

    public double getHealth() {
        return health;
    }

    public void recoverStamina() {
        stamina = Math.min(MAX_STAMINA, stamina + 7);
    }

    public double getStamina() {
        return stamina;
    }

    public boolean canAttack() {
        // Гладиатор может атаковать, если не отдыхает и у него есть стамина
        return !isResting && stamina > weapon.getStaminaCost() * 0.3;
    }
    public void startResting(int turns) {
        this.isResting = true;
        this.restTurns = turns;
    }
    public void updateRest() {
        if (isResting) {
            restTurns--;
            if (restTurns <= 0) {
                isResting = false;
            }
            // Во время отдыха восстанавливаем больше стамины
            recoverStamina();
            recoverStamina(); // Двойное восстановление
        }
    }

    public double calculateBaseDamage() {
        double baseDamage = weapon.getDamage();

        // Уменьшение урона при низкой стамине
        if (stamina < weapon.getStaminaCost() * 0.5) {
            baseDamage *= 0.7; // 30% снижение урона
        } else if (stamina < weapon.getStaminaCost()) {
            baseDamage *= 0.85; // 15% снижение урона
        }

        return baseDamage;
    }

    public void displayStatus() {
        System.out.printf("%s [HP: %.1f, STA: %.1f]%n", name, health, stamina);
    }
}
