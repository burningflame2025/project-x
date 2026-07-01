package com.example.demo1.Model;

//=========normal user======
public class NormalUser extends User {

    public NormalUser(String username, String password, String email, String phone,
                      String firstName, String lastName) {
        super(username, password, email, phone, firstName, lastName);
        this.setTokens(250);
    }

    @Override
    public int calculatePostCost(String text, boolean hasMedia) {
        int cost = text.length();
        if (hasMedia) {
            cost += 10;
        }
        return Math.max(cost, 1);
    }

    @Override
    public int getDailyTokenRecharge() {
        return 250;
    }

    @Override
    public SubscriptionType getSubscriptionType() {
        return SubscriptionType.NONE;
    }

    @Override
    public String getSubscriptionBadge() {
        return "";
    }
}
