package InheritanceProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester {
    public static void main(String[] args) {
        GameWorld gameWorld = new GameWorld();

        Monster cloakedZombie = new Zombie();
        gameWorld.addMonster(cloakedZombie);
        Monster cloakedEliteZombie = new EliteZombie();
        gameWorld.addMonster(cloakedEliteZombie);

        Monster alias1 = new Monster();
        gameWorld.addMonster(alias1);
        Monster alias2 = new Zombie();
        gameWorld.addMonster(alias2);


        Monster monster = new Monster();
        gameWorld.addMonster(monster);
        Monster cloakedZombie2 = new Zombie();
        gameWorld.addMonster(cloakedZombie2);
        Monster cloakedEliteZombie2 = new EliteZombie();
        gameWorld.addMonster(cloakedEliteZombie2);
        Monster alias3 = alias1;
        gameWorld.addMonster(alias3);
        Monster alias4 = alias2;
        gameWorld.addMonster(alias4);


        Monster cloakedZombie3 = new Zombie();




        System.out.println("Initial game state:");

        gameWorld.displayMonsters();

    }


    public static class EliteZombie extends Zombie{
        private double infectionRate;

        public EliteZombie(){
            super("unknown elite zombie", 175, 50, new double[2], 2);
            setDecayRate(0.01);
            setResurrectionChance(0.25);
            this.infectionRate = 0.3;
        }

        public EliteZombie(String name, double health, double damage, double[] position, int range){
            super(name, health, damage, position, range);
            setSpeed(2);
            setDecayRate(0.01);
            setResurrectionChance(0.25);
            this.infectionRate = 0.3;
        }

        public void infect(Monster other) {
            if (Math.random() < infectionRate && !(other instanceof Zombie)) {
                gameWorld.replaceWithZombie(other, new Zombie("Zombie " + other.getName(), other.getHealth(), other.getDamage(), other.getPosition(), 1));

            }
        }

        @Override
        public boolean attack(Monster other) {
            if(super.attack(other)){
                infect(other);
                return true;
            }
            return false;
        }

        @Override
        public void resurrect() {
            if (Math.random() < getResurrectionChance()) {
                System.out.println("The Zombie resurrects!");
                setHealth(getMaxHP() / 2);
                setResurrectionChance(getResurrectionChance() - 0.05);
            }
        }

        @Override
        public String toString() {
            return "##Elite## " + super.toString();
        }
    }

    public static class GameWorld {
        private List<Monster> monsters = new ArrayList<>();

        public void addMonster(Monster monster) {
            monsters.add(monster);
            monster.setGameWorld(this);
        }

        public void replaceWithZombie(Monster target, Zombie newZombie) {
            if (monsters.contains(target)) {
                int index = monsters.indexOf(target);
                newZombie.setGameWorld(this);
                monsters.set(index, newZombie);
                System.out.println(target.getName() + " has been turned into a zombie!");
            }
        }

        public void displayMonsters() {
            StringBuilder output;
            for (Monster monster : monsters) {
                System.out.println(monster);
            }

        }

    }

    public static class Monster {
        protected GameWorld gameWorld;
        private String name;
        private double health;
        private final double maxHP;
        private double damage;
        private int range;
        private double[] position;
        private boolean alive;
        private double speed;

        public Monster(){
            this.name = "unknown";
            this.health = 100;
            this.damage = 1;
            this.position = new double[2];
            this.alive = true;
            this.range = 1;
            this.maxHP = health;
            this.speed = 5;
        }

        public Monster(String name, double health, double damage, double[] position, int range, double speed){
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.position = position;
            this.alive = true;
            this.range = range;
            this.maxHP = health;
            this.speed = speed;
        }

        public boolean attack(Monster other){
            if(Util.EuclideanDistance(position, other.getPosition()) < range){
                other.setHealth(other.getHealth() - damage);
                return true;
            }

            return false; //attack failed
        }
        public String toString() {
            return "name='" + name + '\'' +
                    ", health=" + health +
                    ", damage=" + damage +
                    ", range=" + range +
                    ", position=" + Arrays.toString(position) +
                    ", alive=" + alive;
        }
        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            if(alive) {
                this.health = health;
                if(health < 0) {
                    alive = false;
                    this.health = 0;
                }
            }
        }

        public boolean move(double[] target) {
            if(Util.EuclideanDistance(position, target) < speed){
                this.position = target;
                return true;
            } else {
                double ratio = speed / Util.EuclideanDistance(position, target);
                position[0] += ratio*(target[0] - position[0]);
                position[1] += ratio*(target[1] - position[1]);
                return false;
            }
        }

        public String getName() {
            return name;
        }

        public double getDamage() {
            return damage;
        }

        public double[] getPosition() {
            return position;
        }

        public void setDamage(double damage) {
            this.damage = damage;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isAlive() {
            return alive;
        }

        public double getMaxHP() {
            return maxHP;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getSpeed() {
            return speed;
        }

        public void setGameWorld(GameWorld gameWorld) {
            this.gameWorld = gameWorld;
        }
    }

    public static class Util {
        public static double EuclideanDistance(double[] first, double[] second){
            double sum = 0;
            if(first.length == second.length){
                for(int i = 0; i < first.length; i++){
                    sum += Math.pow((first[i]-second[i]), 2);
                }

                return Math.sqrt(sum);
            }
            return -1;
        }

    }

    public static class Zombie extends Monster {

        private double decayRate;
        private double resurrectionChance;

        public Zombie(){
            super("unknown zombie", 100, 2.5, new double[2], 1, 1.6);
            this.decayRate = 0.05;
            this.resurrectionChance = 0.1;
        }

        public Zombie(String name, double health, double damage, double[] position, int range){
            super(name, health, damage, position, range, 1.6);
            this.decayRate = 0.05;
            this.resurrectionChance = 0.1;
        }

        public void decay() {
            setHealth(getHealth()*(1-decayRate));
            setDamage(getDamage()*(1-decayRate));
            System.out.println("Decay applied to Zombie.");
        }

        public void resurrect() {
            if (Math.random() < resurrectionChance) {
                System.out.println("The Zombie resurrects!");
                setHealth(getMaxHP() / 10);
                resurrectionChance -= 0.05;
            }
        }

        @Override
        public String toString() {
            return "Type: Zombie " +
                    super.toString() +
                    " decayRate=" + decayRate +
                    ", resurrectionChance=" + resurrectionChance;
        }

        @Override
        public void setHealth(double health) {
            super.setHealth(health);
            if(!isAlive()){
                resurrect();
            }
        }

        public void setDecayRate(double decayRate) {
            this.decayRate = decayRate;
        }

        public void setResurrectionChance(double resurrectionChance) {
            this.resurrectionChance = resurrectionChance;
        }

        public double getDecayRate() {
            return decayRate;
        }

        public double getResurrectionChance() {
            return resurrectionChance;
        }
    }
}
