package ru.magtu.entities.Weapon;

public abstract class Weapon {
    protected String name;
    protected double baseDamage;
    protected double staminaCost;

    public Weapon(String name, double baseDamage, double staminaCost) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.staminaCost = staminaCost;
    }
    public Weapon(String name, double baseDamage){
        this.name = name;
        this.baseDamage = baseDamage;
    }

    public abstract double getDamage();

    public String getName() {
        return name;
    }

    public abstract double getStaminaCost();
}

