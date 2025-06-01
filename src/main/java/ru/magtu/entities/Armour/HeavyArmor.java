package ru.magtu.entities.Armour;

public class HeavyArmor extends Armor {
    public HeavyArmor() {
        super("Heavy Armor", 0.5); // 50% защиты
    }

    @Override
    public double absorbDamage(double incomingDamage) {
        return incomingDamage * (1 - defensePercent);
    }
}
