package ru.magtu.entities.Weapon;

import java.util.Random;

public class HeavyMace extends Weapon {
    private final Random random = new Random();

    public HeavyMace() {
        super("Heavy Mace", 14.0, 20.0); // высокая стоимость стамины
    }

    @Override
    public double getDamage() {
        // Симуляция тяжелой, но нестабильной атаки
        // Иногда сильно бьет, иногда промах или слабый удар
        double roll = random.nextDouble();

        if (roll < 0.2) {
            // Промах/слабый удар
            return baseDamage * 0.3;
        } else if (roll < 0.7) {
            // Средний урон
            return baseDamage * 0.8;
        } else {
            // Критический удар
            return baseDamage * 1.5;
        }
    }

    @Override
    public double getStaminaCost() {
        return 40; // Очень затратная
    }
}

