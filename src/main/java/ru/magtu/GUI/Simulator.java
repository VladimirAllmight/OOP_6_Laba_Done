package ru.magtu.GUI;

import ru.magtu.GUI.MusicPlayer.MusicList;
import ru.magtu.GUI.MusicPlayer.MusicPlayer;
import ru.magtu.entities.Gladiators.Berserk;
import ru.magtu.entities.Gladiators.Gladiator;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Simulator extends JPanel {
    private final Gladiator gladiator1;
    private final Gladiator gladiator2;
    private Image backgroundImage;
    private Image gladiator1Image;
    private Image gladiator2Image;
    MusicPlayer player = new MusicPlayer();

    private int gladiator1X = 50;
    private int gladiator2X = 700;
    private final int battlePosition1 = 300;
    private final int battlePosition2 = 450;

    private boolean showFight = true;
    private float fightAlpha = 1.0f;
    private boolean fightAnimationDone = false;
    private boolean battleStarted = false;
    private String battleLog = "";

    public Simulator(Gladiator gladiator1, Gladiator gladiator2) {
        this.gladiator1 = gladiator1;
        this.gladiator2 = gladiator2;
        setPreferredSize(new Dimension(800, 600));
        loadImages();
        startFightAnimation();
    }

    private void loadImages() {

        try {
            backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image2.png"))).getImage();
            gladiator1Image = new ImageIcon(Objects.requireNonNull(getClass()
                    .getResource(gladiator1 instanceof Berserk ? "/berserk.png" : "/standart.png"))).getImage();

            gladiator2Image = new ImageIcon(Objects.requireNonNull(getClass()
                    .getResource(gladiator2 instanceof Berserk ? "/berserk.png" : "/standart.png"))).getImage();
        } catch (Exception e) {
            System.err.println("Ошибка загрузки изображений: " + e.getMessage());
            setBackground(Color.BLACK);
        }
    }

    private void startFightAnimation() {
        player.playSound(MusicList.FIGHT_WORD);
        Timer fightTimer = new Timer();
        fightTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (showFight) {
                    fightAlpha -= 0.02f;
                    if (fightAlpha <= 0) {
                        fightAlpha = 0;
                        showFight = false;
                        fightAnimationDone = true;
                        startGladiatorMovement();
                    }
                    repaint();
                }
            }
        }, 400, 30);
    }

    private void startGladiatorMovement() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean needUpdate = false;

                if (gladiator1X < battlePosition1) {
                    gladiator1X += 4;
                    needUpdate = true;
                }
                if (gladiator2X > battlePosition2) {
                    gladiator2X -= 4;
                    needUpdate = true;
                }

                if (needUpdate) {
                    repaint();
                } else {
                    timer.cancel();
                    startBattle();
                }
            }
        }, 0, 30);
    }


    private void startBattle() {
        if (battleStarted) return;
        battleStarted = true;

        // Случайно выбираем, кто атакует первым
        final boolean firstAttacker = Math.random() < 0.5;

        Timer battleTimer = new Timer();
        final int[] phase = {firstAttacker ? 0 : 1}; // Начинаем с выбранного бойца
        final boolean[] gladiator1Attacked = {false};
        final boolean[] gladiator2Attacked = {false};

        battleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Обновляем состояние отдыха и восстанавливаем выносливость
                gladiator1.updateRest();
                gladiator2.updateRest();
                gladiator1.recoverStamina();
                gladiator2.recoverStamina();

                if (!gladiator1.isAlive() || !gladiator2.isAlive()) {
                    player.playSound(MusicList.DEATH);
                    String winner = gladiator1.isAlive() ? gladiator1.getName() : gladiator2.getName();
                    battleLog = "Победитель: " + winner + "!";
                    repaint();
                    battleTimer.cancel();
                    showWinnerOverlay(winner);
                    return;
                }

                switch (phase[0]) {
                    case 0 -> { // Атака первого гладиатора
                        if (gladiator1.canAttack() && !gladiator1Attacked[0]) {
                            battleLog = gladiator1.getName() + " атакует!";
                            animateAttack(true, () -> {
                                double damage = gladiator1.attack();
                                if (damage > 0) {
                                    gladiator2.takeDamage(damage);
                                    battleLog = gladiator1.getName() + " наносит " + String.format("%.1f", damage) + " урона!";
                                }
                                gladiator1Attacked[0] = true;
                                repaint();
                            });
                        } else {
                            battleLog = gladiator1.getName() + (gladiator1.isResting ? " отдыхает." : " слишком устал.");
                            gladiator1Attacked[0] = true;
                        }
                    }
                    case 1 -> { // Атака второго гладиатора
                        if (gladiator2.canAttack() && !gladiator2Attacked[0]) {
                            battleLog = gladiator2.getName() + " атакует!";
                            animateAttack(false, () -> {
                                double damage = gladiator2.attack();
                                if (damage > 0) {
                                    gladiator1.takeDamage(damage);
                                    battleLog = gladiator2.getName() + " наносит " + String.format("%.1f", damage) + " урона!";
                                }
                                gladiator2Attacked[0] = true;
                                repaint();
                            });
                        } else {
                            battleLog = gladiator2.getName() + (gladiator2.isResting ? " отдыхает." : " слишком устал.");
                            gladiator2Attacked[0] = true;
                        }
                    }
                    case 2 -> { // Сброс флагов атаки
                        gladiator1Attacked[0] = false;
                        gladiator2Attacked[0] = false;
                    }
                }

                phase[0] = (phase[0] + 1) % 3;
                repaint();
            }
        }, 0, 300); // Увеличил задержку для баланса
    }

    private void animateAttack(boolean isFirstAttacker, Runnable afterAnimation) {
        Timer attackTimer = new Timer();
        int[] step = {0};

        attackTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                step[0]++;
                if (step[0] <= 5) {
                    // Движение вперёд
                    if (isFirstAttacker) {
                        gladiator1X += 4;
                        player.playSound(MusicList.BERSERK_ATACK);
                    } else {
                        gladiator2X -= 4;
                        player.playSound(MusicList.STANDART_ATACK);
                    }
                } else if (step[0] <= 10) {
                    // Возврат назад
                    if (isFirstAttacker) {
                        gladiator1X -= 4;
                    } else {
                        gladiator2X += 4;
                    }
                } else {
                    attackTimer.cancel();
                    afterAnimation.run();
                }
                repaint();
            }
        }, 0, 30);
    }
    private void showWinnerOverlay(String winner) {
        JOptionPane.showMessageDialog(this, "Победитель: " + winner, "Бой завершен", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Фон
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Надпись "Fight!"
        if (showFight) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fightAlpha));
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 120));
            String text = "Fight!";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);
            g2d.dispose();
        }

        // Гладиаторы
        if (fightAnimationDone) {
            drawGladiator(g, gladiator1X, 400, gladiator1, gladiator1Image);
            drawGladiator(g, gladiator2X, 400, gladiator2, gladiator2Image);
        }

        // Лог боя
        if (battleStarted) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(50, 50, 700, 40);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(battleLog, 60, 80);
        }
    }

