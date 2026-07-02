package com.example.demo1.Model;

//===========gold=======
public class GoldUser extends PremiumUser {

    public GoldUser(String username, String password, String email) {
        super(username, password, email, "", "", "");
        this.setTokens(600);
    }

    public GoldUser(User normalUser) {
        super(normalUser);
    }

    @Override
    public int calculatePostCost(String text, boolean hasMedia) {
        return 5;
    }
    @Override
    public int getDailyTokenRecharge() {
        return 600;
    }

    @Override
    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.GOLD;
    }

    @Override
    public String getSubscriptionBadge() {
        return "⭐";
    }
}
