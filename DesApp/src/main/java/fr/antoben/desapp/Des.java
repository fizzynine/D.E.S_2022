package fr.antoben.desapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class Des {
    final int taille_bloc = 64;
    final int taille_sous_bloc = 32;
    final int[] tab_decalage = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1}; //table des décalages pour création de clé (diapo 27)
    int[] perm_initiale = {58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7};//permutation initiale (diapo 26) : il suffit de stoker le tableau PI (Permutation Initiale)
    public int[] S = {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}; //table de la fonction S (diapo 30) (tous les Si seront identiques dans un premier temps ...)
    final int[] E = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1};//table diapo 28
    private final Random r = new Random();
    public int[] masterKey;//tableau de 64 éléments pris au hasard dans {0,1}
    public int[] Key1;//masterKey1 pour TDES
    public int[] Key2;//masterKey2 pour TDES
    public int[] Key3;//masterKey3 pour TDES
    public int[][] tab_cles;//tableau des clefs pour DES 16 rondes et TDES;
    public int nbEspace;//le nombre d'espace ajouter pour avoir une string compatible avec une conversion en 64blocs;
    public ArrayList<int[]> tableauPermutationS;//tableau des permutation sur le tableau S
    public ArrayList<Object> tableauTripleDes;//sauvegarde l'ensemble des variables utiliser pour multiple DES 16 rondes;

    /**
     * le constructeur DES initialise la masterkey 1, 2 et 3 pour le TDES, seul la masterkey 1 est utiliser pour DES et DES 16 rondes;
     */
    public Des() {
        tableauTripleDes = new ArrayList<>();
        int[] perm = new int[64];
        for (int i = 0; i < perm_initiale.length; i++) perm[i] = perm_initiale[i] - 1;
        perm_initiale = perm;
        masterKey = new int[64];
        Key1 = new int[64];
        Key2 = new int[64];
        Key3 = new int[64];
        for (int i = 0; i < 64; i++) masterKey[i] = r.nextInt(2);
        Key1 = masterKey;
        for (int i = 0; i < 64; i++) Key2[i] = r.nextInt(2);
        for (int i = 0; i < 64; i++) Key3[i] = r.nextInt(2);
    }

    /**
     * Cette fonction convertit une String en bits
     *
     * @param input une String
     * @return la conversion de input en tableau de bits
     */
    public int[] stringToBits(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }

        String r = result.toString();
        ArrayList<Integer> ar = new ArrayList<>();
        for (char c : r.toCharArray()) ar.add(Integer.parseInt(c + ""));
        return ar.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Cette fonction convertit un tableau de bits en String
     *
     * @param ints le tableau de bits
     * @return la conversion de ints en String;
     */
    public String bitsToString(int[] ints) {
        StringBuilder input = new StringBuilder();
        for (int i : ints) {
            input.append(i);
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(
                input.toString().split("(?<=\\G.{8})")
        ).forEach(s ->
                sb.append((char) Integer.parseInt(s, 2))
        );
        return sb.toString();
    }

    /**
     * va generer une permutation d'indice aleatoire celon la taille d'un tableau
     *
     * @param taille la taille du tableau a permuter
     * @return le tableau de permutation d'indice
     */
    public int[] generePermutation(int taille) {
        ArrayList<Integer> ints = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            int rr = r.nextInt(taille);
            while (ints.contains(rr)) {
                rr = r.nextInt(taille);
            }
            ints.add(rr);
        }
        int[] bruh = new int[taille];
        for (int i = 0; i < taille; i++) {
            bruh[i] = ints.get(i);
        }
        return bruh;
    }

    /**
     * cette fonction permute un bloc en fonction des indice dans un tableau de permutation
     *
     * @param tab_permutation le tableau de permutation
     * @param bloc            le bloc a permuter
     * @return le bloc permuter en fonction du tableau de permutation
     */
    public int[] permutation(int[] tab_permutation, int[] bloc) {
        int[] copy = new int[tab_permutation.length];
        int c = 0;
        for (int i : tab_permutation) {
            //System.out.println("i: "+i+" - "+ints[i]);
            copy[c] = bloc[i];
            c++;
        }
        return copy;
    }

    /**
     * cette fonction recupere un bloc qui a subit une permutation dans son etat d'origine.
     *
     * @param tab_permutation le tableau qui a modifier les indices du bloc
     * @param bloc            le bloc qui a subit une permutation
     * @return le bloc d'origine grace a une permuatation inverse
     */
    public int[] invPermutation(int[] tab_permutation, int[] bloc) {
        final ArrayList<Integer> inverse = new ArrayList<>();
        int target = 0;
        while (target != tab_permutation.length) {
            for (int i = 0; i < tab_permutation.length; i++) {
                if (tab_permutation[i] == target) {
                    inverse.add(i);
                    target++;
                    break;
                }
            }
        }
        return permutation(inverse.stream().mapToInt(i -> i).toArray(), bloc);
    }

    /**
     * Cette fonction effectue un xor entre deux tableau de bits
     *
     * @param b1 tableau b1 de bits
     * @param b2 tableau b2 de bits
     * @return le tableau de xor entre les deux tableau de bits
     */
    public int[] xor(int[] b1, int[] b2) {
        int[] result = new int[b1.length];
        for (int i = 0; i < b1.length; i++)
            if (b1[i] == b2[i]) result[i] = 0;
            else result[i] = 1;
        return result;
    }

    /**
     * Cette fonction coupe un tableau en plusieur tableau de meme taille
     *
     * @param bloc    le bloc de bits a decouper
     * @param nbBlocs nombre de decoupage effectuer
     * @return un tableau de tableau de bits
     */
    public int[][] decoupage(int[] bloc, int nbBlocs) {
        if (bloc.length % nbBlocs == 0) {
            int[][] decoupe = new int[nbBlocs][bloc.length / nbBlocs];
            for (int i = 0; i < nbBlocs; i++)
                System.arraycopy(bloc, (i * (bloc.length / nbBlocs)), decoupe[i], 0, bloc.length / nbBlocs);
            return decoupe;
        } else {
            System.out.println("Ce tableau n'est pas divisible par " + nbBlocs);
            return null;
        }
    }

    /**
     * cette fonction recole tout les bloc du tableau
     *
     * @param blocs le tableau de tableau de bits
     * @return le tableau de bits composer de tous les elements de blocs
     */
    public int[] recollage_bloc(int[][] blocs) {
        int[] tab = new int[blocs.length * blocs[0].length];
        for (int i = 0; i < blocs.length; i++) System.arraycopy(blocs[i], 0, tab, i * blocs[0].length, blocs[0].length);
        return tab;
    }

    /**
     * Cette fonction effectue un decallage a gauche de tous les elements d'un tableau
     *
     * @param bloc   le tableau en question
     * @param nbCran le nombre de decallage a gauche
     * @return le tableau modifier apres les decallages
     */
    public int[] decalle_gauche(int[] bloc, int nbCran) {
        int[] bloc2 = new int[bloc.length];
        for (int i = 0; i < bloc.length; i++) {
            if (i - nbCran < 0) {
                bloc2[bloc.length - nbCran + i] = bloc[i];
            } else {
                bloc2[i - nbCran] = bloc[i];
            }
        }
        return bloc2;
    }
    /*public  int[] decalle_gauche2(int[] bloc, int nbCran){
        int[] permut= new int[bloc.length];
        for(int i=0;i<bloc.length;i++){
            if(i+nbCran>bloc.length-1){
                permut[i]= i + nbCran-bloc.length;
            }
            else {
                permut[i] = i + nbCran;
            }
        }
        return permutation(permut,bloc);
    }*/

    /**
     * Cette fonction genere la clef de la n ieme rondes
     *
     * @param n la ronde
     * @return la clef de la n ieme ronde
     */
    public int[] genereCle(int n) {
        int[] permute = permutation(generePermutation(masterKey.length), masterKey);
        int[] K1 = new int[masterKey.length - 8];
        System.arraycopy(permute, 0, K1, 0, K1.length);
        int[][] blocs = decoupage(K1, 2);
        assert blocs != null;
        int[] G = blocs[0];
        int[] D = blocs[1];
        G = decalle_gauche(G, tab_decalage[n]);
        D = decalle_gauche(D, tab_decalage[n]);
        int[] K2 = recollage_bloc(new int[][]{G, D});
        int[] permute2 = permutation(generePermutation(K2.length), K2);
        int[] Kn = new int[permute2.length - 8];
        System.arraycopy(permute2, 0, Kn, 0, Kn.length);
        tab_cles[n] = Kn;
        return Kn;
    }

    /**
     * Cette fonction transforme un chiffre a 6 bits en chiffre a 4 bits grace au tableau S
     *
     * @param tab le chiffre a 6bits
     * @return le chiffre a 4bits
     */
    public int[] fonction_S(int[] tab) {
        int ligne = Integer.parseInt("" + tab[0] + tab[5], 2);//premier et dernier bits pour la ligne
        int colonne = Integer.parseInt("" + tab[1] + tab[2] + tab[3] + tab[4], 2);//tout les autres bits pour la collone
        String reponse = Integer.toBinaryString(S[ligne * 16 + colonne]);
        int[] s = new int[4];
        for (int i = 0; i < reponse.length(); i++)
            s[i + 4 - reponse.length()] = Integer.parseInt(reponse.charAt(i) + "");
        return s;
    }

    /**
     * Cette fonction F transforme un bloc a partir d'une cle
     *
     * @param uneCle la clef
     * @param unD    le bloc
     * @return le bloc modifier a partir de la clef
     */
    public int[] fonction_F(int[] uneCle, int[] unD) {
        int[] E1 = new int[48];
        for (int i = 0; i < 48; i++) E1[i] = E[i] - 1;
        int[] Dprime = permutation(E1, unD);
        int[] rep = xor(Dprime, uneCle);
        int[][] couper = decoupage(rep, 8);
        int[][] S1 = new int[8][];
        for (int i = 0; i < 8; i++) {
            assert couper != null;
            S1[i] = fonction_S(couper[i]);
        }
        return recollage_bloc(S1);
    }

    /**
     * Cette fonction crypte avec l'algorythme DES en 1 ronde
     *
     * @param message_clair le message a crypter
     * @return la tableau de bits crypter
     */
    public int[] crypteVersion1(String message_clair) {
        nbEspace = 0;// le nombre d'espace ajouter pour avoir un ou plusieurs bloc de 64 bits
        tab_cles = new int[16][48];//meme si il y a une seul clef
        StringBuilder message_clairBuilder = new StringBuilder(message_clair);
        while (message_clairBuilder.length() % 8 != 0) {
            message_clairBuilder.append(" ");
            nbEspace += 1;
        }
        message_clair = message_clairBuilder.toString();
        int[][] blocs = decoupage(stringToBits(message_clair), message_clair.length() / 8);//on decoupe en bloc de 64bits
        int[][] crypte = new int[message_clair.length() / 8][];//va contenir le message crypter
        int[] K0 = genereCle(0);//la cle utiliser
        tab_cles[0] = K0;//pour la sauvegarder
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {//parcourt de tous les blocs
            int[] bloc2 = permutation(perm_initiale, blocs[i]);//permutation initial
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G0 = decoupe[0];
            int[] D0 = decoupe[1];
            int[] D1 = xor(G0, fonction_F(K0, D0));
            int[] recollage = recollage_bloc(new int[][]{D0, D1});// on recole les blocs modifier
            crypte[i] = invPermutation(perm_initiale, recollage);//permutation inverse
        }
        return recollage_bloc(crypte);
    }

    /**
     * Cette fonction decrypte un message code avec l'algorythme DES en 1 ronde
     *
     * @param messageCode le tableau de bits crypter
     * @return le message code decrypter en String
     */
    public String decrypteVersion1(int[] messageCode) {
        int[][] blocs = decoupage(messageCode, messageCode.length / taille_bloc);//on redecoupe en bloc de 64bits
        int[][] decrypte = new int[messageCode.length / taille_bloc][];//va conternir le message decrypter
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {//parcourt de tous les blocs en applicant l'algorythme a l'envers
            int[] bloc2 = permutation(perm_initiale, blocs[i]);
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G1 = decoupe[0];
            int[] D1 = decoupe[1];
            int[] K0 = tab_cles[0];
            int[] G0 = xor(D1, fonction_F(K0, G1));
            int[] recollage = recollage_bloc(new int[][]{G0, G1});
            decrypte[i] = invPermutation(perm_initiale, recollage);
        }
        String message = bitsToString(recollage_bloc(decrypte));
        message = message.substring(0, message.length() - nbEspace);//on oublie pas de supprimer les espaces ajouter avec le cryptage
        return message;
    }

    /**
     * Cette fonction crypte avec l'algorythme DES en 16 rondes
     *
     * @param message_clair le message a crypter
     * @return la tableau de bits crypter
     */
    public int[] crypteVersion2(String message_clair) {
        nbEspace = 0;
        tab_cles = new int[16][48];
        tableauPermutationS = new ArrayList<>();
        StringBuilder message_clairBuilder = new StringBuilder(message_clair);
        while (message_clairBuilder.length() % 8 != 0) {
            message_clairBuilder.append(" ");
            nbEspace += 1;
        }
        message_clair = message_clairBuilder.toString();
        int[][] blocs = decoupage(stringToBits(message_clair), message_clair.length() / 8);
        int[][] crypte = new int[message_clair.length() / 8][];
        for (int i = 0; i < 16; i++) {
            tab_cles[i] = genereCle(i);//on genere 16 clef au debut
            int[] perm = generePermutation(64);
            tableauPermutationS.add(permutation(perm, S));//on genere les variation du tableau S
        }
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {
            int[] bloc2 = permutation(perm_initiale, blocs[i]);
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G0 = decoupe[0];
            int[] D0 = decoupe[1];
            int[] D1 = new int[taille_sous_bloc];
            int[] G1 = new int[taille_sous_bloc];
            for (int j = 0; j < 16; j++) {
                S = tableauPermutationS.get(j);//le tableau S evolue
                D1 = xor(G0, fonction_F(tab_cles[j], D0));
                G1 = D0;
                G0 = G1;//Gn devient Gn+1 pour la prochaine ronde(Gn)
                D0 = D1;//Dn devient Dn+1 pour la prochaine ronde(Dn)
            }
            int[] recollage = recollage_bloc(new int[][]{G1, D1});
            crypte[i] = invPermutation(perm_initiale, recollage);
        }
        return recollage_bloc(crypte);
    }

    /**
     * cette fonction crypte en DES 16 ronde avec des clef existante pour le Triple DES
     *
     * @param message_clair le message a crypter
     * @return la tableau de bits crypter
     */
    public int[] crypteVersion2PourTDES(String message_clair) {
        nbEspace = 0;
        StringBuilder message_clairBuilder = new StringBuilder(message_clair);
        while (message_clairBuilder.length() % 8 != 0) {
            message_clairBuilder.append(" ");
            nbEspace += 1;
        }
        message_clair = message_clairBuilder.toString();
        int[][] blocs = decoupage(stringToBits(message_clair), message_clair.length() / 8);
        int[][] crypte = new int[message_clair.length() / 8][];
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {
            int[] bloc2 = permutation(perm_initiale, blocs[i]);//on utilise un permutation existante.
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G0 = decoupe[0];
            int[] D0 = decoupe[1];
            int[] D1 = new int[32];
            int[] G1 = new int[32];
            for (int j = 0; j < 16; j++) {
                S = tableauPermutationS.get(j);
                D1 = xor(G0, fonction_F(tab_cles[j], D0));
                G1 = D0;
                G0 = G1;
                D0 = D1;
            }
            int[] recollage = recollage_bloc(new int[][]{G1, D1});
            crypte[i] = invPermutation(perm_initiale, recollage);
        }
        return recollage_bloc(crypte);
    }

    /**
     * Cette fonction decrypte un message code avec l'algorythme DES en 16 rondes
     *
     * @param messageCode le tableau de bits crypter
     * @return le message code decrypter en String
     */
    public String decrypteVersion2(int[] messageCode) {
        int[][] blocs = decoupage(messageCode, messageCode.length / taille_bloc);
        int[][] decrypte = new int[messageCode.length / taille_bloc][];
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {
            int[] bloc2 = permutation(perm_initiale, blocs[i]);//on recupere les permutation du bloc crypter
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G1 = decoupe[0];
            int[] D1 = decoupe[1];
            int[] G0 = new int[taille_sous_bloc];
            int[] D0 = new int[taille_sous_bloc];
            for (int j = 15; j >= 0; j--) {//l'algorythme en 16 rondes a l'envers
                S = tableauPermutationS.get(j);
                D0 = G1;
                G0 = xor(D1, fonction_F(tab_cles[j], D0));
                G1 = G0;
                D1 = D0;
            }
            int[] recollage = recollage_bloc(new int[][]{G0, D0});
            decrypte[i] = invPermutation(perm_initiale, recollage);
        }
        String message = bitsToString(recollage_bloc(decrypte));
        message = message.substring(0, message.length() - nbEspace);
        return message;
    }

    /**
     * Cette fonction decrypte un message code avec l'algorythme DES en 16 rondes sans supprimer de bits a la fin pour le TDES
     *
     * @param messageCode le tableau de bits crypter
     * @return le message code decrypter en String
     */
    public String decrypteVersion2PourTDES(int[] messageCode) {
        int[][] blocs = decoupage(messageCode, messageCode.length / 64);
        int[][] decrypte = new int[messageCode.length / 64][];
        for (int i = 0; i < Objects.requireNonNull(blocs).length; i++) {
            int[] bloc2 = permutation(perm_initiale, blocs[i]);
            int[][] decoupe = decoupage(bloc2, 2);
            assert decoupe != null;
            int[] G1 = decoupe[0];
            int[] D1 = decoupe[1];
            int[] G0 = new int[32];
            int[] D0 = new int[32];
            for (int j = 15; j >= 0; j--) {
                S = tableauPermutationS.get(j);
                D0 = G1;
                G0 = xor(D1, fonction_F(tab_cles[j], D0));
                G1 = G0;
                D1 = D0;
            }
            int[] recollage = recollage_bloc(new int[][]{G0, D0});
            decrypte[i] = invPermutation(perm_initiale, recollage);
        }
        return bitsToString(recollage_bloc(decrypte));
    }

    /**
     * Cette fonction crypte avec l'algorythme TDES, il suit l'algorythme:C"K3"(D"K2"(C"K1")))
     *
     * @param message_clair le message a crypter
     * @return la tableau de bits crypter
     */
    public int[] crypteVersion3(String message_clair) {
        tableauTripleDes = new ArrayList<>();//pour enregistrer toutes les cles et permutations
        masterKey = Key1;
        int[] crypteK1 = crypteVersion2(message_clair);//genere tab_cles,tableauPermutation,tableauPermutationS,nbEspace avec master clef 1
        tableauTripleDes.add(tab_cles);
        tableauTripleDes.add(tableauPermutationS);
        tableauTripleDes.add(nbEspace);
        masterKey = Key2;
        tab_cles = new int[16][48];//on genere de nouvelles clef avec la master clef 2
        for (int i = 0; i < 16; i++) {
            tab_cles[i] = genereCle(i);
        }
        String decrypteK2 = decrypteVersion2PourTDES(crypteK1);//decrypte avec les nouvelles clef de master clef 2
        tableauTripleDes.add(tab_cles);
        masterKey = Key3;
        return crypteVersion2(decrypteK2);//crypte avec la master clef 3
    }

    /**
     * Cette fonction decrypte un message code avec l'algorythme TDES, il suit l'algorythme:D"K1"(C"K2"(D"K3")))
     *
     * @param messageCode le tableau de bits crypter
     * @return le message code decrypter en String
     */
    public String decrypteVersion3(int[] messageCode) {
        String decrypteK3 = decrypteVersion2PourTDES(messageCode);//on decrypte le message code avec les clef de la master clef 3
        tab_cles = (int[][]) tableauTripleDes.get(3);
        tableauPermutationS = (ArrayList<int[]>) tableauTripleDes.get(1);
        int[] crypteK2 = crypteVersion2PourTDES(decrypteK3);// on cypte avec les clef de la master clef 2
        tab_cles = (int[][]) tableauTripleDes.get(0);
        tableauPermutationS = (ArrayList<int[]>) tableauTripleDes.get(1);
        nbEspace = (int) tableauTripleDes.get(2);
        return decrypteVersion2(crypteK2);// on decrypte avec les clef de la master clef 1
    }
}


