package polimi.francescopaolobasile.beans;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>Classe test dell'esercizio esame Ingegneria del Software. Chiamare il metodo statico test da un main</p>
 *
 * @author francescobasile
 */
public class NestedList {
    /**
     * <p>Chiamare il metodo statico test da un main </p>
     */
    public static void test() {
        List<List<MyGenericCLass>> A = new ArrayList<>();
        List<List<MyGenericCLass>> B = new ArrayList<>();

        A.add(Arrays.asList(new MyGenericCLass("1"), new MyGenericCLass("2"), new MyGenericCLass("3")));//colonna 0
        A.add(Arrays.asList(new MyGenericCLass("4"), new MyGenericCLass("5"), new MyGenericCLass("6")));//...
        A.add(Arrays.asList(new MyGenericCLass("7"), new MyGenericCLass("8"), new MyGenericCLass("9")));

        B.add(Arrays.asList(new MyGenericCLass("23"), new MyGenericCLass("6"), new MyGenericCLass("0")));
        B.add(Arrays.asList(new MyGenericCLass("1"), new MyGenericCLass("8"), new MyGenericCLass("-3")));
        B.add(Arrays.asList(new MyGenericCLass("10"), new MyGenericCLass("-20"), new MyGenericCLass("9")));

        List<List<Boolean>> C = null;
        try {
            C = comparatoreGreaterMatrici(A, B);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("A:");
        display(traspose(A));// visualizza matrice nel modo usuale
        System.out.println("B:");
        display(traspose(B));
        System.out.println("C:");
        display(traspose(C));
    }

    /**
     * @param A
     * @param B
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public static <T extends MyGenericCLass> List<List<Boolean>> comparatoreGreaterMatrici(List<List<T>> A, List<List<T>> B) throws InterruptedException {
        List<List<Boolean>> C = new ArrayList<>();
        int cols = A.size();
        int rows = A.get(0).size();
//inizializzazione matrice di ritorno a null
        for (int i = 0; i < cols; i++) {
            Boolean[] t = new Boolean[rows];
            for (int j = 0; j < rows; j++) {
                t[j] = null;
            }
            C.add(Arrays.asList(t));
        }
// inizializzazione degli iteratori delle matrici
        Iterator<List<T>> iteratorA = A.iterator(); // iteratore delle colonne contiene a sua volta una lista
        Iterator<List<T>> iteratorB = B.iterator();
        ListIterator<List<Boolean>> iteratorC = C.listIterator(); // iteratoreList sulle colonne di C per poter modificare
//        inizializza array di threads per colonne
        Thread[] threads = new Thread[cols];

//        Avvia un thread in paralleo per ciascuna lista di colonne
        for (int i = 0; i < cols; i++) {
            //passiamo iterator al thread per poter eseguire la comparazione in parallelo
            threads[i] = new Thread(new comparatorThread<>(iteratorA, iteratorB, iteratorC));
            threads[i].setName("threads" + "[" + i + "]");
            threads[i].start();
        }
//        il main thread attende che tutti i threads finiscano
        for (int i = 0; i < cols; i++) {
            threads[i].join();
        }
//        ritorna la matrice di boolean con il risultato del confronto parallelo
        return C;
    }

    /**
     * <p>Restituisce la trsposta di una List di list dove la lista interna rappresenta una colonna di una matrice</p>
     *
     * @param m1  <p>Matrice composta di List</p>
     * @param <T> <p>Generics type parameter</p>
     * @return <b>Ritorna la trasposta della forma matriciale in ingresso</b>
     */
    public static <T> List<List<T>> traspose(List<List<T>> m1) {
        List<List<T>> m2 = new ArrayList<>();
//inizializzazione matrice di ritorno a null
        for (int i = 0; i < m1.size(); i++) {
            List<T> t = new ArrayList<>();
            for (int j = 0; j < m1.get(0).size(); j++) {
                t.add(null);
            }
            m2.add(t);
        }

        int i = 0;
        for (List<T> listColsM1 : m1) {
            ListIterator<List<T>> listListIteratorM2 = m2.listIterator();
            for (T elemM1 : listColsM1) {
                ListIterator<T> t = listListIteratorM2.next().listIterator(i);
                t.next();
                t.set(elemM1);
            }
            i++;
        }
        return m2;
    }

    /**
     * @param m2
     * @param <T>
     */
    public static <T> void display(List<List<T>> m2) {
        for (List<T> listaM2 : m2) {
            for (T elemM2 : listaM2) {
                System.out.print(elemM2 + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}

class MyGenericCLass implements Comparable {
    private final String i;

    public MyGenericCLass(String i) {
        this.i = i;
    }

    public String getI() {
        return i;
    }

    /**
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(@NotNull Object o) {
        Integer i = Integer.parseInt(this.getI());
        Integer o2 = Integer.parseInt(((MyGenericCLass) o).getI());
        return i - o2;
    }

    @Override
    public String toString() {
        return String.valueOf(i);
    }
}

class comparatorThread<T extends MyGenericCLass> implements Runnable {
    private Iterator<List<T>> iteratorA;
    private Iterator<List<T>> iteratorB;
    private ListIterator<List<Boolean>> iteratorC;
    private int size;

    public comparatorThread(Iterator<List<T>> a, Iterator<List<T>> b, ListIterator<List<Boolean>> c) {
        iteratorA = a;
        iteratorB = b;
        iteratorC = c;
    }

    /**
     *
     */
    @Override
    public void run() {
        // inizializzazione degli iteratori delle matrici

//        ListIterator<List<Boolean>> iteratorC = C.listIterator(); // iteratoreList sulle colonne per poter modificare
        System.out.println(Thread.currentThread().getName() + " running!");
        while (iteratorA.hasNext()) { // si suppone che le matrici abbiano stessa dimensione (check non implementato)
            Iterator<T> elemA = iteratorA.next().iterator(); // iteratore ora contiene gli elementi generics
            Iterator<T> elemB = iteratorB.next().iterator();
            ListIterator<Boolean> elemC = iteratorC.next().listIterator();
            while (elemA.hasNext()) {
                elemC.next();
                elemC.set(elemA.next().compareTo(elemB.next()) > 0); // controllo solo su "greater then"
            }
        }
    }
}