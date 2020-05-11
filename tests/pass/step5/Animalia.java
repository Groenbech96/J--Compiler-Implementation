// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package pass.step5;

import java.lang.System;

abstract class Animal {

    protected String scientificName;

    protected Animal(String scientificName) {
        this.scientificName = scientificName;
    }

    public String scientificName() {
        return scientificName;
    }

}

class FruitFly extends Animal {

    public FruitFly() {
        super("Drosophila melanogaster");
    }

}

class Tiger extends Animal {

    public Tiger() {
        super("Panthera tigris corbetti");
    }

}

public class Animalia {

    public static String getFly() {
        Animal fruitFly = new FruitFly();
        return fruitFly.scientificName();
    }
    public static String getTiger() {
        Animal tiger = new Tiger();
        return tiger.scientificName();
    }

}
