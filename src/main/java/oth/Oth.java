package oth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static oth.Oth.Coups.NOMOVE;


public class Oth extends OthVar implements OthConst {

    public Oth(Oth o) {
        etats = o == null ? ETATS_INIT.clone() : o.etats;
        trait = o == null ? OthConst.noir : -o.trait;
        lcoups = new ArrayList<>();
        S1 = new Etat() {
            @Override
            public Etat exec() {
                if (etats[_case] == trait && n - 1 != 0) {
                    lscore.add(new Score(n - 1, dir));
                    lcoups.add(new Coups(caseO, lscore));
                }
                n = 0;
                return null;
            }
        };
        S0 = new Etat() {
            @Override
            public Etat exec() {
                n++;
                return etats[_case = caseO + n * dir] == -trait ? S0.exec() : S1.exec();
            }
        };
    }

    public Oth() {
//        new File(OthConst.pathname + OthConst.filename).createNewFile();
//        writter = new FileWriter(OthConst.filename);

        rangeClosed(1, OthConst.max).forEach(
                num -> {
                    nb = num;
                    Oth o = new Oth(null);
                    o.jouer(o);
                }

        );
    }


    public Oth jouer(Oth o) {
//        findepartie = false;
//        passe = false;
        o.lcoups = new ArrayList<>();
        while (true) {
            if (o.findepartie) {
                break;
            } else {
                parcourir(this);
                fIA(this);
                passe_end(this);
                change(this);
            }
        }
        result(this);
        return o;
        // fFileWritte(this);
    }

    Oth change(Oth o) {
        o.trait = -o.trait;
        o.lcoups = new ArrayList<>();
        return o;
    }

    Oth fIA(Oth o) {
        o.move = (o.lcoups.size() != 0) ? o.lcoups.get(new Random().nextInt(o.lcoups.size())) : NOMOVE;
        return o;
    }

    Oth parcourir(Oth o) {
        range(0, 100).filter(c -> o.etats[c] == OthConst.vide).forEach(c -> {
            o.caseO = c;
            o.lscore = new ArrayList<>();
            DIRS.forEach(d -> {
                o.dir = d;
                Etat etat = o.S0;
                while (true)
                    if ((etat = etat.exec()) == o.S1 || etat == null) break;
            });
        });
        return o;
    }

    Oth passe_end(Oth o) {
        if (o.move == NOMOVE) {
            if (o.passe) {
                o.findepartie = true;
            } else {
                o.passe = true;
            }
        } else {
            if (o.passe) {
                o.passe = false;
            }

            fcoups(this, undomove);
            System.out.println(affiche(this));
        }
        return o;
    }

    Oth result(Oth o) {
        o.sN = 0;
        o.sB = 0;
        range(0, 100).forEach(c -> {
            switch (o.etats[c]) {

                case OthConst.blanc -> o.sB++;
                case OthConst.noir -> o.sN++;
            }
        });
        return o;
    }
//
//    Oth fFileWritte(Oth o) {
//        try {
//            o.writter.write((o.sB > o.sN ? "1" : (o.sN > o.sB ? "0" : "0.5")) + "," + o.sB + "," + o.sN);
//            o.writter.write("\n");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if (o.nb == OthConst.max) {
//            try {
//                o.writter.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//        return o;
//    }


    public List<Oth.Coups> legalmoves(Oth o) {
        range(0, 100).filter(c -> o.etats[c] == OthConst.vide).forEach(c -> {
            o.caseO = c;
            o.lscore = new ArrayList<>();
            DIRS.forEach(d -> {
                o.dir = d;
                Oth.Etat etat = o.S0;
                while (true)
                    if ((etat = etat.exec()) == o.S1 || etat == null) break;
            });
        });
        return o.lcoups.stream().distinct().toList();
    }


    public Oth fcoups(Oth o, boolean undomove) {
        o.move.lscore()
                .forEach(score -> rangeClosed(0, score.n())
                        .forEach(n -> o.etats[o.move.sq0() + n * score.dir()] = undomove ? -o.trait : o.trait));
        o.etats[o.move.sq0()] = undomove ? OthConst.vide : o.trait;
        return o;
    }


    public String affiche(Oth o) {
        StringBuilder sb = new StringBuilder();
        for (Coups cps : o.lcoups)
            sb.append(cps).append("\n");
        sb.append("num ").append(o.nb++).append("\n");
        sb.append(o.trait == OthConst.blanc ? "blanc" : "noir").append("\n");
        sb.append(OthConst.SCASES[o.move.sq0()]).append("\n");
        sb.append(o).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder spos = new StringBuilder();
        range(0, 100).forEach(_case -> {
            if (etats[_case] == OthConst.vide) spos.append("- ");
            else {
                String print = switch (etats[_case]) {
                    case OthConst.vide -> "_";
                    case OthConst.blanc -> "b";
                    case OthConst.noir -> "n";
                    case OthConst.out -> " ";
                    default -> "?";
                };
                spos.append(print).append(" ");
            }
            if (_case % 10 == 9) spos.append("\n");
        });
        return spos.toString();
    }


    public record Coups(int sq0, List<Score> lscore) {
        public static Coups NOMOVE;

        @Override
        public String toString() {
            return "(" + OthConst.SCASES[sq0] + ", " + lscore + ")";
        }
    }

    public record Score(int n, int dir) {
        @Override
        public int n() {
            return n;
        }
    }

    public abstract static class Etat {
        public abstract Etat exec();


    }
}