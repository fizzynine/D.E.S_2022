package fr.antoben.desapp;

import java.util.Arrays;

public class TestDes {

    /*

    Pour texte la classe test, il faut exécuter uniquement cette classe et pas le projet entier car
    ça va ouvrir l'interface graphique...

    */


    public static void print(String title, int[]... tab) {
        StringBuilder s = new StringBuilder(title + " : ");
        for (int[] tabb : tab) {
            s.append("\n").append(Arrays.toString(tabb));
        }
        System.out.println(s);
    }
    public static void main(String[] args) {
        Des d = new Des();
        //System.out.println(Arrays.toString(masterKey));
        String x = "    DHFGFHHFD  ./EGGGH erz'(\n-(è-îghipgjohk";
        int[] bloc = d.stringToBits(x);
        print("Test fonction stringToBits", bloc);
        System.out.println();
        System.out.println("Test fonction bitsToString :\n" + d.bitsToString(bloc));
        System.out.println();
        int[] tab_permutation = d.generePermutation(bloc.length);
        int[] p = d.permutation(tab_permutation, bloc);
        print("Test fonction generePermutation", tab_permutation);
        System.out.println();
        print("Test fonction permutation", p);
        System.out.println();
        print("Test fonction invPermutation", d.invPermutation(tab_permutation, p));
        System.out.println();
        print("Test fonction xor",
                d.xor(new int[]{1}, new int[]{0})
                , d.xor(new int[]{0}, new int[]{1})
                , d.xor(new int[]{0}, new int[]{0})
                , d.xor(new int[]{1}, new int[]{1})
                , d.xor(new int[]{1, 0, 1}, new int[]{1, 1, 1}));
        int[] tab = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
        System.out.println();
        print("Test fonction decoupage", d.decoupage(tab, 6));
        int[][] blocss = d.decoupage(tab, 6);
        System.out.println();
        print("Test fonction recollage_bloc", d.recollage_bloc(blocss));
        System.out.println();
        print("Test fonction decalle_gauche", d.decalle_gauche(tab, 3));
        //print("",decalle_gauche2(tab,3));
        //print("",d.genereCle(0));
        System.out.println();
        print("Test fonction S", d.fonction_S(new int[]{1, 1, 1, 1, 1, 1}));
        System.out.println();
        print("Test fonction S", d.fonction_S(new int[]{0, 0, 0, 0, 0, 1}));
        System.out.println();
        print("Test fonction permutation pour un tableau de permutation plus grand que la longueur du bloc", d.permutation(new int[]{0, 1, 2, 3, 0, 1, 0, 3}, new int[]{0, 1, 2, 3}));
        System.out.println();
        //print("F",d.fonction_F(d.genereCle(0),d.decoupage(bloc,2)[1]));
        String code = "    DHFGFHHFD  ./EGGGH erz'(\n-(è-îghipgjohk";
        System.out.println("Le message a crypter est :\n" + code);
        System.out.println();
        //String code="aples";
        int[] crypte = d.crypteVersion1(code);
        System.out.println("Test de cryptage DES pour 1 ronde :\n" + d.bitsToString(crypte));
        System.out.println();
        System.out.println("Test de decryptage DES pour 1 ronde :\n" + d.decrypteVersion1(crypte));
        System.out.println();
        int[] crypte2 = d.crypteVersion2(code);
        System.out.println("Test de cryptage DES pour 16 rondes :\n" + d.bitsToString(crypte2));
        System.out.println();
        String decode = d.decrypteVersion2(crypte2);
        System.out.println("Test de decryptage DES pour 16 rondes :\n" + decode);
        System.out.println();
        //System.out.println(d.bitsToString(d.crypteVersion2PourTDES(decode)));
        int[] crypte3 = d.crypteVersion3(code);
        System.out.println("Test de cryptage TripleDES :\n" + d.bitsToString(crypte3));
        System.out.println();
        System.out.println("Test de decryptage TripleDES :\n" + d.decrypteVersion3(crypte3));
    }
}