package ru.magtu.entities.Weapon;

import java.util.Random;

public class Sword extends Weapon {
    public Sword() {
        super("Sword", 11);
    }

    @Override
    public double getDamage() {
        return baseDamage + new Random().nextDouble() * 5;
    }

    @Override
    public double getStaminaCost() {
        return 10; // Недорогой по стамине
    }
}


