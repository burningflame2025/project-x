package com.example.demo1.Model;

//======blueUser=====
public class BlueUser extends PremiumUser {

    public BlueUser(String username, String password, String email) {
        super(username, password, email, phone, firstName, lastName);
        this.setTokens(400);
    }

    public BlueUser(User normalUser) {
        super(normalUser);
    }

    @Override
    public int calculatePostCost(String text, boolean hasMedia) {
        int cost = text.length() / 2;
        if (hasMedia) {
            cost += 5;
        }
        return Math.max(1, cost);
    }

    @Override
    public int getDailyTokenRecharge() {
        return 400;
    }

    @Override
    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.BLUE;
    }

    @Override
    public String getSubscriptionBadge() {
        return "🔵";
    }
}
