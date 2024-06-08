package org.coder.design.patterns._1_oop._3_inheritance.bird;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Sparrow implements Flyable, Tweetable, EggLayable { // 麻雀

    private TweetAbility tweetAbility = new TweetAbility();
    private EggLayAbility eggLayAbility = new EggLayAbility();
    private FlyAbility flyAbility = new FlyAbility();

    @Override
    public void layEgg() {
        eggLayAbility.layEgg();
    }

    @Override
    public void fly() {
        flyAbility.fly();
    }

    @Override
    public void tweet() {
        tweetAbility.tweet();
    }
}
