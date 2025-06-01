package ru.magtu.entities.Armour;

public class LightArmor extends Armor {
    public LightArmor() {
        super("Light Armor", 0.15); // 15% защиты
    }

    @Override
    public double absorbDamage(double incomingDamage) {
        return incomingDamage * (1 - defensePercent);
    }
}
