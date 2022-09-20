package polimi.francescopaolobasile;

import polimi.francescopaolobasile.concorrenza.CounterTest;
import polimi.francescopaolobasile.designpatterns.factory.AbstractFactoryTest;
import polimi.francescopaolobasile.generics.GenericsTest;
import polimi.francescopaolobasile.rndgame.MatchTest;

public class Main {

    public static void main(String[] args) {
//        MatchTest.Test();
//        GenericsTest.Test();
//        CounterTest.testJoin();
        AbstractFactoryTest abstractFactoryTest = new AbstractFactoryTest();
        abstractFactoryTest.setUp();
    }
}
