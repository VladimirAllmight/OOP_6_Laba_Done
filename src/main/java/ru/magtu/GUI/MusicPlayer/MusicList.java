package ru.magtu.GUI.MusicPlayer;

public enum MusicList {
    MAIN_WINDOW("/game-music-7408.wav"),
    FIGHT_WORD("/final-round-fight-mortal-kombat-spoken-fx_1bpm.wav"),
    STANDART_ATACK("/mixkit-fast-sword-whoosh-2792.wav"),
    BERSERK_ATACK("/mixkit-heavy-sword-smashes-metal-2795.wav"),
    DEATH("/mixkit-battle-man-scream-2175.wav");

    private final String filePath;

    MusicList(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}