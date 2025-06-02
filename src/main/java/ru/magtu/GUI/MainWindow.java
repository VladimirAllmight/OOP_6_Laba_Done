package ru.magtu.GUI;

import ru.magtu.GUI.MusicPlayer.MusicList;
import ru.magtu.GUI.MusicPlayer.MusicPlayer;
import ru.magtu.entities.Armour.*;
import ru.magtu.entities.Gladiators.*;
import ru.magtu.entities.Weapon.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Objects;

public class MainWindow extends JFrame {
    private JComboBox<String> gladiator1Type, gladiator2Type;
    private JComboBox<String> gladiator1Weapon, gladiator2Weapon;
    private JComboBox<String> gladiator1Armor, gladiator2Armor;

    public void showWindow() {
        setTitle("🏛️ Gladiator Simulator");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL url = getClass().getResource("/image.png");
                ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(url));
                g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        MusicPlayer player = new MusicPlayer();
        player.playSound(MusicList.MAIN_WINDOW);

        // Главная панель управления
        JPanel controlPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        controlPanel.add(createGladiatorPanel("Боец 1", true));
        controlPanel.add(createGladiatorPanel("Боец 2", false));

        JButton startButton = new JButton("Начать бой");
        startButton.setFont(new Font("Georgia", Font.BOLD, 20));
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.addActionListener(this::startSimulation);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);

        backgroundPanel.add(controlPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);
        setVisible(true);
    }

    private JPanel createGladiatorPanel(String title, boolean isFirst) {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                title,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Serif", Font.BOLD, 18),
                Color.WHITE
        ));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Берсерк", "Стандартный"});
        JComboBox<String> weaponBox = new JComboBox<>(new String[]{"Меч", "Тяжелая Булова"});
        JComboBox<String> armorBox = new JComboBox<>(new String[]{"Легкие", "Тяжелые", "Нет"});

        panel.add(createLabeledCombo("Тип:", typeBox));
        panel.add(createLabeledCombo("Оружие:", weaponBox));
        panel.add(createLabeledCombo("Доспехи:", armorBox));

        if (isFirst) {
            gladiator1Type = typeBox;
            gladiator1Weapon = weaponBox;
            gladiator1Armor = armorBox;
        } else {
            gladiator2Type = typeBox;
            gladiator2Weapon = weaponBox;
            gladiator2Armor = armorBox;
        }

        return panel;
    }

    private JPanel createLabeledCombo(String labelText, JComboBox<String> comboBox) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));

        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(comboBox, BorderLayout.CENTER);
        return panel;
    }

    private void startSimulation(ActionEvent e) {
        Gladiator gladiator1 = createGladiator(
                gladiator1Type.getSelectedIndex(),
                gladiator1Weapon.getSelectedIndex(),
                gladiator1Armor.getSelectedIndex(),
                "Боец 1"
        );

        Gladiator gladiator2 = createGladiator(
                gladiator2Type.getSelectedIndex(),
                gladiator2Weapon.getSelectedIndex(),
                gladiator2Armor.getSelectedIndex(),
                "Боец 2"
        );

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Бой гладиаторов");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(new Simulator(gladiator1, gladiator2));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        this.dispose();
    }

    private Gladiator createGladiator(int type, int weaponType, int armorType, String gladiatorType) {
        Weapon weapon = switch (weaponType) {
            case 0 -> new Sword();
            case 1 -> new HeavyMace();
            default -> throw new IllegalStateException("Unexpected weapon value: " + weaponType);
        };

        Armor armor = switch (armorType) {
            case 0 -> new LightArmor();
            case 1 -> new HeavyArmor();
            default -> null;
        };

        return switch (type) {
            case 0 -> new Berserk(gladiatorType, weapon, armor);
            default -> new StandardGladiator(gladiatorType, weapon, armor);
        };
    }

    public static void main(String[] args) {
        new MainWindow().showWindow();
    }
}