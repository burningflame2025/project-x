package com.example.demo1.Model;

import java.util.Date;

public abstract class PremiumUser extends User {

    protected Date subscriptionExpiry;
    protected boolean autoRenew;

    public PremiumUser(String username, String password, String email, String phone, String firstName, String lastName) {

        super(username, password, email, phone, firstName, lastName);
        this.subscriptionExpiry = null;
        this.autoRenew = false;
    }
    public PremiumUser(User user) {
        super(user);
        this.autoRenew = false;
        this.subscriptionExpiry = null;
    }

    public void activateSubscription(int months) {
        Date now = new Date();
        if (subscriptionExpiry == null || subscriptionExpiry.before(now)) {
            subscriptionExpiry = now;
        }
        long millis = months * 30L * 24 * 60 * 60 * 1000;
        subscriptionExpiry = new Date(subscriptionExpiry.getTime() + millis);
    }

    public void extendSubscription(int months) {
        activateSubscription(months);
    }

    public boolean isSubscriptionValid() {

        if (subscriptionExpiry == null) {
            return false;
        }
        return subscriptionExpiry.after(new Date());
    }
    public int getDaysRemaining() {

        if (!isSubscriptionValid()) {
            return 0;
        }

        long diff = subscriptionExpiry.getTime() - System.currentTimeMillis();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public Date getSubscriptionExpiry() {
        return subscriptionExpiry;
    }

    public void setSubscriptionExpiry(Date subscriptionExpiry) {
        this.subscriptionExpiry = subscriptionExpiry;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}