//    private void animateAttack(boolean isFirstAttacker, Runnable afterAnimation) {
//        Timer attackTimer = new Timer();
//        int attackDistance = 20;
//        int[] step = {0};
//
//        attackTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                gladiator1.updateRest();
//                gladiator2.updateRest();
//
//                gladiator1.recoverStamina();
//                gladiator2.recoverStamina();
//                step[0]++;
//                if (step[0] <= 5) {
//                    // Движение вперёд
//                    if (isFirstAttacker) {
//                        gladiator1X += 4;
//                    } else {
//                        gladiator2X -= 4;
//                    }
//                } else if (step[0] <= 10) {
//                    // Возврат назад
//                    if (isFirstAttacker) {
//                        gladiator1X -= 4;
//                    } else {
//                        gladiator2X += 4;
//                    }
//                } else {
//                    attackTimer.cancel();
//                    afterAnimation.run(); // Действие после анимации
//                }
//                repaint();
//            }
//        }, 0, 30);
//    }

    private void drawGladiator(Graphics g, int x, int y, Gladiator gladiator, Image image) {
        int barWidth = 100;
        int barHeight = 10;
        int spacing = 5;
        int textOffset = 5; // Отступ текста от полосок

        // Шкала здоровья
        double healthPercent = gladiator.getHealth() / 100.0;
        int currentHealth = (int) gladiator.getHealth();

        // Фон шкалы здоровья
        g.setColor(Color.RED);
        g.fillRect(x, y - 50, barWidth, barHeight);

        // Заполненная часть шкалы здоровья
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 50, (int)(barWidth * healthPercent), barHeight);

        // Текст здоровья
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String healthText = currentHealth + "/100";
        g.drawString(healthText, x + barWidth + textOffset, y - 50 + barHeight);

        // Шкала выносливости
        double staminaPercent = gladiator.getStamina() / 100.0;
        int currentStamina = (int) gladiator.getStamina();

        // Фон шкалы выносливости
        g.setColor(Color.GRAY);
        g.fillRect(x, y - 50 + barHeight + spacing, barWidth, barHeight);

        // Заполненная часть шкалы выносливости
        g.setColor(Color.CYAN);
        g.fillRect(x, y - 50 + barHeight + spacing, (int)(barWidth * staminaPercent), barHeight);

        // Текст выносливости
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String staminaText = currentStamina + "/100";
        g.drawString(staminaText, x + barWidth + textOffset, y - 50 + barHeight + spacing + barHeight);

        // Тип гладиатора
        String type = gladiator instanceof Berserk ? "Берсерк" : "Стандартный";
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(type, x, y - 10);

        // Изображение гладиатора
        if (image != null) {
            g.drawImage(image, x, y, 80, 160, this);
        } else {
            g.setColor(gladiator instanceof Berserk ? Color.RED : Color.BLUE);
            g.fillRect(x, y, 80, 160);
        }
    }
}