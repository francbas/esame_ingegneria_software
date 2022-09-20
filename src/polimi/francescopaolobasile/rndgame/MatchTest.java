package polimi.francescopaolobasile.rndgame;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MatchTest {
    public static void Test() {
        Match match = new Match(5);
        try {
            match.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Match {
    private final ScoreTable st;
    private final int n;

    public Match(Integer nplayer) {
        n = nplayer;
        this.st = new ScoreTable(n);
    }

    public void start() throws InterruptedException {
        PlayerThread[] playerThreads = new PlayerThread[n];
        Player[] p = new Player[n];
        System.out.printf("Benvenuto al RND Game (aka exam killer!)%n");

        for (int i = 0; i < n; i++) {
            p[i] = new Player(i, st);
            st.addPlayer(p[i]);
        }
        while (!st.containsValue(5)) {
            System.out.printf("%nNuovo Round!%n");
            for (int i = 0; i < n; i++) {
                System.out.printf("Player[%d]: %d%n", i, st.getPunteggio(i));
                playerThreads[i] = new PlayerThread(p[i]);
                playerThreads[i].setName("TH_player_" + i);
                playerThreads[i].start();
            }
            for (int i = 0; i < n; i++) {
                playerThreads[i].join();
            }
        }

        System.out.printf("%nPunteggio finale:%n");
        for (int i = 0; i < n; i++) {
            System.out.printf("Player[%d]: %d%n", i, st.getPunteggio(i));

        }
//            System.out.printf("Player[%d]: %d%n", st.getHasMap().get(i), st.getPunteggio(i));

//        System.out.printf("Score finale%s%n", st.getHasMap().entrySet());

//        while (!st.containsValue(5)) {
//            for (int i = 0; i < n; i++) {
//                synchronized (playerThreads[i]) {
//                    playerThreads[i].wait();
//                }
//            }
//        }
    }
}

class PlayerThread extends Thread {
    private final Player player;

    public PlayerThread(Player p) {
        super();
        this.player = p;
    }

    public void run() {
        try {
            player.play();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        synchronized (this) {
//            notify();
//        }
    }

}

class Player {
    private final Integer id;
    private final ScoreTable st;

    public Player(Integer id, ScoreTable st) {
        this.id = id;
        this.st = st;
    }

    public Integer getId() {
        return id;
    }

    public void play() throws InterruptedException {

        int score1 = 1;
        int score2 = 1;

        Bomb bomb = new Bomb(st, this);
        Thread t_bomb = new Thread(bomb);
        t_bomb.start();
        t_bomb.join();
//        synchronized (t_bomb) {
//            t_bomb.wait();
//        }

        if (bomb.getCurrentStato() == Status.EXPLODED) {
            score1 = -1;
        } else {
            score2 = -1;
        }
        st.addPunteggio(this.id, score1);
        st.addPunteggio(bomb.getPickedPlayerId(), score2);
    }
}

enum Status {IDLE, PENDING, UNEXPLODED, EXPLODED}

class Bomb implements Runnable {
    private Status curStato;
    private final ScoreTable st;
    private final Player curPlayer;
    private Integer pickedPlayerId;

    public Bomb(ScoreTable st, Player player) {
        this.st = st;
        curStato = Status.IDLE;
        this.curPlayer = player;
    }

    public void run() {
        SecureRandom rnd = new SecureRandom();
        curStato = Status.PENDING;
        int R;
        while (curStato == Status.PENDING) {
            R = rnd.nextInt(st.getSize() * 2) + 1;
            if (R <= st.getSize()) {
                pickedPlayerId = R - 1;
                if (st.getPunteggio(pickedPlayerId) > st.getPunteggio(curPlayer.getId())) {
                    curStato = Status.EXPLODED;
                } else {
                    curStato = Status.UNEXPLODED;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        synchronized (this) {
//            notify();
//        }
    }

    public Status getCurrentStato() {
        return curStato;
    }

    public Integer getPickedPlayerId() {
        return pickedPlayerId;
    }
}

class ScoreTable {
    private final Map<Integer, Integer> scoremap;

    public ScoreTable(int n) {
        scoremap = new HashMap<>(n);
    }

    public void addPlayer(Player player) {
        scoremap.put(player.getId(), 0);
    }

    public boolean containsValue(Integer i) {
        return Collections.max(scoremap.values()) >= i;
    }

    public synchronized Integer getPunteggio(Integer playerId) {
//        int temp = scoremap.get(playerId);
        return scoremap.get(playerId);
    }

    public synchronized void addPunteggio(Integer playerId, Integer score) {
        scoremap.put(playerId, getPunteggio(playerId) + score);
    }

    public Integer getSize() {
        return scoremap.size();
    }

    public Map<Integer, Integer> getHasMap() {
        return scoremap;
    }
}
