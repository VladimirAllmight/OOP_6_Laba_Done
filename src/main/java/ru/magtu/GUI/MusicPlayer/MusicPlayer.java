package ru.magtu.GUI.MusicPlayer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class MusicPlayer {
    private Clip currentClip;

    public void playSound(MusicList sound) {
        stop();

        try {
            // Получаем поток из ресурсов
            InputStream audioStream = getClass().getResourceAsStream(sound.getFilePath());

            if (audioStream == null) {
                throw new IOException("Файл не найден: " + sound.getFilePath());
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioInputStream);
            currentClip.start();
        } catch (Exception e) {
            System.err.println("Ошибка при воспроизведении звука: " + e.getMessage());
        }
    }

    public void stop() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
        }
    }
}