package oth;

import java.io.FileWriter;
import java.util.List;

public class OthVar {
    public List<Oth.Coups> lcoups;
    public int[] etats;
    public Oth.Coups move;
    public boolean undomove;
    public int caseO;
    public int dir;
    public int trait;
    public List<Oth.Score> lscore;
    public Oth.Etat S0;
    public Oth.Etat S1;
    int nb = 0;
    FileWriter writter;
    boolean passe = true;
    boolean findepartie;
    int sN;
    int sB;
    int n;
    int _case;
}
