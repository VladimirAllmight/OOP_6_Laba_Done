package ru.magtu.entities.Armour;

public abstract class Armor {
    protected String name;
    protected double defensePercent;

    public Armor(String name, double defensePercent) {
        this.name = name;
        this.defensePercent = defensePercent;
    }

    public abstract double absorbDamage(double incomingDamage);

    public String getName() {
        return name;
    }
}

